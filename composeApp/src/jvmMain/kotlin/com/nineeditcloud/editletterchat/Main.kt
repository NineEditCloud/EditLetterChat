package com.nineeditcloud.editletterchat

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import io.github.vinceglb.filekit.FileKit

fun main(){
    FileKit.init(appId="EditLetterChat")/*FileKit跨平台 应用私有路径获取&文件操作 框架 初始化，应用入口点调用 为桌面应用指定唯一ID，构建系统路径*/
    application{
        Window(onCloseRequest=::exitApplication, title="辑信"){
            Navigator(StartupLoading())/*使用Voyager跨平台界面*/
        }
    }
}
