package com.nineeditcloud.editletterchat.client
import com.nineeditcloud.editletterchat.common_tools.Log
import com.nineeditcloud.editletterchat.common_tools.deviceType
import com.nineeditcloud.editletterchat.common_tools.toData
import com.nineeditcloud.editletterchat.common_tools.toHashMap
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException

/**
 * 自动重连的 TCP长连接客户端(简化版)
 * @param account 认证账号
 * @param token 认证令牌
 * @param onMessage 收到消息时的回调(运行在协程所在线程，非主线程)
 */
fun tcpLongConnClient(account:String, token:String, onMessage/*收到消息回调*/:(HashMap<String,String>)->Unit)=runBlocking{
    val host="192.168.1.47";val port=9000
    while(coroutineContext.isActive){
        try{
            aSocket(SelectorManager(Dispatchers.IO) ).tcp().connect(host, port)
                .use{ socket ->
                    /*.use是个扩展函数，它会自动关闭资源(类似Java的try-with-resources)，所以即使内部抛出异常，use块结束后、退出作用域时 socket都会被关闭，对应的输入输出通道也会关闭。因此不会有多余遗留内容，资源会自动清理*/
                    Log.msg("Ktor已连接TCP长连接服务器：", "$host:$port")
                    val input=socket.openReadChannel()/*输入流通道，接收*/
                    val output=socket.openWriteChannel(autoFlush=true)/*输出流通道，发送*/

                    val authMsg=OnlineAccountAuthRequest(account,token,deviceType() ).toData()
                    output.writeStringUtf8(authMsg)/*发送认证信息*/

                    val authResponse=input.readUTF8Line()/*读取认证响应*/ ?:throw Exception("认证响应为空")/*若读取响应为空，抛出异常触发自动重连*/
//                    require(authResponse.contains("\"auth_ok\"")/*正常条件*/ ){/*若不达成正常条件 则接收此块内异常信息并抛出异常*/
//                        "认证失败: $authResponse"/*异常信息*/
//                    }
                    if(authResponse.contains("auth_ok") ) Log.msg("Ktor-TCP长连接", "账号令牌认证成功")
                    else{
                        throw IllegalArgumentException("账号令牌认证失败: $authResponse")/*抛出异常信息*/
                    }

                    while(coroutineContext.isActive){/*循环等待接收服务端消息*/
                        val line=input.readUTF8Line() ?:break/*若接收结果为空 说明服务端断开，跳出接收循环*/
                        /*未跳出情况下执行以下代码*/
                        val msg=line.toHashMap()
                        onMessage(msg)
                        Log.msg("Ktor-TCP长连接", "收到服务器反馈：$line")
                    }
                }
        }catch(e:CancellationException){
            Log.e("Ktor-TCP长连接", "连接异常", e)
            throw e/*正常取消时直接抛出，不吞掉取消*/
        }catch(e:Exception){
//            if(e is CancellationException) throw e
            if(e is IllegalArgumentException) Log.e("Ktor-TCP长连接", e.message.toString(), e)
            Log.e("Ktor-TCP长连接", "连接异常", e)
            if(coroutineContext.isActive) delay(5_000)/*连接异常、读写异常等，等待5秒后自动重连*/
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
