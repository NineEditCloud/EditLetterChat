package com.nineeditcloud.editletterchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.nineeditcloud.editletterchat.database.getDatabase
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.image00_upscale_4x
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/*启动图界面*/
/*---Voyager-Navigation核心导航操作
Navigator()：各平台下启动Voyager-Navigation的Screen跨平台通用界面
LocalNavigator.currentOrThrow ：Voyager-Navigation 获取绑定当前界面的导航控制器
navigator.push(): 跳转到新界面
navigator.pop(): 返回上个界面
navigator.replace(): 将当前界面 替换成目标界面
navigator.popUntil(): 返回到导航栈中的指定界面
*/

var doesAnAccountExist=false
class StartupLoading:Screen{
    @Composable
    override fun Content(){
        Box/*堆叠布局，方便以后再加跳转计时等组件*/(Modifier.fillMaxSize().background(Color(0xFFFDFDFD))/*包裹布局与背景图组件相同底色*/){
            Image/*背部图片*/(painterResource(Res.drawable.image00_upscale_4x/*辑信启动图片原高度660.dp左右*/), contentDescription="启动加载界面图片",
                              Modifier.fillMaxWidth()/*填充容器全部宽度*/.align(Alignment.BottomCenter)/*在Box中 垂直居底 水平居中*/,
                              contentScale=ContentScale.FillWidth/*拉伸至组件宽度(高度按拉伸比例自适应)，自动调整图片组件高度*/
                              /*内容缩放方式：
                              Crop裁剪以填充(会剪掉部分)
                              FillBounds拉伸以填充整个区域(不保持宽高比) FillWidth拉伸至组件宽度(高度按拉伸比例自适应) FillHeight拉伸至组件高度(宽度按拉伸比例自适应)
                              Fit等比例缩放图片，使得图片完全显示在区域内，可能不会填满*/
                             )
        }

        val accountDatabase=getDatabase("UserAccount_LocalData")/*获取 用户账号本地数据 数据库实例*/
        val userAccountDao=accountDatabase.userAccountDao()/*获取数据库中的 已登录账号本地数据 表实例*/
        val lifecycleOwner=LocalLifecycleOwner.current/*lifecycle协程，绑定 Activity(活动) 或 Fragment(界面片段) 生命周期*/
        lifecycleOwner.lifecycleScope.launch{/*协程*/
            doesAnAccountExist=userAccountDao.getHisCurrentUseAccount()/*查询是否已存在正在使用的账号*/
        }
        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/

        LaunchedEffect(Unit)/*使用LaunchedEffect实现延迟跳转(无跳转计时)*/{
            delay(2000)/*延迟2秒*/
            if(doesAnAccountExist){/*如果存在正在使用的账号*/
                navigator.replace(StartSelector())/*将当前界面 替换成开始选择界面，将原本界面覆盖*/
            }else{/*否则*/
                navigator.replace(SignIn())/*将当前界面 替换成登陆界面，将原本界面覆盖*/
            }
        }
    }
}




