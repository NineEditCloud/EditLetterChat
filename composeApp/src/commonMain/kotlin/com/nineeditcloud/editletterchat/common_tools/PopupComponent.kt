package com.nineeditcloud.editletterchat.common_tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.new_user
import org.jetbrains.compose.resources.painterResource

/*自定义组件-列表项弹窗视图组件 调用很方便*/
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PopupItem(title/*标题*/:String, msg/*消息*/:String, onTap/*点击事件*/:( ()->Unit)?=null,
    popupItemsTitle/*弹窗菜单列表项 标题*/:List<String>, popupItemsUnit /*弹窗菜单列表项 事件*/:List<()->Unit>,
    modifier:Modifier=Modifier, onShowPopup:( (Boolean)->Unit)/*参数回调*/
    ){

    var isContextMenuVisible by rememberSaveable{ mutableStateOf(false) }
//    var pressOffset by remember{ mutableStateOf(DpOffset.Zero) }
    var popupOffset by remember{ mutableStateOf(Offset.Zero) }/*列表子项在容器布局中的坐标，每次赋值会重组发射新位置信息*/
    var itemHeight by remember{ mutableStateOf(0.dp) }
    val interactionSource=remember{ MutableInteractionSource() }
    val density=LocalDensity.current
    Card(elevation=0.dp, modifier=modifier.onSizeChanged{ itemHeight=with(density){ it.height.toDp() } },
        ){
        Box(modifier=Modifier.fillMaxWidth().padding(0.dp)
                .indication(interactionSource,LocalIndication.current)
                .pointerInput(true){/*触摸监听输入*/
                    detectTapGestures(/*点击动作识别*/
                        onLongPress/*长按*/={/*获取点击组件在容器组件中的位置坐标 默认赋值给it*/
//                            pressOffset=DpOffset(it.x.toDp(), it.y.toDp() )
                            popupOffset=it
                            isContextMenuVisible=true
                            onShowPopup(true)/*参数回调返回值*/
                        },
                        onPress/*点击*/={
                            val press=PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press) )
                            if(!isContextMenuVisible)/*若弹窗为关闭状态*/ onTap?.invoke()/*不为空则调用*/
                        },
                        )
                }
           ){
            Row/*水平布局*/(Modifier.fillMaxWidth()/*填充容器全部宽，否则若在Button按钮容器中会默认被放置中间*/.padding(7.dp)/*内边距*/, ){
                Image(painter=painterResource(Res.drawable.new_user)
//                        rememberAsyncImagePainter(model=File("${contactMessageItem.id}.jpg") )/*Image图片资源，加载账号Id对应的头像路径*/
                      ,contentDescription="头像圆角图片",/*Image描述(必填此项，否则报错)*/
                      Modifier.size(45.dp)/*设置图片尺寸*/.clip(RoundedCornerShape(5.dp) )/*设置圆角半径，12.dp为圆形*/
                          .background(Color.LightGray)/*可选：添加背景色，便于观察圆角效果*/,
                      contentScale=ContentScale.Crop/*可选：缩放类型，如裁剪适应*/,
                     )
                Column/*竖直布局*/(Modifier.padding(start=10.dp)/*竖直布局外边距(因为是在Row水平布局中，所以是左边距)*/){
                    /*此布局内是昵称和最新消息 控件*/
                    Text/*昵称文本*/(title, fontSize=10.sp, lineHeight=15.sp,
                        color=MaterialTheme.colorScheme.onSurface/*昵称黑/白色，导航图和列表里的界面必须用MaterialTheme，否则出现不会实时跟随系统深浅主题变色的Bug*/, )
                    Text/*最新消息文本*/(msg, color=Color.Gray/*内容灰色*/, fontSize=8.sp, lineHeight=10.sp)
                }

            }
//            Divider(Modifier.padding(start=80.dp) )/*列表项分割线，已废弃，更名为HorizontalDivider*/
            HorizontalDivider(Modifier.padding(start=80.dp), color=Color.LightGray)/*水平分割线*/
        }

        if(isContextMenuVisible){/*若列表项弹窗状态为打开*/
            Popup(alignment=Alignment.TopStart/*弹窗内容位置*/,
                  onDismissRequest/*点外部关弹窗*/={ isContextMenuVisible=false; onShowPopup(false) },
                  offset=with(density){ IntOffset(x=popupOffset.x.toInt(), y=popupOffset.y.toInt() ) },
//                  properties=PopupProperties(focusable=true, dismissOnBackPress=true, dismissOnClickOutside=true),
                  ){
                Row(Modifier/*.padding(end=10.dp)*/, /*horizontalArrangement=Arrangement.End*//*子项水平靠右*/){
                    val listItemWindowBackground=if(!isSystemInDarkTheme() ) Color.White else Color.Black
                    val windowItemBackground=if(!isSystemInDarkTheme() ) Color.Black else Color.White
                    Row(Modifier.background(listItemWindowBackground,RoundedCornerShape(8.dp) )
                            .clip(RoundedCornerShape(8.dp) )/*裁剪内容为圆角(为使点击涟漪不超出此布局圆角范围)*/
                       ){
                        for(i in 0..< popupItemsTitle.size){/*遍历弹窗列表标题集合的每个索引*/
                            val endPadd=if(i !=popupItemsTitle.size-1)/*若不是最后一个元素*/ 5.dp else 0.dp
                            Text(popupItemsTitle[i]/*标题*/, Modifier.background(windowItemBackground).padding(end=endPadd)
                                .clickable{
                                    popupItemsUnit[i].invoke()/*点击事件*/
                                },
                                 color=listItemWindowBackground,
                                 fontSize=6.sp,
                                 lineHeight=12.sp, )
                        }
                    }

                }
            }
        }

    }

    BackHandler(isContextMenuVisible){/*只在列表项弹窗状态为打开时 拦截返回键 并执行代码*/
        isContextMenuVisible=false/*关闭列表项弹窗*/
    }
}