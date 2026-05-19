package com.nineeditcloud.editletterchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.image01
import org.jetbrains.compose.resources.painterResource

/*开始选择-登录和注册 选择界面*/
/** 像素单位
 * DP(Density-independentPixels 密度无关像素)：用于UI元素尺寸，在不同密度的屏幕上显示同样大小
 * SP(Scale-independentPixels 缩放无关像素)：用于字体大小，无固定缩放大小，自适应系统缩放大小
 */

class StartSelector:Screen{
    @Composable
    override fun Content() {
        Box/*堆叠布局，可把子组件堆叠在同一位置*/(Modifier.fillMaxSize()/*布局填充容器所有大小*/.background(Color(0xFFFDFDFD))/*包裹布局与背景图组件相同底色*/){
            Image/*背部图片*/(painterResource(Res.drawable.image01/*图片高度920.dp左右*/), contentDescription="logo",
                              modifier=Modifier.fillMaxWidth()/*填充容器全部宽度*/.align(Alignment.Center)/*在Box中 完全居中*/,
                              contentScale=ContentScale.FillWidth/*拉伸至组件宽度(高度按拉伸比例自适应)，自动调整图片组件高度*/    )

            Row/*水平布局*/(modifier=Modifier.fillMaxWidth()/*填充容器全部宽度*/.padding(bottom=100.dp)/*底部边距*/
                .align(Alignment.BottomCenter)/*在Box中 垂直居底 水平居中*/ ){
                val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/

                Button(modifier=Modifier.padding(start=50.dp)/*开头(左)边距*/.width(100.dp),onClick={/*点击事件*/
                    navigator.push(SignUp())/*跳转到注册界面*/
                }, shape=RoundedCornerShape(12.dp),/*按钮形状 圆角大小*/
                       colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF6933CC)/*按钮背景为蓝紫色*/)
                      ){ Text("注册",color=MaterialTheme.colorScheme.onSurface/*文字颜色*/, fontWeight=FontWeight.W900/*字体粗细 最粗*/) }

                Spacer(Modifier.weight(1f)/*填充当前布局全部空间，将其它按钮推到右侧*/)

                Button(modifier=Modifier.padding(end=50.dp)/*结尾(右)边距*/.width(100.dp)/*宽度*/,onClick={/*点击事件*/
                    navigator.push(SignIn())/*跳转到登录界面*/
                }, shape=RoundedCornerShape(12.dp),/*按钮形状 圆角大小*/
                       colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF6933CC)/*按钮背景为蓝紫色*/)
                      ){ Text("登录", color=MaterialTheme.colorScheme.onSurface, fontWeight=FontWeight.W900) }
            }
        }
    }
}




