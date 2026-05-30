package com.nineeditcloud.editletterchat.client
import com.nineeditcloud.editletterchat.common_tools.Log
import com.nineeditcloud.editletterchat.common_tools.MessageBuilder
import com.nineeditcloud.editletterchat.common_tools.MessageReader
import com.nineeditcloud.editletterchat.common_tools.MessageReader.Entry
import com.nineeditcloud.editletterchat.common_tools.deviceType
import com.nineeditcloud.editletterchat.common_tools.readBigEndianInt
import com.nineeditcloud.editletterchat.common_tools.toData
import com.nineeditcloud.editletterchat.common_tools.toHashMap
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException

/**自动重连的 TCP长连接客户端(简化版)
 * @param account 认证账号
 * @param token 认证令牌
 * @param 某OnMessage 收到消息时的回调(运行在协程所在线程，非主线程)
 */
fun tcpLongConnClient(account:String, token:String, onHashMapMessage/*收到哈希表字符串键值对消息回调*/:(HashMap<String,String>)->Unit,
                      onBytesMessage/*收到字节串键值对消息回调*/:(List<Entry>)->Unit )=runBlocking{
    val host="192.168.1.47";val port=9000
    while(coroutineContext.isActive){
        try{
            aSocket(SelectorManager(Dispatchers.IO) ).tcp().connect(host, port)
                .use{ socket ->
                    /*.use是个扩展函数，它会自动关闭资源(类似Java的try-with-resources)，所以即使内部抛出异常，use块结束后、退出作用域时 socket都会被关闭，对应的输入输出通道也会关闭。因此不会有多余遗留内容，资源会自动清理*/
                    Log.msg("Ktor已连接TCP长连接服务器：", "$host:$port")
                    val input=socket.openReadChannel()/*输入流通道(接收)*/
                    val output=socket.openWriteChannel(autoFlush=true)/*输出流通道(发送)，autoFlush=true 启用自动刷新(flush)，适合场景是 连续多次写入、控制缓冲区、流式写入大文件，获取反馈值场景一次性用完后建议close()释放*/
                    /*旧版socket.write发送字节流帧和socket.openWriteChannel的却别，一次性发送整包数据(如一段消息帧)，socket.write(frame)内部就是openWriteChannel的极简封装 本质上都是把ByteArray拷贝到TCP发送缓冲区 然后由操作系统发送，socket.write只是少写一个中间channel对象
                    两者最终都会触发一次系统调用(writev) 带宽利用率、CPU 开销、内存拷贝次数相同，autoFlush=true的channel写完后也会立即flush，和write的行为一致
                    若场景是 构建完整消息帧 → 一次性发出，用socket.write(frame)完全足够 代码最少，两者 性能几乎无区别 接收端处理上完全一样 毫无差异，
                    Ktor3.x中无socket.write(字节串数组对象)方法*/


                    val authMsg=OnlineAccountAuthRequest(account,token,deviceType() ).toData()/*自定义键值对 认证消息*/
                    output.writeStringUtf8(authMsg)/*发送认证消息*/
                    val authResponse=input.readUTF8Line()/*读取字符串认证响应*/ ?:throw Exception("认证响应为空")/*若读取响应为空，抛出异常触发自动重连*/
//                    require(authResponse.contains("\"auth_ok\"")/*正常条件*/ ){/*若不达成正常条件 则接收此块内异常信息并抛出异常*/
//                        "认证失败: $authResponse"/*异常信息*/
//                    }
                    if(authResponse.contains("auth_ok") )/*若包含认证成功内容*/ Log.msg("Ktor-TCP长连接", "账号令牌认证成功")
                    else throw IllegalArgumentException("账号令牌认证失败: $authResponse")/*抛出异常信息*/

                    /*---------- 用户向服务器发送 传给好友 带图片的消息：两方账号ID + 消息+图片 ----------*/
//                    val imageBytes=java.io.File("avatar.jpg").readBytes()/*获取图片字节串，实际开发中用FileKit跨平台框架 获取图片并读取字节串*/
//                    val bytesBuilder=MessageBuilder().apply{
//                        addText("userID", "1")/*发送者键值对*/
//                        addText("friendID", "2")/*接收者键值对*/
//                        addText("msg","消息内容")/*消息键值对*/
//                        addBinary("image", imageBytes)/*消息附带图片键值对，自动附加CRC32*/
//                    }.buildFrame()/*将MessageBuilder对象中 字节串键值对数组的集合 构建为集中的键值对数组*/
//                    output.writeByteArray(bytesBuilder)/*发送字节串数组*/

                    while(coroutineContext.isActive){/*循环等待接收服务端消息*/
//                        val line=input.readUTF8Line()/*以字符流读取 服务端反馈的一条消息*/ ?:break/*若接收结果为空 说明服务端断开，跳出接收循环*/
                        /*未跳出情况下执行以下代码*/
//                        onHashMapMessage(line.toHashMap()/*将消息转为哈希表键值对*/ )/*返回哈希表字符串键值对消息回调*/
//                        Log.msg("Ktor-TCP长连接", "收到服务器反馈：$line")

                        val headerBytes=ByteArray(4)/*4字节读取长度头*/
                        val readCount=input.readAvailable(headerBytes, 0, 4)/*读取可用数据大小*/
                        if(readCount<4) break /*若可用数据大小<4，认为服务端断开连接，跳出接收循环*/
                        val frameLen=headerBytes.readBigEndianInt()/*读大端字节序*/
                        if(frameLen<=0) continue /*无效帧*/
                        val body=input.readByteArray(frameLen)/*以字节流读取(挂起直到读满frameLen字节) 服务端反馈的消息体*/ ?:break/*若接收结果为空 说明服务端断开，跳出接收循环*/
                        val entries=MessageReader(body).readAll()/*解析*/
                        onBytesMessage(entries)
                    }
                }
        }catch(e:CancellationException){
            Log.e("Ktor-TCP长连接", "连接异常", e)
            throw e/*正常取消时直接抛出，不吞掉取消*/
        }catch(e:Exception){
//            if(e is CancellationException) throw e
            if(e is IllegalArgumentException) Log.e("Ktor-TCP长连接", e.message.toString(), e)
            Log.e("Ktor-TCP长连接", "连接异常", e)
            if(coroutineContext.isActive) delay(5_000)/*连接异常、读写异常等，连接异常后等5秒自动重连(防止连续异常时 重连太频繁造成服务端压力)*/
        }
    }
}

fun main(){
//    startTcpLongConnClient()/*调用*/ /*.join()*//*等待长连接协程结束(显式等待返回的Job 替代delay)，它通常是永不结束的*/
//    delay(Long.MAX_VALUE)/*保持进程运行*/
    /*CoroutineScope.startTcpLongConnClient方案，在CoroutineScope中调用，返回Job 可用于取消
    旧代码要用delay(等待函数) 保持runBlocking(协程)进程 原因：协程的父子关系被切断
    startTcpLongConnClient 内部使用 launch(Dispatchers.IO + SupervisorJob() ) 启动了一个新协程
    由于显式指定了 SupervisorJob()，这个新协程的Job 会替换掉 从runBlocking继承的父Job，因此它不再是 runBlocking作用域的子协程

    runBlocking的默认行为 会等待它自己的Job中 所有直接子协程完成才会退出
    但这里启动的长连接协程并非其子协程，所以 runBlocking 在调用 startTcpLongConnClient() 后没有其他任务，会立即返回，导致进程退出

    若删除 delay(Long.MAX_VALUE)，main 函数执行流程如下：
    启动长连接协程(它独立于 runBlocking 运行)
    runBlocking 执行完毕，退出
    整个 JVM 进程结束，所有非守护协程被强制终止 → 长连接协程还没来得及建立连接就被杀死*/
    /*onMessage:(String)->Unit={ println("收到: $it") } 可用于调用时或自身传参 转入代码中执行的动态参数*/

}

data class OnlineAccountAuthRequest/*上线账号认证请求-结构模型数据类*/(
    val account:String,/*账号*/
    val token:String,/*Token令牌*/
    val authDeviceType:String,/*认证客户端设备平台类型*/
    )
