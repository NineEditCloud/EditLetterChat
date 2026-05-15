package com.nineeditcloud.editletterchat

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRect
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIDevice
import platform.UIKit.UILabel
import platform.UIKit.UIView

class IOSPlatform:Platform{
    override val name:String=UIDevice.currentDevice.systemName()/*获取设备系统名*/ +
            " "+ UIDevice.currentDevice.systemVersion/*获取设备系统版本*/


}
actual fun getPlatform/*获取设备信息*/():Platform=IOSPlatform()/*返回值为IOSPlatform类*/

//@OptIn(ExperimentalForeignApi::class)
//fun createCustomView(): UIView {
//    val container/*主容器视图*/ = UIView(frame=CGRect(x=0.0, y=0.0, width=300.0, height=200.0))
//    container.backgroundColor = UIColor.lightGrayColor
//
//    val label/*添加UILabel*/ = UILabel(frame = CGRect(x = 20.0, y = 50.0, width = 260.0, height = 30.0))
//    label.text = "Hello from Kotlin"
//    label.textAlignment = NSTextAlignmentCenter
//    label.textColor = UIColor.blackColor
//    container.addSubview(label)
//
//    val button/*添加UIButton*/ = UIButton.buttonWithType(UIButtonTypeSystem) as UIButton
//    button.frame = CGRect(x = 50.0, y = 100.0, width = 200.0, height = 44.0)
//    button.setTitle("Tap me", forState = UIControlStateNormal)
//    container.addSubview(button)
//
//    return container
//}


