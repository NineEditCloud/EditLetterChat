package com.nineeditcloud.editletterchat

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.*
import platform.CoreGraphics.CGRect
import kotlinx.cinterop.ExperimentalForeignApi

fun MainViewController()=ComposeUIViewController{ App() }

class IOSPlatform: Platform {
    override val name:String=UIDevice.currentDevice.systemName()/*获取设备系统名*/ + " " + UIDevice.currentDevice.systemVersion/*获取设备系统版本*/
}

