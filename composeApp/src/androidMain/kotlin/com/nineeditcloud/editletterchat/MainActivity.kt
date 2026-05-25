package com.nineeditcloud.editletterchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nineeditcloud.editletterchat.common_tools.AppTheme

//import androidx.compose.runtime.Composable
//import android.app.Activity
//import androidx.compose.ui.platform.LocalView
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat

/*Android移动端*/
class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        FileKit.init(this)/*FileKit跨平台 应用私有路径获取&文件操作 框架 初始化*/
        enableEdgeToEdge()/*界面无边界*/
        setContent{
            AppTheme {
                Navigator(StartupLoading())/*使用Voyager跨平台界面*/
            }
            /*以下设置系统导航栏 颜色透明 和 图标深浅主题*/
            val systemUiController=rememberSystemUiController()
            systemUiController.setStatusBarColor/*设置状态栏颜色*/(color=Color.Transparent/*透明*/, darkIcons=!isSystemInDarkTheme()/*根据 深/浅 主题调整图标颜色*/)
            systemUiController.setNavigationBarColor/*设置导航栏颜色*/(color=Color.Transparent/*透明*/)
        }
//        val windowInsetsController=WindowCompat.getInsetsController(window, window.decorView)
//        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())/*沉浸界面，关闭导航栏*/
        WindowCompat.setDecorFitsSystemWindows(window, false)/*声明界面扩展到 导航栏和状态栏 背面展示*/
    }
}


//@Composable
//actual fun hideSystemNavBars/*隐藏系统导航栏-安卓端实际方法*/(){
//    val insetsController=insetsController()
//    insetsController.hide(WindowInsetsCompat.Type.navigationBars())/*隐藏系统导航栏*/
//    insetsController.systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE/*设置短暂显示行为(适用于沉浸式体验)*/
//}
//@Composable
//actual fun showSystemNavBars/*隐藏系统导航栏-安卓实际方法*/(){
//    val insetsController=insetsController()
//    insetsController.show(WindowInsetsCompat.Type.systemBars())/*显示导航栏*/
//}
//@Composable
//fun insetsController/*获取安卓嵌入控制器*/():WindowInsetsControllerCompat{
//    val view=LocalView.current
//    val window=(view.context as Activity).window
//    return WindowInsetsControllerCompat(window, view)
//}


