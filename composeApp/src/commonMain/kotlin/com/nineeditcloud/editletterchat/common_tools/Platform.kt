package com.nineeditcloud.editletterchat.common_tools

class Greeting{
    private val platform =getPlatform()
    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform

expect fun deviceType/*获取设备平台类型-期望函数*/():String
