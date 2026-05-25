package com.nineeditcloud.editletterchat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

/*Android移动端*/
class MainActivity:ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?){
        FileKit.init(this)/*FileKit跨平台 应用私有路径获取&文件操作 框架 初始化*/
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            AppTheme{
                Navigator(StartupLoading())/*使用Voyager跨平台界面*/
            }

        }
        val windowInsetsController=WindowCompat.getInsetsController(window, window.decorView)
//        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())/*沉浸界面，关闭导航栏*/
        WindowCompat.setDecorFitsSystemWindows(window,false)/*声明界面扩展到 导航栏和状态栏 背面展示*/


    }
}
