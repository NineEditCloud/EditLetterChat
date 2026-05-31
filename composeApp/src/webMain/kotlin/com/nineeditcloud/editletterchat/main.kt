package com.nineeditcloud.editletterchat

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main(){
//    ComposeViewport{
//        KMPTheme{
//            Navigator(StartupLoading())/*使用Voyager跨平台界面*/
//        }
//    }
    androidx.compose.web.renderComposable(rootElementId="root"){/*Canvas模式：渲染到一个<canvas>元素中，底层用Skia引擎 通过WebAssembly(Wasm)直接在一个<canvas>上绘制所有像素，就像Android上用Skia绘制ComposeUI一样*/
        KMPTheme{
            Navigator(StartupLoading())/*使用Voyager跨平台界面*/
        }
    }
}