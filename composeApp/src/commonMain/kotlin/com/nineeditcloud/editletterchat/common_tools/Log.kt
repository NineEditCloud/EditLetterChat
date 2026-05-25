package com.nineeditcloud.editletterchat.common_tools

object Log/*日志*/{
    fun e/*错误日志*/(tag:String, message:String, throwable:Throwable?=null){
        val log=buildString{
            append("[$tag] ERROR: $message")
            if(throwable!=null) {
                append("\n${throwable.stackTraceToString()}")
            }
        }
        println(log)/*在LogCat/控制台 打印 具体异常类型和消息，使用 tag:System.out 过滤*/
    }
}