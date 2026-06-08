package com.nineeditcloud.editletterchat
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.nineeditcloud.editletterchat.common_tools.TopAppBar
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.retutn
import org.jetbrains.compose.resources.painterResource

/**
 * 首页功能弹窗视图列表 跳转的四个界面：
 * 创建群聊
 * 添加联系(加 好友/群)
 * 扫一扫
 * 收付款
 */

class CreateGroupChat/*创建群聊界面*/:Screen{
    @Composable
    override fun Content(){
        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/
        Column(Modifier.fillMaxSize() ){
            TopAppBar(rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft),{
                navigator.pop()/*关闭当前界面*/
            }, "创建群聊", )
        }
    }
}
class Add_FriendAndGroupChat/*添加好友和群聊界面(添加联系)*/:Screen{
    @Composable
    override fun Content(){
        val backgroundColor=if(!isSystemInDarkTheme())Color(0xFFEEF2FD) else Color(0xFF1C1E1F)/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/

        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/

        Column(Modifier.fillMaxSize() ){
            TopAppBar(rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft), {
                navigator.pop()/*关闭当前界面*/
            }, "添加联系", )
        }
    }
}
class ScanQRCode/*扫码界面*/:Screen{
    @Composable
    override fun Content(){
        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/
        Column(Modifier.fillMaxSize() ){
            TopAppBar(rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft),{
                navigator.pop()/*关闭当前界面*/
            }, "扫一扫", )
        }
    }
}
class PaymentAndReceipt/*收/付 款 界面*/:Screen{
    @Composable
    override fun Content(){
        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/
        Column(Modifier.fillMaxSize() ){
            TopAppBar(rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft),{
                navigator.pop()/*关闭当前界面*/
            }, "收/付款", )
        }
    }
}


