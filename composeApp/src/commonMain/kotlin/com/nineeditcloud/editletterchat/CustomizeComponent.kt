package com.nineeditcloud.editletterchat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.tbib.compose_toast.AdvToast
import io.github.tbib.compose_toast.rememberAdvToastStates
import kotlinx.coroutines.launch

/*自定义组件*/

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditBox/*多功能自定义_编辑框*/(value:String, onValueChange:(String)->Unit, maxLines:Int=1/*Int.MAX_VALUE无限行数*/,
                                   startIcon/*开头图标*/:@Composable (() -> Unit)?/*接收Comp作为参数 slot模式*/=null,
                                   startText/*开头文本*/:String="",startOnTap:( ()->Unit )?=null,
                                   labelText/*标签文本*/:String="",
                                   endIcon/*结尾图标*/:@Composable (() -> Unit)? =null,
                                   background:Color=Color.Transparent/*编辑框背景 默认透明*/, shape:Shape=RoundedCornerShape(10.dp)/*形状，默认圆角10.dp*/,
                                   borderColor:Color=Color.Transparent/*边框颜色 默认透明*/, underline:Boolean=false/*下划线，默认不用*/, modifier:Modifier=Modifier,
                                   inputType:KeyboardType=KeyboardType.Text/*输入类型 默认文本*/, contentVisualStatus:Boolean=false/*内容可见状态 默认否*/){
    val underlineColor=if(!isSystemInDarkTheme()) Color.Gray else Color.White/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/

    var hasFocus by remember { mutableStateOf(false) }/*编辑框焦点状态 (是否被选中)*/

    val topLabelLeftMargin/*顶部标签背部颜色块 动态左边距*/=if(hasFocus || value!="") 13.dp else 33.dp/*顶部标签动态位置*/
    val topPadding=16.dp
    var minHeight=31.dp/*装饰盒内部 包裹组件的布局 最小高度*/

    /*为什么用不了Modifier的weight属性，并不是依赖版本问题，Modifier本身没有weight属性，weight属性是用于Row、Column等布局中的子元素上的*/

    Row(modifier=modifier) {
        BasicTextField/*基本编辑框，默认无装饰(下划线、边框)，UI完全自定义*/(
            value=value,/*值绑定text对象*/onValueChange=onValueChange/*值变更时，text对象跟随变更*/,
            modifier=Modifier
                .fillMaxWidth()/*填充全部宽度*/.heightIn(max = 200.dp)/*设置最小和最大高度*/
                .onFocusChanged/*焦点变化时*/{ focusState ->
                    hasFocus = focusState.isFocused/*更改焦点状态变量*/
                }
//                .focusRequester(focusRequester)/*可选，用于程序控制焦点*/
            ,maxLines=maxLines/*最大行数*/, keyboardOptions=KeyboardOptions(keyboardType=inputType),
            textStyle/*文本样式*/=LocalTextStyle.current.copy(
                fontSize=13.sp, lineHeight/*文本行高，决定 光标大小 和 行高度(多行下不可比字体大小少3，否则堆叠在一起)*/=
                    if(value.contains("\n")/*若包含换行(\n或\r)，为多行文本*/) 12.sp/*多行文本的文本行高必须和字体大小相同或只比字体小1-2，否则会覆盖在一起堆叠*/
                    else/*其它，单行文本*/ 18.sp,
                fontWeight/*字体粗细*/=FontWeight.Bold/*Medium中等，Bold粗*/,letterSpacing/*字水平间距*/=0.5.sp,
                color=MaterialTheme.colorScheme.onSurface/*文本颜色，适应系统深浅模式主体*/
            ),visualTransformation/*视觉转换*/=if(contentVisualStatus||inputType!=KeyboardType.Password) VisualTransformation.None else PasswordVisualTransformation(),
            decorationBox/*装饰盒(包裹文本和光标，必备，也可在BasicTextField外包裹Box代替)*/={innerTextField->
                Box(Modifier.fillMaxWidth()){
                    Row(Modifier
                        .padding(vertical = topPadding/*边框外的顶部边距，把此布局向内推，边框依旧在内*/)
                        .background(background, shape)/*设置背景颜色和背景圆角形状*/
                        .border(3.dp, borderColor, shape)/*边框*/   ){
                        Row(Modifier
                            .fillMaxWidth()/*填充全部宽度*/.heightIn(
                            min = minHeight, max = 196.dp
                        )/*设置 最小和最大 高度，各比编辑框小4*/
                            .padding(horizontal = 8.dp, vertical = 8.dp)/*包裹组件的布局 水平和垂直 边距*/,
                            verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/){
                            val width=if(startText=="")/*若没有开头文本*/ 34.dp else/*若有开头文本(最多五个数字)*/ 55.dp
                            Box/*包裹开头*/(Modifier
                                .height(34.dp)/*固定高度，防止 不同开头组件改变编辑框高度(编辑框高度会影响下划线底部空隙)*/
                                .width(width)/*动态宽度*/.pointerInput/*识别点击手势(无涟漪效果)*/(
                                    Unit
                                ) {
                                    detectTapGestures(
                                        onTap/*点击*/ = { startOnTap?.invoke()/*如果接收到的开头点击事件不为空则调用*/ }
                                    )
                                },contentAlignment=Alignment.Center/*子项居中*/){
                                startIcon?.invoke()/*开头 图标等组件，?.invoke：如果传递Comp不为空则调用*/
                                if(startText!=""){
                                    Text/*开头文本*/(text=startText,fontSize=12.sp,fontWeight=FontWeight.Bold,
                                        lineHeight/*行高*/=0.1.sp,letterSpacing/*字间隔*/=0.sp, color=MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            Column/*包裹内容和标签*/(Modifier.weight(1f)/*占用全部空间把剩余内容推到结尾*/,
                                verticalArrangement=Arrangement.Center/*因为此布局占据了全部高度，所以子项垂直居中*/){
                                if(value!="" || hasFocus){/*内容不为空 或 编辑框被选中*/
                                    innerTextField()/*放置文本和光标位置*/
                                    minHeight=35.dp
                                }else{/*内容为空 并且 编辑框未被选中*/
                                    Lable(labelText)/*使用定义好的标签*/
                                    minHeight=31.dp
                                }
                                if(underline){/*如果启用下划线*/
                                    HorizontalDivider/*水平分割线*/(Modifier.height(1.dp).padding(top = 2.dp, bottom = 2.dp),
                                                      color=underlineColor.copy(0.7f) )
                                }
                            }
                            endIcon?.invoke()/*结尾图标*/
                        }
                    }

                    Box/*堆叠布局，为方便只设一次左边距 再套层Box*/(Modifier.padding(start=topLabelLeftMargin/*水平开头 动态边距*/) ){
                        Row(Modifier
                            .padding(top = topPadding)/*契合 编辑框顶部边距(编辑框预留给标签的空间)*/.height(
                                4.dp
                            )
                            .background(background)
                            .widthIn(min = 3.dp)/*最小宽度，内部标签显示时自适应增加宽度*/ ){
                            if(value!="" || hasFocus)/*内容不为空 或 编辑框被选中 时*/ Lable(labelText)/*使用定义好的标签，以自适应标签宽度*/
                        }/*占据文字所在部分背部边框颜色，以改变样式*/
                        if(value!="" || hasFocus)/*内容不为空 或 编辑框被选中 时*/ Lable(labelText)/*使用定义好的标签*/
                    }
//                    Row(Modifier.align(Alignment.BottomStart)/*此布局在Box中 居底 开头*/.height(3.dp)/*高度*/.width(3.dp)/*宽度*/
//                        .padding(start=3.dp)/*水平开头边距*/.background(background) ){
//                    }/*占据边框底部 部分颜色，以改变样式*/
                }
            },cursorBrush/*光标的颜色和样式*/=SolidColor(MaterialTheme.colorScheme.onSurface)
        )
    }

}

@Composable
fun Lable(text:String){
    return Text(text,fontSize=13.sp,lineHeight=1.sp,fontWeight=FontWeight.Bold/*字体粗细 粗*/, color=MaterialTheme.colorScheme.onSurface)
}

@Composable
fun DecorationBox/*装饰盒*/(){

}

@Composable
fun FixedSizeText/*不受系统字体大小缩放的文本*/(text:String, fontSize:Dp, lineHeight:TextUnit=1.sp, fontWeight:FontWeight?=null, modifier:Modifier=Modifier,
                                                color:Color=Color.Unspecified, textAlign:TextAlign?=null){
    val density=LocalDensity.current
    val scaledFontSize=with(density){
        (fontSize/fontScale).toSp()/*抵消系统字体缩放*/
    }
    Text(text=text, fontSize=scaledFontSize, lineHeight=lineHeight, fontWeight=fontWeight, color=color, modifier=modifier, textAlign=textAlign)
}

@Suppress("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyToast/*简易Toast(放在堆叠布局最顶层)*/(message:String, ui/*自定义UI*/:@Composable (() -> Unit)?/*接收Comp作为参数 slot模式*/=null){
    val stateCustomToast = rememberAdvToastStates()
    val coroutineScope = rememberCoroutineScope()
    /*使用MakeCustomToast定义样式*/
    AdvToast.MakeCustomToast(state=stateCustomToast, textColor=MaterialTheme.colorScheme.onSurface,
//        paddingTop = 50.dp,/*调整位置到顶部*/
        modifier=Modifier/*.padding(horizontal=50.dp)*/
            .background(Color.Cyan, shape=RoundedCornerShape(8.dp)), /*align=Arrangement.Top*/)
    coroutineScope.launch{ stateCustomToast.show(message) }
}

