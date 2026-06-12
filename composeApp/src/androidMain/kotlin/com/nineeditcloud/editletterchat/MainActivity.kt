package com.nineeditcloud.editletterchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nineeditcloud.editletterchat.common_tools.KMPTheme

//import androidx.compose.runtime.Composable
//import android.app.Activity
//import androidx.compose.ui.platform.LocalView
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat

/*Android移动端*/
class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        val splashScreen=installSplashScreen()/*先安装SplashScreen控制(启用自定义SplashScreen主题)，为确保安卓12+ 默认启动画面 无启动图*/
//        splashScreen.setKeepOnScreenCondition{ false }/*setKeepOnScreenCondition决定何时让启动画面消失 常用于等待数据加载、初始化完毕后再进入主界面，显式返回false 即使数据未就绪也强制立即移除(依旧无法移除安卓12+启动画面) 与不用setKeepOnScreenCondition一样无法缩短启动画面时长*/
        super.onCreate(savedInstanceState)
        FileKit.init(this)/*FileKit跨平台 应用私有路径获取&文件操作 框架 初始化*/
        enableEdgeToEdge()/*界面无边界*/
        setContent{
            val context=LocalContext.current/*创建 安卓Context上下文调用 对象*/
            val activity=context as ComponentActivity/*创建Intent，绑定当前活动*/
            exitApp={ activity.finish() }/*向全局变量传递 退出当前活动的代码*/
            KMPTheme{
                Navigator(StartupLoading() )/*用Voyager-Navigator跨平台界面*/
            }
            /*以下设置系统导航栏 颜色透明 和 图标深浅主题*/
            @Suppress("DEPRECATION") val systemUiController=rememberSystemUiController()
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


