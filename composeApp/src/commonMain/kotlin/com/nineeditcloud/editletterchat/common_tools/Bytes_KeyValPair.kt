package com.nineeditcloud.editletterchat.common_tools

/*纯Kotlin-stdlib标准库实现，适合TCP长连接的 字节串(二进制)键值对(值可加入图片二进制数据)*/

/*========== 大端字节序工具 ==========*/
/*转大端字节序 字节串(二进制)数组*/
fun Short.toBigEndianBytes()=byteArrayOf(
    (toInt() shr 8 and 0xFF).toByte(),
    (toInt() and 0xFF).toByte(), )
fun Int.toBigEndianBytes()=byteArrayOf(
    (this shr 24 and 0xFF).toByte(),
    (this shr 16 and 0xFF).toByte(),
    (this shr 8 and 0xFF).toByte(),
    (this and 0xFF).toByte(), )
/*读 大端字节序*/
fun ByteArray.readBigEndianShort(offset:Int=0):Short{
    val high=this[offset].toInt() and 0xFF
    val low=this[offset + 1].toInt() and 0xFF
    return ((high shl 8) or low).toShort()
}
fun ByteArray.readBigEndianInt(offset:Int=0):Int{
    val b0=this[offset].toInt() and 0xFF
    val b1=this[offset + 1].toInt() and 0xFF
    val b2=this[offset + 2].toInt() and 0xFF
    val b3=this[offset + 3].toInt() and 0xFF
    return (b0 shl 24) or (b1 shl 16) or (b2 shl 8) or b3
}

/*========== CRC32 纯Kotlin实现(跨平台) ==========*/
object CRC32{
    private val table=IntArray(256).apply {
        for (i in 0..255) {
            var crc=i
            for (j in 0..7) {
                crc=if (crc and 1 != 0) (crc ushr 1) xor 0xEDB88320.toInt() else crc ushr 1
            }
            this[i]=crc
        }
    }

    fun compute(data:ByteArray, offset:Int=0, length:Int=data.size - offset):Int {
        var crc=0xFFFFFFFF.toInt()
        for (i in offset until offset + length) {
            val index=(crc xor (data[i].toInt() and 0xFF)) and 0xFF
            crc=(crc ushr 8) xor table[index]
        }
        return crc xor 0xFFFFFFFF.toInt()
    }
}

/*========== 消息构建器 ==========*/
class MessageBuilder{
    private val chunks=mutableListOf<ByteArray>()/*用于构建 字节串数组的 (mutableListOf)可写有序集合*/
    fun addText(key:String, text:String){
        addEntry(key, text.encodeToByteArray(), 0x01, withChecksum=false)
    }

    /*添加二进制数据(如图片)，自动附上CRC32校验*/
    fun addBinary(key:String, data:ByteArray){
        addEntry(key, data, 0x02, withChecksum=true)
    }

    private fun addEntry(key:String, value:ByteArray, type:Byte, withChecksum:Boolean){
        val keyBytes=key.encodeToByteArray()
        chunks.add(keyBytes.size.toShort().toBigEndianBytes())/*键长度*/
        chunks.add(keyBytes)/*键*/
        chunks.add(byteArrayOf(type) )/*类型*/
        chunks.add(value.size.toBigEndianBytes())/*值长度*/
        chunks.add(value)/*值*/
        if(withChecksum){/*校验和(仅二进制)*/
            val crc=CRC32.compute(value)
            chunks.add(crc.toBigEndianBytes())
        }
    }

    fun buildFrame():ByteArray{
        val body=buildBody()
        val header=body.size.toBigEndianBytes()/*4字节总长度*/
        return header + body
    }

    private fun buildBody():ByteArray{
        val totalSize=chunks.sumOf{ it.size }
        val result=ByteArray(totalSize)
        var offset=0
        for(chunk in chunks){
            chunk.copyInto(result, offset)
            offset+=chunk.size
        }
        return result
    }
}

data class Entry(val key:String, val type:Byte, val value:ByteArray){
    val textValue:String get()=value.decodeToString()
}
/*========== 消息解析器 ==========*/
class MessageReader(private val data:ByteArray){
    private var pos=0/*消息已读取进度*/



    fun readAll():List<Entry>{
        val entries=mutableListOf<Entry>()
        while(pos < data.size){
            entries.add(readNext() )
        }
        return entries
    }

    fun readNext():Entry{
        val keyLen=data.readBigEndianShort(pos).toInt(); pos += 2/*读键长度*/
        val key=data.copyOfRange(pos, pos + keyLen).decodeToString(); pos += keyLen/*读键*/
        val type=data[pos]; pos += 1/*读类型*/
        val valueLen=data.readBigEndianInt(pos); pos += 4/*读值长度*/
        val value=data.copyOfRange(pos, pos + valueLen); pos += valueLen/*读值*/

        if(type == 0x02.toByte() ){/*若是二进制，读并校验 CRC32*/
            val receivedCrc=data.readBigEndianInt(pos); pos += 4
            val computedCrc=CRC32.compute(value)
            if(receivedCrc != computedCrc){
                throw IllegalStateException("图片数据校验失败！key=$key")
            }
        }
        return Entry(key, type, value)
    }


}
fun List<Entry>.toHashMap/*List集合扩展函数 转HashMap键值对集合*/():HashMap<String,ByteArray>{
    val msgMap=hashMapOf<String/*键*/,ByteArray/*值*/>()/*hashMapOf集合对象中存在一个HashMap，创建集合操作对象时声明val常量 依旧能更改其对应HashMap的元素*/
    this.forEach{e->
        msgMap[e.key]=e.value/*在HashMap集合中 对应字段 赋值数据*/
    }
    return msgMap
}

/*========== 帧切割辅助(从TCP流中切出完整帧) ==========*/
class FrameBuffer {
    private var buffer=ByteArray(0)

    fun feed(data:ByteArray):List<ByteArray>{
        buffer += data
        val frames=mutableListOf<ByteArray>()
        while (buffer.size >= 4) {
            val frameLength=buffer.readBigEndianInt(0)
            val totalNeeded=4 + frameLength
            if (buffer.size < totalNeeded) break
            // 提取一帧（不含长度头，只返回 body）
            frames.add(buffer.copyOfRange(4, totalNeeded))
            buffer=buffer.copyOfRange(totalNeeded, buffer.size)
        }
        return frames
    }
}

val frameBuffer=FrameBuffer()
/*每次收到 socket 数据时调用*/
fun onDataReceived(data:ByteArray){
    val frames=frameBuffer.feed(data)
    for(body in frames){
        try{
            val reader=MessageReader(body)
            for(entry in reader.readAll() ){
                when(entry.key){
                    "image" ->{
                        /*图片已通过 CRC32校验，可以直接保存或显示*/
//                        saveImage(entry.value)
                        println("收到图片，大小=${entry.value.size}，校验通过")
                    }
                    "userId" -> println("用户ID: ${entry.textValue}")
                }
            }
        }catch(e:Exception){/*校验失败或解析错误，丢弃该帧*/
            println("消息损坏，已丢弃: ${e.message}")
        }
    }
}


