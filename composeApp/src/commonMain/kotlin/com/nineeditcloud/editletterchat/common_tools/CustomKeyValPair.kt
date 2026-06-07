package com.nineeditcloud.editletterchat.common_tools

/*自定义-字符串键值对(适用于HTTP的简短序列化)*/

const val delimiter="`"/*分隔符，`(反引号) 并非英文句子中常用的'(单引号)*/
fun <T:Any>/*可调用处 泛型*/ T.toData/*对象模型转数据(序列化) 定义数据类的扩展函数*/():String{
    val str=this::class.toString()/*this代表调用当前扩展函数的对象，获取数据类对象模型实例的KClass，并转为String*/
    val left=str.indexOf('(')/*获取字符串中从头开始第一个(字符的索引，若获取失败返回-1*/
    return str.substring(left+1, str.length-1)/*截取字符串中 第一个(字符和最后一个字符 之间的字符串，索引从0开始 含索引对应的字符*/
//        .replace(Regex("\\s+"), "")/*将空格、制表符、换行等所有空白字符 替换为不存在*/
        .replace(", ",delimiter)/*将", "替换为更简略的"`"分隔符*/ + "\n"/*追加换行符以作为一条消息结束*/
    /*writeStringUtf8本身不添加换行符
    常见需要添加 \n 的场景：
    基于行的协议(Line-based protocols)，很多 TCP 文本协议(如 IRC、SMTP、简单的聊天协议)都规定：一条消息=一行文本，以\n或\r\n结束，若不加\n，接收方就会一直等待后续字节，直到超时或连接关闭，无法判断当前消息是否完整
    NDJSON(换行分隔的 JSON)，用于流式JSON数据，每行一个完整的JSON对象。
    在某些 Kotlin/Ktor 应用中，用 Flow或通道发送消息，用\n作为帧分隔符 以方便解析。
    网络流(Socket) 中手动实现消息边界
    若不加\n，接收方可能不知道一条消息在何处结束，尤其是连续发送多条消息时*/
}

fun String?.toHashMap/*数据转哈希表键值对 定义String的扩展函数*/():HashMap<String,String>{
    val strs=this?.split(delimiter)/*?.安全调用 若不为空 则将调用此函数的字符串 以分割字符 分割为数组*/
    val map=hashMapOf<String/*键*/,String/*值*/>()/*hashMapOf集合对象中存在一个HashMap，创建集合操作对象时声明val常量 依旧能更改其对应HashMap的元素*/
    strs?.forEach{ str -> /*遍历strs数组*/
        val key=str.substringBefore('=')/*截取 =字符前的 字符串*/
        val value=str.substringAfter('=')/*截取 =字符后的 字符串*/
        map[key]=value/*map[键]=赋值，若键不存在则添加新键值对元素*/
    }
    return map
}