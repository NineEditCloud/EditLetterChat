package com.nineeditcloud.editletterchat.common_tools

import android.os.Build

class AndroidPlatform :Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform():Platform= AndroidPlatform()

actual fun deviceType/*设备平台类型-安卓端实际方法*/():String="Android"
