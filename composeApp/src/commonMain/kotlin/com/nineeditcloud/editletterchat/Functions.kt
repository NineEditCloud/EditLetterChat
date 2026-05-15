package com.nineeditcloud.editletterchat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

/**
 * 首页功能弹窗视图列表 跳转的四个界面：
 * 创建群聊
 * 添加联系(加 好友/群)
 * 扫一扫
 * 收付款
 */



class CreateGroupChat/*创建群聊界面*/: Screen{
    @Composable
    override fun Content() {
        val density=LocalDensity.current
        var show by remember { mutableStateOf(false) }
        var clickPosition by remember { mutableStateOf(Offset.Zero) }

        Box(Modifier.fillMaxSize()
                .pointerInput(Unit){
                    detectTapGestures { offset ->
                        clickPosition=offset/*获取相对于 Box 的点击位置*/
                        show=true
                    }
                }
           ){
            Button(
                onClick={ /* 可选：按钮点击也可以显示在按钮位置 */ }, Modifier.fillMaxWidth()
                  ){
                Text("Click anywhere to show menu at click position")
            }

            if(show){
                Popup/*使用 Popup 而不是 DropdownMenu*/(alignment=Alignment.TopStart, offset=with(density){
                    IntOffset(x=clickPosition.x.toInt(), y=clickPosition.y.toInt() )
                }, onDismissRequest={ show=false }
                                                       ){/*自定义菜单内容*/
                    Column(Modifier.width(200.dp).background(Color.White, RoundedCornerShape(4.dp) )
                               .shadow(4.dp)
                               .pointerInput(Unit){
                                   detectTapGestures{/*点击菜单外部关闭需要在 Popup 外部处理*/
                                   }
                               }
                          ){
                        Text("Menu Item 1", Modifier.fillMaxWidth().padding(16.dp)
                            .clickable{ show=false }
                            )
                        Text("Menu Item 2", Modifier.fillMaxWidth().padding(16.dp)
                            .clickable{ show=false }
                            )
                        Text("Dismiss", Modifier.fillMaxWidth().padding(16.dp)
                            .clickable{ show=false }
                            )
                    }
                }
            }
        }
    }
}
class Add_FriendAndGroupChat/*添加好友和群聊界面*/: Screen{
    @Composable
    override fun Content() {
        val backgroundColor=if(!isSystemInDarkTheme())Color(0xFFEEF2FD) else Color(0xFF1C1E1F)/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/

        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/

        Column(Modifier.fillMaxSize()){
            Row(Modifier.background(backgroundColor).fillMaxWidth().statusBarsPadding()/*顶部状态栏边距，防止内容跑到 顶部状态栏 后边被挡住*/
                ,verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/){
                Box(Modifier.fillMaxWidth(), /*contentAlignment=Alignment.CenterVertically*/){
                    IconButton/*仅用来包裹图标的按钮，放在按钮里可点击命中区域更大*/(onClick={/*返回图标按钮点击事件*/
                        navigator.pop()/*关闭当前界面*/
                        //                activity.onBackPressed()//模拟按下返回键
                        //                OnBackPressedCallback()/*安卓13以上模拟按下返回键*/
                        //                ActivityCompat.finishAfterTransition(this as Activity)/*退出当前活动，如果当前界面有过渡动画，等待过渡动画结束后再退出当前活动*/
                    }, Modifier.align(Alignment.CenterStart)){
                        Icon(imageVector=Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription="返回图标"/*图标描述，必填项，否则报错*/)
                    }
                    Text("添加联系",/*标题*/ lineHeight=1.sp, modifier=Modifier/*.weight(1f*//*百分百*//*)*//*填充全部宽度*/
                        .align(Alignment.Center),
                         fontSize=10.sp, color=MaterialTheme.colorScheme.onSurface/*文字颜色根据主题自适应*/)
                }

            }
        }
    }
}
class ScanQRCode/*扫码界面*/: Screen{
    @Composable
    override fun Content() {

    }
}
class PaymentAndReceipt/*收付款界面*/: Screen{
    @Composable
    override fun Content() {

    }
}


