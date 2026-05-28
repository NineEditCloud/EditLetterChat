package com.nineeditcloud.editletterchat.client
import com.nineeditcloud.editletterchat.common_tools.Log
import com.nineeditcloud.editletterchat.common_tools.toData
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import com.nineeditcloud.editletterchat.common_tools.toHashMap
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.CancellationException

fun tcpLongConnClient(accountId:String, token:String)=runBlocking{/*协程作用域*/
    val hostname="192.168.1.47";val port=9000
    val socket=aSocket(SelectorManager(Dispatchers.IO) ) .tcp().connect(hostname, port)/*Ktor-network在输入输出流建立TCP客户端长连接*/
    Log.msg("Ktor已连接TCP长连接服务器：", "$hostname:$port")
    val input=socket.openReadChannel()/*输入流通道，接收*/
    val output=socket.openWriteChannel(autoFlush=true)/*输出流通道，发送*/

//    val authMsg="""{"type":"auth","token":"secret-token"}""" + "\n"/*模拟JSON认证消息*/
    val authMsg=OnlineAccountAuthRequest(accountId,token).toData()
    output.writeStringUtf8(authMsg)/*发送认证消息*/

    val authResponseLine=input.readUTF8Line()/*读取 服务端反馈的认证响应*/ ?:/*若为空*/error("无身份验证响应")
//    val authResponse=json.decodeFromString<ContentType.Message>(authResponseLine)/*将服务端反馈的认证响应的 JSON字符串 反序列化解析为数据类*/
    Log.msg("Ktor-TCP客户端接收：", authResponseLine)
    val authResponse=authResponseLine.toHashMap()/*将服务端反馈的认证响应的 键值对字符串 解析为HashMapOf*/
    if(authResponse["type"]/*获取类型键对应值*/ !="auth_ok"){
        Log.msg("Ktor-TCP客户端", "账号认证失败：${authResponse["message"]/*获取消息键对应值*/}")
        socket.close()/*因为账号认证失败，关闭连接、释放资源*/
        return@runBlocking
    }
    Log.msg("Ktor-TCP客户端", "账号令牌认证成功")

    launch(Dispatchers.IO){/*启动I/O协程*/
        while(isActive){/*激活状态下持续循环*/
            val line=input.readUTF8Line()/*接收服务器消息*/ ?:break/*若接收失败则结束循环*/
            Log.msg("Ktor-TCP客户端", "收到服务器反馈: $line")
        }
    }

//    while(true){
//        delay(2000)/*等待2秒*/
//        output.writeStringUtf8("""{"type":"ping"}""" + "\n")/*发送测试消息*/
//        output.writeStringUtf8("Hello from client at ${System.currentTimeMillis()}\n")/*主协程负责发送消息(模拟交互)*/
//    }
//    socket.close()/*关闭连接，长连接持续收消息不建议关闭*/
}

/**
 * 自动重连的 TCP 长连接客户端（简化版）
 *
 * @param host 服务器地址
 * @param port 端口
 * @param authToken 认证令牌
 * @param onMessage 收到消息时的回调（运行在协程所在线程，非主线程）
 */
suspend fun tcpLongConnClient(
    authToken: String,
    onMessage: (String) -> Unit){
    val host="192.168.1.47";val port=9000
    while(coroutineContext.isActive){
        try {
            aSocket(SelectorManager(Dispatchers.IO) ).tcp().connect(host, port)
                .use{ socket ->
                    val input=socket.openReadChannel()
                    val output=socket.openWriteChannel(autoFlush = true)

                    // 发送认证信息
                    output.writeStringUtf8("""{"type":"auth","token":"$authToken"}""" + "\n")

                    // 读取认证响应
                    val authResponse = input.readUTF8Line()
                        ?: throw Exception("认证响应为空")
                    require(authResponse.contains("\"auth_ok\"")) {
                        "认证失败: $authResponse"
                    }

                    // 循环接收服务端消息
                    while (coroutineContext.isActive) {
                        val line = input.readUTF8Line() ?: break  // 服务端断开
                        onMessage(line)
                    }
                }
        }catch(e:CancellationException){
            throw e/*正常取消时直接抛出，不吞掉取消*/
        }catch(e:Exception){
//            if(e is CancellationException) throw e
            if (coroutineContext.isActive) delay(5_000)/*连接异常、读写异常等，等待 5 秒后自动重连*/
        }
    }
}

fun main()=runBlocking{
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
    )
