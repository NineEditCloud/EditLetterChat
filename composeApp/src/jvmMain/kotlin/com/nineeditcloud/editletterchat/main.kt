package com.nineeditcloud.editletterchat

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
fun main() = application {
    Window(onCloseRequest=::exitApplication, title="辑信"){
        App()
    }
}
