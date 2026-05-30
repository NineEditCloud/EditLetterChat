package com.nineeditcloud.editletterchat

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main(){
    ComposeViewport{
        KMPTheme{
            Navigator(StartupLoading())/*使用Voyager跨平台界面*/
        }
    }
}