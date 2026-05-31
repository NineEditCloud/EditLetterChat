package com.nineeditcloud.editletterchat

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import com.nineeditcloud.editletterchat.common_tools.KMPTheme
import io.github.vinceglb.filekit.FileKit


/*JVM桌面端*/
fun main(){
    FileKit.init(appId="EditLetterChat")/*FileKit跨平台 应用私有路径获取&文件操作 框架 初始化，应用入口点调用 为桌面应用指定唯一ID，构建系统路径*/
    application{
        val verticalNarrowWindowState/*竖直窄窗状态*/=rememberWindowState(width=350.dp/*宽度，Dp.Unspecified由内容决定*/, height=680.dp,/*高度，Dp.Unspecified由内容决定*/
                                            placement=WindowPlacement.Floating/*设置初始位置为浮动*/,
                                            position=WindowPosition((800-350).dp/*水平坐标*/, 0.dp/*垂直坐标*/)/*窗口位置(桌面屏幕常规尺寸为1280*720的倍数 或按最小尺寸800*600)*/,
                                            )
        Window(onCloseRequest=::exitApplication, state=verticalNarrowWindowState, title="辑信"){
            KMPTheme{
                Navigator(StartupLoading() )/*用Voyager-Navigator跨平台界面*/
//                NavigableListDetailPaneScaffold(navigator=navigator, listPane={
//                        AnimatedPane{
//                            ListContent(words=sampleWords, selectionState=navigator.currentDestination?.contentKey?.let{
//                                    SelectionVisibilityState.ShowSelection(it)
//                            }?:SelectionVisibilityState.NoSelection,
//                                        onWordClick={ word ->
//                                            scope.launch{
//                                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, word)
//                                            }
//                                                    },
//                                animatedVisibilityScope=this@AnimatedPane, sharedTransitionScope=this@SharedTransitionLayout, )
//                        }
//                    },
//                    detailPane={
//                        AnimatedPane{
//                            DetailContent(definedWord=navigator.currentDestination?.contentKey,
//                                animatedVisibilityScope=this@AnimatedPane, sharedTransitionScope=this@SharedTransitionLayout,
//                                onClosePane={
//                                    scope.launch{
//                                        navigator.navigateBack(backNavigationBehavior=BackNavigationBehavior.PopUntilScaffoldValueChange)
//                                    }
//                                }, )
//                        }
//                    })
            }

        }
    }
}


