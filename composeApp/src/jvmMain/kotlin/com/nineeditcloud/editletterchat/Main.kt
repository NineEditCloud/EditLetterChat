package com.nineeditcloud.editletterchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.nineeditcloud.editletterchat.common_tools.KMPTheme
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.icon00
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.awaitCancellation
import java.awt.geom.RoundRectangle2D
import java.beans.PropertyChangeListener


/*JVM桌面端*/
fun main(){
    FileKit.init(appId="EditLetterChat")/*FileKit跨平台 应用私有路径获取&文件操作 框架 初始化，应用入口点调用 为桌面应用指定唯一ID，构建系统路径*/
    application{
        KMPTheme{
            val icon=painterResource(Res.drawable.icon00)/*注意drawable目录资源不能有相同名称文件(即使扩展名不同也不行)，否则Res.drawable选择有重复名文件时执行异常*/
//            val verticalNarrowWindowState/*竖直窄窗状态*/=rememberWindowState(width=350.dp/*宽度，Dp.Unspecified由内容决定*/, height=680.dp/*高度，Dp.Unspecified由内容决定*/,
//                placement=WindowPlacement.Floating/*设置初始位置为浮动*/,
//                position=WindowPosition((800-350).dp/*水平坐标*/, 0.dp/*垂直坐标*/)/*窗口位置(桌面屏幕常规尺寸为1280*720的倍数 或按最小尺寸800*600)*/, )
//            Window(icon=icon, title="辑信", onCloseRequest/*关闭请求功能*/=::exitApplication/*exitApplication是ApplicationScope的扩展函数*/,
//            state=verticalNarrowWindowState, resizable=false/*边缘是否可 缩放/调整大小*/, ){
//                Navigator(StartupLoading() )/*用Voyager-Navigator跨平台界面*/
//            }
            val horizontalWindowState/*水平窗状态*/=rememberWindowState(width=911.dp/*宽度，Dp.Unspecified由内容决定*/, height=641.dp/*高度，Dp.Unspecified由内容决定*/,
                placement=WindowPlacement.Floating/*设置初始缩放为浮动*/,
                position=WindowPosition(180.dp/*水平坐标*/, 20.dp/*垂直坐标*/)/*窗口位置(桌面屏幕常规尺寸为1280*720的倍数 或按最小尺寸800*600)*/, )
            Window(icon=icon, onCloseRequest/*关闭请求功能*/=::exitApplication/*exitApplication是ApplicationScope的扩展函数*/, state=horizontalWindowState,
                   undecorated=true/*去掉默认原生标题栏*/, transparent=false/*保留窗口背景(可设true做异形窗口)*/, resizable=false/*边缘是否可 缩放/调整大小*/, ){
//                Navigator(StartupLoading() )/*用Voyager-Navigator跨平台界面*/
                App("辑信", horizontalWindowState, ::exitApplication/*::后边代表传递的方法 或Lambda包装onCloseApp={ exitApplication() }*/)
            }
        }

    }
}



@Composable
fun FrameWindowScope.App(title:String, windowState:WindowState, onCloseApp:()->Unit, ){
    /*平台判断，用于决定按钮放在左边还是右边*/
    val isMacOS=remember{ System.getProperty("os.name").lowercase().contains("mac") }/*获取是否为MacOS平台*/
    val placement=windowState.placement
    val isMaximized =placement==WindowPlacement.Maximized /*比较是否为最大窗口*/
    val isFullscreen=placement==WindowPlacement.Fullscreen/*比较是否为全屏*/

    val cornerRadiusPx=with(LocalDensity.current){ 16.dp.toPx() }/*圆角半径(dp)*/

    /*监听全局键盘事件(通过 AWT Toolkit 或 Compose onKeyEvent)*/
    /*推荐在顶层Box上用Modifier.onKeyEvent，但全屏时窗口内容抢占焦点*/
    val keyListener=object:java.awt.event.KeyAdapter(){/*键盘监听事件适配器对象*/
        override fun keyPressed(e:java.awt.event.KeyEvent){/*键盘监听事件*/
            if(e.keyCode==java.awt.event.KeyEvent.VK_ESCAPE){/*捕获Esc键 事件*/
                windowState.placement=WindowPlacement.Floating/*退出全屏(设为浮动窗口)*/
            }
        }
    }
    LaunchedEffect(placement){/*监听窗口模式状态触发，用于动态设置窗口形状*/
        if(isMaximized){/*若是全屏状态*/
            window.shape=null/*全屏时必须移除圆角形状，否则无法铺满屏幕*/

            window.addKeyListener(keyListener)/*窗口 添加 键盘监听事件对象*/
            try{
                awaitCancellation()/*保持监听直到Composable退出*/
            }finally{/*窗口结束时 强制完成任务*/
                window.removeKeyListener(keyListener)/*窗口 移除 键盘监听事件对象*/
            }

        }else{
            val updateShape/*刷新形状 代码块*/={
                window.shape/*矩形形状-圆形*/=RoundRectangle2D.Double(0.0, 0.0, window.width.toDouble(), window.height.toDouble(),
                    cornerRadiusPx.toDouble(), cornerRadiusPx.toDouble(), )
            }
            updateShape()/*非全屏时恢复圆角，刷新形状*/
            val listener=PropertyChangeListener{ evt ->/*窗口参数监听事件*/
                if(evt.propertyName=="size" || evt.propertyName=="location"){/*监听大小变化，形状跟随(如拖拽、全屏、边缘缩放)*/
                    updateShape()/*刷新形状*/
                }
            }
            window.addPropertyChangeListener(listener)/*窗口 添加 参数监听事件*/
            try{
                awaitCancellation()/*保持监听直到Composable退出*/
            }finally{/*窗口结束时 强制完成任务*/
                window.removePropertyChangeListener(listener)/*窗口 移除 参数监听事件*/
            }
        }
    }
    Column(modifier=Modifier.fillMaxSize() ){
        WindowDraggableArea/*可拖动窗体组件*/{
            /*========== 自定义标题栏 ==========*/
            Row(modifier=Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF2D2D2D) )/*标题栏背景色*/
                /*.windowDraggableArea()*//*拖拽移动窗口*/, verticalAlignment=Alignment.CenterVertically, ){
                Spacer(modifier=Modifier.width(8.dp) )
                if(isMacOS){/*macOS 风格：窗口控制按钮在左边*/
                    WindowControlButtons(isMaximized=isMaximized,
                                         onMinimize={ window.isMinimized=true },
                                         onMaximize={
                                             windowState.placement/*窗口缩放*/=if(isMaximized) WindowPlacement.Floating/*浮动*/ else WindowPlacement.Maximized/*最大窗口*/
                                         },
                                         onClose={ onCloseApp.invoke() },
                                        )
                    Spacer(modifier=Modifier.width(8.dp) )
                }

                Text(text=title, color=Color.White, fontSize=14.sp, modifier=Modifier.weight(1f) )/*标题文本(居中)*/

                if(!isMacOS){/*Windows/Linux 风格：按钮在右边*/
                    WindowControlButtons(isMaximized=isMaximized,
                                         onMinimize={ window.isMinimized = true },
                                         onMaximize={
                                             windowState.placement=if(isMaximized) WindowPlacement.Floating else WindowPlacement.Maximized
                                         },
                                         onClose={ onCloseApp.invoke() },
                                        )
                }
            }
        }

        /*========== 窗口内容区域 ==========*/
        Box(modifier=Modifier.fillMaxSize().background(Color(0xFF3C3C3C) ) ){

        }
    }
}

@Composable
fun WindowControlButtons(isMaximized:Boolean, onMinimize:()->Unit, onMaximize:()->Unit, onClose:()->Unit ){
    Row{
        IconButton(onClick=onMinimize){
            Icon(Icons.Default.Minimize, contentDescription="最小化", tint=Color.White)
        }
        IconButton(onClick=onMaximize){
            Icon(if(!isMaximized) Icons.Default.Fullscreen else Icons.Default.FullscreenExit,
                contentDescription=if(!isMaximized) "最大化" else "还原", tint=Color.White)
        }
        IconButton(onClick=onClose){
            Icon(Icons.Default.Close, contentDescription="关闭", tint=Color.White)
        }
    }
}


