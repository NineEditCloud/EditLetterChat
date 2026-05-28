package com.nineeditcloud.editletterchat.common_tools

object Log/*日志 单例对象*/{
    fun e/*错误日志*/(tag:String, errorMsg:String, throwable:Throwable?=null){
        val log=buildString{/*构建字符串(日志)*/
            append("[$tag] ERROR: $errorMsg")/*字符串之间追加(标签+"Error:"+错误消息)*/
            if(throwable!=null){/*若抛出异常参数不为空，传递了抛出的异常*/
                append("\n${throwable.stackTraceToString()}")/*追加抛出的异常*/
            }
        }
        println(log)/*在LogCat或控制台 打印 错误标签+错误消息+抛出异常，使用 tag:System.out 过滤查看*/
    }

    fun msg/*消息日志*/(tag:String, msg:String){
        val log=buildString{/*构建字符串(日志)*/
            append("[$tag] Message: $msg")/*字符串之间追加(标签+"Message:"+消息)*/
        }
        println(log)/*在LogCat或控制台 打印 标签+消息，使用 tag:System.out 过滤查看*/
    }
}