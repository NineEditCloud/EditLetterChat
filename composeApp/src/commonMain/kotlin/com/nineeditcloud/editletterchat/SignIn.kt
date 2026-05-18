package com.nineeditcloud.editletterchat

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.nineeditcloud.editletterchat.client.EditLettrtChat_HTTPApiClient
import com.nineeditcloud.editletterchat.client.Result
import com.nineeditcloud.editletterchat.database.AppDatabase
import com.nineeditcloud.editletterchat.database.UserAccountLocalData
import com.nineeditcloud.editletterchat.database.getDatabase
import compose.icons.Octicons
import compose.icons.octicons.Eye16
import compose.icons.octicons.EyeClosed16
import compose.icons.octicons.Person16
import createNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*登录界面*/

class SignIn :Screen{
    @Composable
    override fun Content() {
        var accountId by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        //    val scope=rememberCoroutineScope()/*协程*/
        val lifecycleOwner=LocalLifecycleOwner.current/*lifecycle协程，绑定 Activity(活动) 或 Fragment(界面片段) 生命周期*/


        val backgroundColor=if(!isSystemInDarkTheme()) Color(0xFFEEF2FD) else Color(0xFF1C1E1F)/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/
        //    val color=Brush.horizontalGradient(listOf(Color(0xFF6933CC),Color.Magenta, Color.Blue,Color.Cyan,
        //        Color.Green,Color.Yellow,Color(0xFFFFA500),Color.Red))/*渐变色(蓝紫色 紫蓝青绿黄橙红)*/
        val editBoxBackground=if(!isSystemInDarkTheme()) Color.White else Color(0xED2B2D30)
        val editBoxBorder=if(!isSystemInDarkTheme()) Color(0xED2B2D30) else Color.White

        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/

        Column(Modifier.background(backgroundColor).fillMaxSize() ){
            Column(modifier=Modifier.fillMaxSize().padding(horizontal=40.dp),
                   horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){

                Text/*标题*/(text="辑信", fontSize=32.sp, fontWeight=FontWeight.W900, modifier=Modifier.padding(bottom=80.dp),
                             style = TextStyle(brush = Brush.horizontalGradient(listOf(Color(0xFF6933CC),Color.Blue))/*渐变色(蓝紫色变蓝色)*/)
                            )

                EditBox/*账号编辑框*/(value=accountId, onValueChange={ accountId=it },
                                      startIcon={ Icon(Icons.Default.Person, contentDescription="头像图标",
                                                       Modifier.size(34.dp).clip(RoundedCornerShape(50.dp))/*裁剪内容(包括此组件的图片)为圆角*/)
                                      },
                                      labelText/*标签文本*/="账号", inputType=KeyboardType.Number/*输入类型 数字账号*/,
                                      background=editBoxBackground, borderColor=editBoxBorder, modifier=Modifier.padding(bottom=10.dp))
                EditBox/*密码编辑框*/(value=password, onValueChange={ password=it },
                                      startIcon={ Icon(imageVector=Icons.Default.Lock, contentDescription="密码图标",
                                                       Modifier.size(30.dp).clip(RoundedCornerShape(50.dp))/*裁剪内容(包括此组件的图片)为圆角*/)
                                      },
                                      labelText/*标签文本*/="密码", inputType=KeyboardType.Password/*输入类型 密码*/,
                                      endIcon={
                                          Icon(imageVector=if(passwordVisible) Octicons.Eye16 else Octicons.EyeClosed16,
                                               contentDescription="显示/隐藏 密码 视觉切换 图标",
                                               Modifier.pointerInput/*指针输入事件(无涟漪效果)*/(Unit){
                                                   detectTapGestures/*识别点击手势*/(onTap/*点击*/={
                                                       passwordVisible=!passwordVisible/*切换密码可见状态*/
                                                   })
                                               })
                                      }, contentVisualStatus=passwordVisible,
                                      background=editBoxBackground, borderColor=editBoxBorder, modifier=Modifier.padding(bottom=24.dp))

                Button/*登录按钮*/(
                    onClick={/*模拟登录过程*/isLoading = true
                        lifecycleOwner.lifecycleScope.launch{
                            val result=EditLettrtChat_HTTPApiClient.signIn(accountId, password)
                            when(result){
                                is Result.Success -> {/*请求成功，处理accountId*/
                                    withContext(Dispatchers.Main){/*在UI活动线程中执行(UI在主线程)*/
                                        isLoading=true
                                        val accountDatabase=getDatabase("UserAccount_LocalData")/*获取连接数据库*/
                                        val userAccountDao=accountDatabase.userAccountDao()/*获取 用户账号 表的Dao操作实例*/
                                        userAccountDao.insertAccount(UserAccountLocalData(result.accountId, password, password, result.token))/*将账号数据存入 用户账号表*/
                                        userAccountDao.updateUnusedState_excludeCurrentUse(result.accountId)/*更新 用户账号表 中未在使用的账号current_use字段值为false*/
                                        navigator.replace(MainActivity1())/*将当前界面 替换成 首页界面，覆盖原本界面*/
                                    }

                                }
                                is Result.Error -> {/*显示错误信息*/
                                    withContext(Dispatchers.Main){/*在UI活动线程中执行(UI在主线程)*/
                                        isLoading=false
                                        createNotification(NotificationType.TOAST).show(result.message)/*底部弹窗提示 响应消息*/
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = accountId.isNotEmpty() && password.isNotEmpty() && !isLoading/*按钮是否可用，账号和密码不为空且未在登录进行中*/,
                    shape = RoundedCornerShape(12.dp),/*设定按钮形状 圆角大小*/
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6933CC)/*按钮背景为蓝紫色*/)
                                  ){
                    val text=if(!isLoading) "登录" else "登录发送中..."
                    Row(verticalAlignment=Alignment.CenterVertically/*子项垂直居中*/){
                        Text(text=text, fontSize=13.sp, fontWeight=FontWeight.W800, style=MaterialTheme.typography.labelLarge)
                        if(isLoading){
                            CircularProgressIndicator/*加载图形*/(modifier=Modifier.size(20.dp), color=MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text/*更改密码选项*/("忘记密码？", Modifier.pointerInput/*识别点击手势(无涟漪效果)*/(Unit){
                    detectTapGestures(
                        onTap/*点击*/={  }
                                     )
                }, color = MaterialTheme.colorScheme.onSurface, lineHeight = 1.sp/*行高设置为1可让文本占用位置变小*/)
            }
        }
    }
}


