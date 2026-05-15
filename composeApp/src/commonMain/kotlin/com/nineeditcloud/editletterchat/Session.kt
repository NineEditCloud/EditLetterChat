package com.nineeditcloud.editletterchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nineeditcloud.editletterchat.ui.theme.InstantMessagingTheme

var sessionBackgroundColor=Color.White

/*消息会话界面*/
class Session : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstantMessagingTheme {
                Greeting()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    val context=LocalContext.current
    val activity=context as ComponentActivity//创建绑定该界面的Intent，为界面Activity跳转和退出做准备

    var text by remember{ mutableStateOf("") }/*text对象，实时监听文本编辑框内容*/

    sessionBackgroundColor=if(!isSystemInDarkTheme()) Color(0xFFEEF2FD) else Color(0xF5080909)/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/


    Column(Modifier.fillMaxSize().imePadding()/*自适应软键盘升起边距*/
    /*.background(sessionBackgroundColor)*/
//        .systemBarsPadding()/*系统栏边距，防止内容跑到手机的 顶部状态栏 和 底部导航栏 后面被挡住，如果手机底部导航栏高度不清晰，可不用此参数*/
    ){
        Column(Modifier.fillMaxWidth().background(sessionBackgroundColor.copy(0.99f)/*带两分透明*/) ) {
            Row/*水平布局*/(Modifier.statusBarsPadding()/*顶部状态栏边距，防止内容跑到 顶部状态栏 后边被挡住*/  .fillMaxWidth()/*填充全部宽度*/
//                .background(sessionBackgroundColor)/*如果容器布局已设置背景色，再设一层会加重颜色，此布局有自适应系统状态栏边距，再设置一层会导致状态栏后边部分与此布局颜色不同*/
                ,verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/) {
                IconButton/*仅用来包裹图标的按钮，放在按钮里可点击命中区域更大*/(onClick={/*返回图标按钮点击事件*/
                    activity.finish()//退出当前活动
//                activity.onBackPressed()//模拟按下返回键
//                OnBackPressedCallback()/*安卓13以上模拟按下返回键*/
//                ActivityCompat.finishAfterTransition(this as Activity)/*退出当前活动，如果当前界面有过渡动画，等待过渡动画结束后再退出当前活动*/
                }){
                    Icon(imageVector=Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription="返回图标"/*图标描述，必填项，否则报错*/)
                }
                Text(selectedName,/*昵称标题*/ lineHeight=1.sp, modifier=Modifier/*.weight(1f*//*百分百*//*)*//*填充全部宽度*/
                    .clickable{

                    }, fontSize=10.sp, color=MaterialTheme.colorScheme.onSurface/*文字颜色根据主题自适应*/)
                Spacer(Modifier.weight(1f)/*填充全部宽度将 图标按钮 推到右侧*/)/*弹性空间，Modifier本身无weight，weight属性是用于Row、Column布局的子元素上的*/
                IconButton(onClick = {/*消息界面右上角功能菜单按钮点击事件*/
                }){
                    Icon(imageVector=Icons.Default.MoreVert, contentDescription="功能菜单图标")/*功能菜单图标*/
                }
            }
        }


        Column(Modifier.fillMaxSize()) {
            LazyColumn(Modifier
                .background(sessionBackgroundColor)
                .weight(1f)/*填充全部空间将下一个布局推到底部，但不影响其显示，Modifier本身无weight，weight属性是用于Row、Column布局的子元素上的*/
                .fillMaxWidth()/*填充全部宽度*/
            ){
            }

            /*聊天界面底部工具部分*/
            Column(Modifier.background(sessionBackgroundColor.copy(0.9f)).fillMaxWidth()
            ) {
                //HorizontalDivider(color = Color.Gray.copy(0.3f), modifier = Modifier.height(1.dp))/*水平分割线*/
                Column(Modifier.navigationBarsPadding()/*确保内容不被挤到系统导航栏位置，系统导航栏边距*/) {
                    Row(verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/,
                        modifier=Modifier.padding(horizontal=10.dp,vertical=3.dp)/*水平、垂直内边距*/){

                        BasicTextField/*基本编辑框，默认无装饰(下划线、边框)，UI完全自定义*/(
                            value=text,/*值绑定text对象*/onValueChange={text=it}/*值变更时，text对象跟随变更*/,
                            modifier=Modifier
                                .weight(1f)/*weight 1f填充全部控件，把发送按钮推到右侧，填充当前布局全部空间且不影响其它控件，将其它控件推到当前布局末尾，Modifier本身没有weight，weight属性是用于Row、Column布局的子元素上的*/
                                .fillMaxWidth()/*填充全部宽度*/   /*.height(40.dp)*//*高度*/
                                .heightIn(min=35.dp, max=200.dp)/*设置最小和最大高度*/
                                .padding(end=10.dp)/*编辑框 末尾边距*/
                                .background(if(!isSystemInDarkTheme()) Color.White else Color(0xED2B2D30),
                                    RoundedCornerShape(10.dp) )/*设置背景颜色和背景圆角形状*/
                            ,maxLines=Int.MAX_VALUE/*无限行数*/, textStyle/*文本样式*/=LocalTextStyle.current.copy(
                                fontSize=13.sp, lineHeight/*文本行高，决定 光标大小 和 行高度(多行下不可比字体大小少3，否则堆叠在一起)*/=
                                    if(text.contains("\n")/*若包含换行(\n或\r)，为多行文本*/) 12.sp/*多行文本的文本行高必须和字体大小相同或只比字体小1-2，否则会覆盖在一起堆叠*/
                                    else/*其它，单行文本*/ 18.sp,
                                fontWeight/*字体粗细*/=FontWeight.Bold/*Medium中等，Bold粗*/,letterSpacing/*字水平间距*/=0.5.sp,
                                color=MaterialTheme.colorScheme.onSurface/*文本颜色，适应系统深浅模式主体*/
                            ),
                            decorationBox/*装饰盒(包裹文本和光标，必备，也可在BasicTextField外包裹Box代替)*/={innerTextField->
                                Box(Modifier.fillMaxWidth()/*填充全部宽度*/.heightIn(min=31.dp, max=196.dp)/*设置 最小和最大 高度，各比编辑框小4*/
                                    .padding(horizontal=8.dp, vertical=4.dp)/*包裹布局的 水平和垂直 边距*/){
                                    innerTextField()/*放置文本和光标位置*/
                                }
                            },cursorBrush/*光标的颜色和样式*/=SolidColor(MaterialTheme.colorScheme.onSurface)
                        )

                        Button(onClick={/*发送按钮点击事件*/

                        },enabled=text.isNotBlank()||text.contains("\n")/*text 不为空 或 包含换行 则可用*/,
                            shape=RoundedCornerShape(12.dp)/*圆角大小*/,
                            colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF6933CC)/*蓝紫色*/)/*按钮可用时 使用固定颜色(不随系统深浅色模式主题变色)*/,
                            modifier/*必须放在形状属性后边*/=Modifier /*.padding(end=10.dp)*/ /*末尾外边距*/
                                .height(36.dp)
                        ){
                            Text("发送", fontSize=8.5.sp, lineHeight=1.sp/*行间隔高度*/, fontWeight=FontWeight.Bold,
//                                modifier=Modifier.size(8.5.dp,1.dp)/*固定大小宽高，会裁剪组件*/,
                                color=MaterialTheme.colorScheme.onSurface)
                        }
                    }


                }
            }

        }

    }

}

data class MessageItem(
    val id: String,
    var icon: Painter,
    var name: String,
    var message: String
)




/*界面预览*/
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Greeting()
//}