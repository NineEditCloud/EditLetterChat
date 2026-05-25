package com.nineeditcloud.editletterchat.common_tools

import platform.UIKit.UIDevice

class IOSPlatform:Platform {
    override val name:String=UIDevice.currentDevice.systemName()/*获取设备系统名*/+" "+
            UIDevice.currentDevice.systemVersion/*获取设备系统版本*/
}
actual fun getPlatform/*获取设备信息*/():Platform=IOSPlatform()/*返回值为IOSPlatform类*/

actual fun deviceType/*设备平台类型-IOS端实际方法*/():String="IOS"




