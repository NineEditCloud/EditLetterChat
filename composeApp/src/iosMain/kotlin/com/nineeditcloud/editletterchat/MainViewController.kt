package com.nineeditcloud.editletterchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator
import com.nineeditcloud.editletterchat.client.FileKit
import com.nineeditcloud.editletterchat.common_tools.KMPTheme
import platform.UIKit.UIApplication
import platform.posix.exit

//import platform.darwin.nil
//import platform.UIKit.UIViewController

//import kotlinx.cinterop.ExperimentalForeignApi
//import platform.CoreGraphics.CGRect
//import platform.UIKit.NSTextAlignmentCenter
//import platform.UIKit.UIButton
//import platform.UIKit.UIButtonTypeSystem
//import platform.UIKit.UIColor
//import platform.UIKit.UIControlStateNormal
//import platform.UIKit.UILabel
//import platform.UIKit.UIView
//import platform.UIKit.*
//import kotlin.experimental.ExperimentalNativeApi

/*IOS移动端-应用主函数*/
@Composable
fun MainViewController()=ComposeUIViewController{
//    FileKit.init()
    exitApp={
        exit(0)/*iOS退出应用(非正常推荐方式，但能做到)*/
        /*或用UIApplication相关方法*/
    }
    KMPTheme{
        Navigator(StartupLoading() )/*用Voyager-Navigator跨平台界面*/
    }
}/*.apply{*//*在 ComposeUIViewController 的工厂方法中使用*//*
    this.setViewControllers(listOf(HideHomeIndicatorController()), animated=false)*//*替换默认的UIViewController 为自定义的*//*
}*/

//actual fun hideSystemNavBars/*隐藏系统导航栏-IOS端方法*/(){
//}
//actual fun showSystemNavBars/*显示系统导航栏-IOS端方法*/(){
//}
//class HideHomeIndicatorController:UIViewController{/*创建一个自定义的 UIViewController 子类，用于隐藏 Home Indicator*/
//    override fun prefersHomeIndicatorAutoHidden():Boolean=true
//}


//@OptIn(ExperimentalForeignApi::class)
//fun createCustomView():UIView {
//    val container/*主容器视图*/=UIView(frame=CGRect(x=0.0, y=0.0, width=300.0, height=200.0))
//    container.backgroundColor=UIColor.lightGrayColor
//
//    val label/*添加UILabel*/ =UILabel(frame=CGRect(x=20.0, y=50.0, width=260.0, height=30.0))
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




