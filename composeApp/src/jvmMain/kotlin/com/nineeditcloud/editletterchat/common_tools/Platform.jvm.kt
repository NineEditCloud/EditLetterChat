package com.nineeditcloud.editletterchat.common_tools

class JVMPlatform:Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform():Platform= JVMPlatform()

actual fun deviceType/*设备平台类型-桌面端实际方法*/():String="Desktop"
