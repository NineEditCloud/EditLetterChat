package com.nineeditcloud.editletterchat

import platform.UIKit.UIDevice


class IOSPlatform:Platform{
    override val name:String=UIDevice.currentDevice.systemName()/*获取设备系统名*/+" "+
            UIDevice.currentDevice.systemVersion/*获取设备系统版本*/
}
actual fun getPlatform/*获取设备信息*/():Platform=IOSPlatform()/*返回值为IOSPlatform类*/






