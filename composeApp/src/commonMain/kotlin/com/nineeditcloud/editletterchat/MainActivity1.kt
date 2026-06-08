package com.nineeditcloud.editletterchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable/*布局单击、双击、长按*/
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.nineeditcloud.editletterchat.common_tools.Log
import com.nineeditcloud.editletterchat.common_tools.PopupItem
import com.nineeditcloud.editletterchat.common_tools.filesPath
import com.nineeditcloud.editletterchat.database.AccountFriendLocalData
import com.nineeditcloud.editletterchat.database.UserAccountLocalData
import com.nineeditcloud.editletterchat.database.getDatabase
import compose.icons.Octicons
import compose.icons.octicons.DeviceCamera16
import compose.icons.octicons.File16
import compose.icons.octicons.Image16
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.cover07
import editletterchat.composeapp.generated.resources.name_edit
import editletterchat.composeapp.generated.resources.new_user
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

/*首页 导航图 界面*/

val userAccountDBTableDao=getDatabase("userAccount_localData")/*获取 用户账号本地数据 数据库实例*/.userAccountDao()/*获取数据库中的 已登录账号本地数据 表Dao*/
var accountData:UserAccountLocalData?=null/*账号数据 默认值*/
var selectedFriend:AccountFriendLocalData?=null/*好友数据 默认值*/

var imagePath:String=""
var backgroundColor:Color=Color.White/*全局背景色初始化值*/

var navController:NavHostController?=null
var currentRoute:String?=null/*当前导航页获取结果 初始值，equals(比较)扩展函数支持String?类型*/

var exitApp:( ()->Unit)={}/*全局默认空实现，避免未注入时崩溃*/

@OptIn(ExperimentalComposeUiApi::class)
class MainActivity1:Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(){
        val lifecycleOwner=LocalLifecycleOwner.current/*lifecycle协程，绑定 Activity(活动) 或 Fragment(界面片段) 生命周期*/
        lifecycleOwner.lifecycleScope.launch{/*协程作用域*/
            accountData=userAccountDBTableDao.getCurrentUseAccountIdByCurrentUse()/*获取当前使用账号数据，不存在时返回null*/
        }
        Log.msg("ELC账号数据",accountData.toString() )/*输出LogCat消息日志*/

        val drawerState/*抽屉状态对象 每次改变自动重组刷新发射*/=remember{ DrawerState(DrawerValue.Closed) }
        val scope=rememberCoroutineScope()/*协程作用域(抽屉控制器操作执行工具)*/

        navController=rememberNavController()/*NavHost导航图控制器对象(导航图和导航栏共用同一个导航控制器，实现控制导航图)*/
//        var presses by remember{ mutableIntStateOf(0) }

        val navItems=listOf(/*导航项信息列表，底部导航栏内容(放进主题块中 或主题块调用的部分 否则选中状态不变色)*/
                            NavItem("消息", "message", Icons.Default.Home, badgeCount=0,"用户名"),
                            NavItem("联系人", "contact", Icons.Default.Person, badgeCount=23,"联系人"),
                            NavItem("动态", "dynamic", Icons.Default.Favorite, badgeCount=100,"动态"),
                           )

        val navBackStackEntry by navController!!.currentBackStackEntryAsState()/*创建NavBSE对象，绑定navHostController导航控制器对象(不为空则调用)*/
        currentRoute=navBackStackEntry?.destination?.route/*获取当前导航页 字符串 并赋值给全局变量(可随处访问)，用于顶部标题栏 动态给出对应内容*/

        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigator跨平台Screen界面导航 绑定当前界面的导航控制器*/

        /*这部分是把好友头像保存到本地，若正式上架的话要把头像改成遍历所有接收到此账号好友的头像*/
        /*获取外部私有 文件路径和缓存路径
        应用外部文件路径：/storage/emulated/0/Android/data/包名/files
        应用外部缓存路径：/storage/emulated/0/Android/data/包名/cache
        安卓下Context参数 要在主函数处用this表示 那个调用当前类的 Context上下文对象，其它可用LocalContext.current创建Context上下文对象*/

        /*以下两行是用FileKit获取私有路径的错误示例*/
//        val filesPath:PlatformFile?=(FileKit.filesDir ?.absolutePath()?.isNotBlank() ) as PlatformFile?/*获取应用私有文件目录，   若获取失败则为空，错误示例(返回值Boolean强制转换为PlatformFile?导致闪退)*/
//        val cachePath:PlatformFile?=(FileKit.cacheDir ?.absolutePath()?.isNotBlank() ) as PlatformFile?/*获取应用私有临时缓存目录，若获取失败则为空，错误示例(返回值Boolean强制转换为PlatformFile?导致闪退闪退)*/
        if(filesPath()!=null){/*若获取到了 外部私有文件绝对路径*/
            val dir=filesPath/"avatar"/*目标文件夹*/
            dir.createDirectories()/*创建不存在的文件夹及其不存在的父目录 行为幂等(Idempotent)，若文件夹存在 什么都不执行 也不要报错*/
//            if(!dir.exists()){/*若文件夹不存在，(!代表反面，文件夹存在的反面)，其实FileKit会自行判断(不存在时自动创建)，若要判断是否为已存在还是刚创建 可添加判断*/
//                dir.createDirectories()/*创建文件夹，否则在不存在此文件夹的情况下，向不存在的目录保存文件会报错并闪退*/
//            }
            val imageFile=dir/"new_user.png"/*目标图片文件*/

            val imageBitmap:ImageBitmap=imageResource(Res.drawable.new_user)/*处理图片(Compress & Save)，假设已在 Composable 函数中获取到 ImageBitmap*/

            CoroutineScope(Dispatchers.Default).launch {/*在协程中执行图片保存操作*/
                val imageBytes=imageBitmap.encodeToByteArray(format=ImageFormat.PNG, quality=90)/*将ImageBitmap 编码为字节数组*/
                val compressedBytes=FileKit.compressImage/*压缩图片(可选步骤，如不需要可直接写入原始字节)*/(bytes=imageBytes, quality=90,
                                                          imageFormat=ImageFormat.PNG)
                imageFile.write(compressedBytes)/*将压缩后的图片数据写入文件*/
                imagePath=imageFile.path/*获取文件路径*/
            }
        }

        /*监听是否为长按状态，由于combinedClickable闪退Bug，所以写一个 按下/抬起监听 让超文本按钮onClick判断*/
//        var lastDownTime by remember { mutableLongStateOf(0L) }

        var expanded/*顶部菜单列表状态*/ by remember{ mutableStateOf(false)/*默认关闭状态*/ }
        val drawerDensity=LocalDensity.current/*抽屉Density*/
        val systemBars=WindowInsets.systemBars/*抽屉需要的*/
        val bottomBarInsets=systemBars.getBottom(drawerDensity)/*抽屉需要的*/

        backgroundColor=if(!isSystemInDarkTheme() ) Color(0xFFEEF2FD) else Color(0xFF1C1E1F)/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/
        val drawerBackgroundColor=if(!isSystemInDarkTheme()) Color.White else Color(0xFF1C1E1F) /*抽屉背景色*/
        val topCoverBackground=painterResource(Res.drawable.cover07)

        var listShowPopup/*接收 导航图代码方法-参数回调值的弹窗状态*/ by remember{ mutableStateOf(false)/*默认关闭状态*/ }/*值变化自动重组发射新值 用于判断作决定*/
        Box(Modifier.fillMaxSize().background(backgroundColor)/*.semantics(mergeDescendants=true){}*//*合并子组件语义*/
           ){
            ModalNavigationDrawer/*左侧抽屉，会自动适应系统 顶部状态栏和底部导航栏 部分的边距*/(
                drawerState/*绑定抽屉状态对象*/=drawerState, gesturesEnabled/*手势功能启用*/=true,
                drawerContent/*抽屉内容*/={
                    ModalDrawerSheet/*模态抽屉模板(自带与状态栏、导航栏的边距，不要在有顶部状态栏边距时放入顶部封面背景图)，若无此组件会导致点抽屉任意区域都关抽屉*/(
                        Modifier.fillMaxSize(),
                        windowInsets=WindowInsets(top=0,left=0,right=0,bottom=bottomBarInsets),/*关掉与顶部状态栏的边距*/
                        drawerContainerColor=Color.Transparent/*抽屉模板颜色透明*/){
                        Box/*堆叠布局*/(Modifier.fillMaxSize() ){
                            Image/*用户顶部封面图*/(topCoverBackground,contentDescription="",
                                Modifier.fillMaxWidth()/*占据全部容器宽度*/.align(Alignment.TopCenter)/*在Box中 垂直居顶 水平开头*/,
                                contentScale=ContentScale.FillWidth/*拉伸至组件宽度(高度按拉伸比例自适应)，自动调整图片组件高度*/
                            )
//                            CompositionLocalProvider(LocalConfiguration provides fixedConfiguration){/*使用固定字体缩放大小的组件，不受系统字体大小影响*/
//                            }

                            Column(Modifier.fillMaxSize()) {
                                Box/*用户卡片部分堆叠布局*/(Modifier.fillMaxWidth().padding(top=180.dp)/*顶部边距*/){
                                    Column(Modifier.fillMaxWidth()) {
                                        Column(Modifier.fillMaxWidth().height(40.dp)) { }/*填充Row水平布局名片外边距部分后边的上一半为透明*/
                                        Column(Modifier.fillMaxWidth().background(drawerBackgroundColor).height(40.dp)/*填充剩余的所有空间，但不影响其它组件空间*/) { }/*填充Row名片下一半及下面的全部 为抽屉背景颜色*/
                                    }
                                    Row/*水平布局*/(Modifier.fillMaxWidth().padding(horizontal=20.dp)/*水平外边距*/.height(80.dp)
                                                        .background(
//                                                            brush=Brush.horizontalGradient(listOf(Color.Gray,Color.White) )/*水平渐变色(灰渐变白)*/,
                                                            drawerBackgroundColor,RoundedCornerShape(12.dp)/*圆角背景*/,
                                                            )
//                                                        .align(Alignment.BottomCenter)/*子项对齐方式，垂直居底 水平居中*/
//                                                        .clip(RoundedCornerShape(8.dp))/*裁剪内容为圆角(包括子组件)，并不波及当前组件背景为圆角*/
                                                        .border(1.dp,Color.Gray.copy(0.3f),RoundedCornerShape(12.dp))/*圆角显0.3灰色边框*/
                                                   ){
                                        Icon(painterResource(Res.drawable.new_user), contentDescription="用户头像图片",
                                             Modifier.size(80.dp).padding(10.dp).clip(RoundedCornerShape(50.dp))/*裁剪内容(包括此组件的图片)为圆角*/)
                                        Column/*竖直布局*/(Modifier.fillMaxWidth().padding(end=5.dp)/*水平结尾边距，防止内容贴紧边框*/ ){
                                            Row(verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                Text("用户昵称", fontSize=15.sp, lineHeight=1.sp, modifier=Modifier.width(100.dp), color=MaterialTheme.colorScheme.onSurface)/*字体大小和行高度必须用sp，否则报错*/
                                                Row(Modifier.clip(RoundedCornerShape(8.dp)).clickable{

                                                }.border(1.dp, color=Color.Gray,RoundedCornerShape(8.dp) )/*画灰色圆角边框*/
                                                        .padding(start=10.dp)/*原来padding是内边距(当前布局背景跟随内边距向内推，所以也可当外边距用，但边框依旧在内边距外)*/
                                                    ,verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/){
                                                    Text("切换账号", fontSize=10.sp, lineHeight=1.sp, color=MaterialTheme.colorScheme.onSurface)
                                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription="切换账号图标")
                                                }
                                            }
                                            Text("个性签名", fontSize=11.sp, lineHeight=1.sp, color=Color.Gray/*灰色*/)
                                            Row(verticalAlignment = Alignment.CenterVertically/*子项垂直居中*/) {
                                                Icon(Icons.Default.DateRange, contentDescription = "等级图标",
                                                     Modifier.size(15.dp))
                                                Text("等级：*", fontSize = 11.sp, lineHeight = 1.sp, color = MaterialTheme.colorScheme.onSurface)
                                                Icon(Icons.Default.ShoppingCart, contentDescription = "会员图标",Modifier.padding(start = 10.dp).size(15.dp))
                                                Text("会员：未知", fontSize = 11.sp, lineHeight = 1.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }
                                Column(Modifier.fillMaxWidth().background(drawerBackgroundColor)){
//                                    HorizontalDivider()/*功能项分割线，Divider()已废弃，更名为HorizontalDivider()*/
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("钱包", color=MaterialTheme.colorScheme.onSurface) }, selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/{ drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(Icons.Default.Lock, contentDescription="钱包图标") },
                                        shape=RectangleShape/*设置直角*/,
                                        )
                                    NavigationDrawerItem/*导航项*/(
                                        label={Text("收藏", color=MaterialTheme.colorScheme.onSurface) }, selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/{ drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon = { Icon(Icons.Default.Star, contentDescription="收藏图标") },
                                        shape = RectangleShape/*设置直角*/,
                                        )
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("文件", color=MaterialTheme.colorScheme.onSurface) }, selected = false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/{ drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(Octicons.File16, contentDescription="文件图标",
                                                    Modifier.size(25.dp)) },
                                        shape=RectangleShape/*设置直角*/,
                                        )
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("相册",color=MaterialTheme.colorScheme.onSurface) },
                                        selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/ { drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(Octicons.Image16, contentDescription="相册图标",
                                                    Modifier.size(25.dp)) },
                                        shape=RectangleShape/*设置直角*/,
                                        )
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("笔记", color=MaterialTheme.colorScheme.onSurface) },
                                        selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/ { drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(Icons.Default.Edit, contentDescription="笔记图标",Modifier.size(25.dp)) },
                                        shape=RectangleShape/*设置直角*/,
                                        )

                                    Spacer(Modifier.weight(1f)/*填充全部高度将Row推到底部*/)/*弹性空间*/
                                    Row{
                                        Column(horizontalAlignment = Alignment.CenterHorizontally/*子项水平居中*/, modifier = Modifier.padding(horizontal = 20.dp)) {
                                            Icon(Icons.Default.Settings, contentDescription="设置图标")
                                            Text("设置", fontSize = 11.sp, lineHeight = 1.sp,color=MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally/*子项水平居中*/, modifier = Modifier.padding(horizontal = 20.dp)) {
                                            Icon(Icons.Default.Home, contentDescription="主题图标")
                                            Text("个性主题", fontSize = 11.sp, lineHeight = 1.sp,color= MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally/*子项水平居中*/, modifier = Modifier.padding(horizontal = 20.dp)) {
                                            Icon(Icons.Default.LocationOn, contentDescription="地区天气")
                                            Text("地区天气", fontSize = 11.sp, lineHeight = 1.sp,color= MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }

                            }

                        }

                    }

                }, ){/*抽屉外的界面*/
                Box(Modifier.fillMaxSize() ){
                    Scaffold/*脚手架*/(
                        topBar/*顶部栏*/={
                            TopAppBar/*顶部应用栏*/(
                                colors/*样式*/=topAppBarColors(
                                    containerColor=backgroundColor/*背景色，浅色主题下白色*/,
                                    titleContentColor=MaterialTheme.colorScheme.onSurface/*标题色，浅色主题下黑色*/,
                                    ),
                                navigationIcon/*顶部左侧导航项图标按钮*/={
                                    IconButton/*顶部左侧导航 图标按钮*/(
                                        onClick={
                                            scope.launch { drawerState.open()/*启动左侧抽屉*/ }
                                        },
                                    ){
                                        Icon(imageVector/*顶部左侧导航按钮图标*/=Icons.Default.Person, contentDescription="用户头像，打开名片抽屉",
                                             Modifier.size(40.dp) )
                                    }
                                },
                                title/*顶部应用栏的标题控件集*/={
//                                    AnimatedVisibility(visible=currentRoute.equals("message") ){/*按条件定义是否可见*/
//                                        Text("用户昵称")
//                                    }

                                    Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/){
                                        if(currentRoute.equals("message") ){/*如果导航图是在导航消息界面*/
                                            Column/*竖直布局*/(
                                                Modifier.combinedClickable(
                                                    onClick={/*单击事件*/

                                                    },
                                                    onLongClick={/*长按事件*/

                                                    },
                                                ),
                                            ){
                                                Text("用户昵称", fontSize=10.sp/*字体大小*/, lineHeight=1.sp/*控件行间隔高度*/)
                                                Row/*水平布局*/(verticalAlignment=Alignment.CenterVertically/*子控件垂直居中对齐*/){
                                                    Icon(imageVector=Icons.Default.AddCircle, contentDescription="状态图标", Modifier.size(12.dp))
                                                    Text("状态", fontSize=8.sp/*字体大小*/,
//                                                         modifier=Modifier.border(width=1.dp, color=Color.Blue, shape=RoundedCornerShape(8.dp)),//绘制边框查看偏移问题
                                                         lineHeight=1.sp/*文本行间隔高度*/)
                                                }
                                            }
                                        }else{/*导航图在导航其它界面*/
                                            Text(text=
                                                     if(currentRoute.equals("contact")) "联系人"/*如果导航图在导航联系人界面*/
                                                     else /*if (currentRoute.equals("dynamic"))*/ "动态"/*如果导航图在动态界面*/,
                                                 fontSize=15.sp/*字体大小*/, lineHeight=1.sp/*文本行间隔高度*/,
                                                 )
                                        }

                                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.End/*子项居右*/) {
                                            Icon(Icons.Default.Search, contentDescription="搜索图标", Modifier.clickable{/*搜素图标点击事件*/

                                            }.padding(horizontal=5.dp).size(30.dp)  )
                                            Icon(Icons.Default.Menu, contentDescription="功能菜单图标", Modifier.clickable{/*功能菜单点击事件*/
                                                expanded=!expanded/*打开或关闭功能菜单列表*/
//                                                if(!expanded) expanded=true else expanded=false
                                            }.padding(horizontal=5.dp).size(30.dp), )
                                        }


                                    }

                                },
                                )
                        },
                        bottomBar/*底部栏*/={
                            NavigationBar(Modifier.height(111.dp), containerColor=backgroundColor/*底部导航栏背景色*/ ){/*导航栏*/
                                navItems.forEach/*遍历items*/{ navItem/*每次赋值给新建navItem变量*/ ->
                                    NavigationBarItem(
                                        icon/*图标集*/={

                                        },

                                        label/*标签集*/={
                                            BadgedBox/*徽章布局 给图标旁加徽章*/(
                                                badge={/*徽章集*/
                                                    if (navItem.badgeCount > 0){/*判断徽章消息数如果大于0则执行*/
                                                        Badge/*徽章*/(containerColor=Color.Red/*徽章背景颜色*/, contentColor=Color.White/*徽章内控件颜色*/){
                                                            Text(
                                                                text=if (navItem.badgeCount > 99) "99+"/*徽章消息数显示上限*/
                                                                else navItem.badgeCount.toString()/*99以下显示完整消息数*/,
                                                                fontSize=8.sp/*徽章字体大小*/, lineHeight=1.sp/*文本行间隔高度*/
                                                                )
                                                        }
                                                    }
                                                },
                                                ){/*徽章布局内的其它组件*/
                                                Column(horizontalAlignment=Alignment.CenterHorizontally/*子内容水平居中对齐*/){
                                                    Icon/*图标*/(imageVector=navItem.icon,contentDescription=navItem.title,
                                                                 Modifier.size(23.dp)/*图标大小*/)
                                                    Text/*标签*/(navItem.title, fontSize=8.sp/*标签字体大小*/, lineHeight=1.sp/*文本行间隔高度*/,
                                                                 color=if(currentRoute==navItem.route) Color(0xFF6933CC) /*导航图导航页符合导航项为蓝紫色(0xFF6933CC)，Magenta为紫色*/
                                                                 else MaterialTheme.colorScheme.onSurface/*未导航此导航项页面则为该主题下文字颜色*/
                                                                )
                                                }

                                            }
                                        },

                                        selected=currentRoute==navItem.route,/*是否为选中状态，判断当前导航页是否符合此导航项*/
                                        onClick={/*导航项点击事件*/
                                            navController!!.navigate(navItem.route)/*导航控制器(不为空则调用) 使用对应导航页*/{
                                                /*配置导航动作行为*/
                                                popUpTo/*导航前弹出回退栈中的片段*/(navController!!.graph.startDestinationId/*获取导航图起始页面*/) { saveState=true/*保存弹出的片段状态，以便下次导航依旧是保存的状态*/ }
                                                launchSingleTop=true//单顶模式(SingleTop)重要配置：如果目标页面已在回退栈的顶部，就不创建新实例，而是重用现有实例
                                                restoreState=true//当导航目标已访问过且其状态被保存，则自动恢复该页面状态
                                            }
                                        },
                                        )
                                }
                            }

                        },

                        floatingActionButton/*浮动按钮*/={
                            FloatingActionButton(onClick={/*presses++*/

                            }, ){/*包含组件*/
                                Icon(Icons.Default.Add, contentDescription="添加图标")/*图标*/
                            }
                        },
                        ){ innerPadding/*用来适应顶部栏和底部栏的边距(没有水平边距)，防止界面中间内容被顶部栏和底部栏遮挡*/ ->
                        /*脚手架中间内容*/

                        Box(Modifier.fillMaxSize() ){
                            Column/*竖直布局*/(verticalArrangement=Arrangement.spacedBy(16.dp),
                                               modifier=Modifier.padding(innerPadding).background(backgroundColor)
                                          ){
                                /*放置导航图(内嵌界面加载)*/
                                Nav(navController!!){
                                    listShowPopup=it/*将导航图代码方法的 参数回调值 赋值给listShowPopup列表弹窗状态变量*/
                                }

                            }

                            @Suppress("DEPRECATION")
                            BackHandler/*拦截返回键*/{
                                if(drawerState.isClosed)/*如果抽屉是关闭状态)*/
                                    if(expanded){/*若有弹窗视图是打开状态*/
                                        expanded=false
                                    }else/*否则，弹窗是关闭状态*/
                                        if(navigator.canPop)/*若回退栈中有上个界面*/ navigator.pop()/*Voyager导航返回上个界面*/
                                        else{
//                                            exitProcess(0)/*无法回退界面时暴力结束进程(不确定在某些条件下是否会导致发生问题)，Koltin/Native 中不可用*/
                                            exitApp.invoke()
                                        }
                                else/*否则，抽屉是打开状态*/
                                    scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/ { drawerState.close()/*关闭抽屉*/ }
                            }

                            if(expanded){/*若顶部菜单弹窗视图状态为打开*/
                                /*和Popup和DropdownMenu的好处在于根据 点击输入位置弹出，适合会动的布局或控件 定位弹窗视图，将其放进脚手架中间部分适合*/
                                Popup/*Popup自定义弹窗视图(DropdownMenu底层实现)，当需求超出DropdownMenu标准范畴，可自定义*/(
                                    alignment=Alignment.TopEnd/*居顶靠右*/, offset=IntOffset(0,56)/*调整弹出菜单位置*/,
                                    onDismissRequest={expanded=false}/*点击外部区域则关闭菜单*/, ){
                                    /*自定义菜单项内容*/
                                    /*注：弹窗视图不能用脚手架中间的边距(由于视图弹出时不在脚手架内)，否则会向下错位*/
                                    Row/*自定义布局以实现位置和外边距*/(Modifier.fillMaxSize()
                                        .pointerInput(Unit){/*指针输入(默认无按下涟漪)*/
                                            detectTapGestures/*检测点击手势，包括按下、点击、长按、双击等*/(
                                                onPress/*按下事件*/={
                                                    expanded=false/*关闭弹窗视图*/
                                                    },
                                            ){/*综合触摸事件*/
                                            }
                                        }
                                        .background(Color.Black.copy(0.4f) )/*背景黑色 透明(显示0.3 30%)*/,
                                        horizontalArrangement=Arrangement.End/*子项靠右*/){
                                        Column/*为不影响菜单外整页显示阴影大小，中间加个布局用来写内边距，(由于Compose的内边距会把当前布局的背景向内推)*/(Modifier
                                            .padding(top=50.dp,end=10.dp)/*顶部和右侧内边距，防止遮挡顶部应用栏，以及右侧内边距*/
                                            .fillMaxWidth(0.4f) ){
                                            Column(Modifier /*.align(Alignment.TopEnd)*/ /*居顶靠右*/    .width(180.dp)
                                                       .background(backgroundColor,RoundedCornerShape(13.dp) )
                                                       .clip(RoundedCornerShape(13.dp))/*裁剪内容为圆角，以待子项最上一行和最下一行点击涟漪为圆角(必须在点击事件前裁剪)*/
                                                       .fillMaxWidth(0.4f),
                                                   ){
                                                Row(Modifier.fillMaxWidth().height(50.dp)
//                                                        .clip(RoundedCornerShape(topStart = 13.dp, topEnd = 13.dp))/*裁剪顶部为圆角，以待点击圆角涟漪(必须在点击事件前裁剪)*/
                                                        .clickable{/*创建群聊选项点击事件*/
                                                            navigator.push(CreateGroupChat())/*跳转 创建群聊界面*/
                                                        }/*.background(Color.White,RoundedCornerShape(topStart = 13.dp, topEnd = 13.dp))*//*背景白色，顶部圆角*/,
                                                    verticalAlignment=Alignment.CenterVertically/*子项垂直居中对齐*/, ){
                                                    Icon(painterResource(Res.drawable.name_edit), contentDescription="创建群聊图标",
                                                         Modifier.padding(horizontal = 10.dp))
                                                    Text("创建群聊", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
                                                HorizontalDivider(Modifier.padding(start=40.dp,top=0.dp), color=Color.LightGray)/*水平分割线*/
                                                Row(Modifier.fillMaxWidth().height(50.dp).clickable{/*添加好友选项点击事件*/
                                                    navigator.push(Add_FriendAndGroupChat())/*跳转 添加好友和群聊界面(添加联系)*/
                                                }, verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                    Icon(painterResource(Res.drawable.new_user), contentDescription="添加 好友/群 图标",
                                                         Modifier.padding(horizontal = 10.dp))
                                                    Text("加好友/群", /*lineHeight = 1.sp,*/ fontSize=10.sp, color=MaterialTheme.colorScheme.onSurface)
                                                }
                                                HorizontalDivider(Modifier.padding(start=40.dp,top=0.dp), color=Color.LightGray)/*水平分割线*/
                                                Row(Modifier.fillMaxWidth().height(50.dp).clickable{/*扫一扫选项点击事件*/
                                                    navigator.push(ScanQRCode())/*跳转 扫一扫界面*/
                                                }, verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                    Row(Modifier.padding(horizontal = 10.dp)) {
                                                        Icon(imageVector=Octicons.DeviceCamera16, contentDescription="扫一扫图标",
                                                             Modifier.size(25.dp))
                                                    }
                                                    Text("扫一扫", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
                                                HorizontalDivider(Modifier.padding(start=40.dp,top=0.dp), color=Color.LightGray)/*水平分割线*/
                                                Row(Modifier.fillMaxWidth().height(50.dp)
//                                                        .clip(RoundedCornerShape(bottomStart = 13.dp, bottomEnd = 13.dp))/*裁剪底部为圆角，以待点击圆角涟漪(必须在点击事件前用)*/
                                                        .clickable{/*收付款选项点击事件*/
                                                            navigator.push(PaymentAndReceipt())/*跳转 收付款界面*/
                                                        }/*.background(Color.White, RoundedCornerShape(bottomStart = 13.dp, bottomEnd = 13.dp))*//*背景白色，底部圆角*/,
                                                    verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                    Row(Modifier.padding(horizontal = 10.dp)) {
                                                        Icon(Icons.Default.Check, contentDescription = "收付款图标",
                                                             Modifier.border(2.dp, Color.Black,RoundedCornerShape(5.dp))/*由于图标很小，所以尽量把变宽设细，圆角设小，否则变成圆了*/
                                                                 .size(25.dp))
                                                    }
                                                    Text("收付款", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
//                                                DropdownMenuItem(onClick = {}, text = {Text("选项")})/*内容选项默认排列(没什么用处，不如直接写布局)*/
                                            }
                                        }

                                    }

                                }
                            }





                        }
                    }

                }

            }





            //        if (buttonBounds!=null){
            //            val receiver=LocalDensity.current
            //            DropdownMenu/*DropdownMenu特定标准菜单模板 弹窗视图*/(
            //                expanded=listItemWindowExpanded,/*状态条件绑定，以及发起弹窗视图的按钮坐标不为空*/
            //                onDismissRequest={ listItemWindowExpanded=false },/*点击外部则关闭弹窗视图*/
            //                offset=DpOffset(/*设置菜单偏移，对齐到按钮右下角*/
            //                    x = with(receiver) {
            //                        buttonBounds!!.right.toDp() /*计算X轴偏移，使菜单右边缘对齐按钮右边缘，-48，但留出10dp空隙边距*/
            //                    },
            //                    y = with(receiver) {
            //                        buttonBounds!!.bottom.toDp()/*计算Y轴偏移，使菜单紧贴按钮下方，若为调整不贴在上面，+10dp*/
            //                    }
            //                ),
            //                modifier = Modifier
            ////                    .width(180.dp)/*固定菜单宽度*/
            ////                    .clip(RoundedCornerShape(13.dp))/*裁剪表面内容为圆角*/
            ////                    .background(Color.White/*RoundedCornerShape(13.dp),*/ /*最好不设圆角，DropdownMenu有默认背景和圆角，会两层圆角*/)//背景
            ////                    .border(/*边框*/width = 1.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
            ////                    .shadow(/*阴影*/elevation = 8.dp,shape = RoundedCornerShape(8.dp))
            ////                    .padding(end = 10.dp)/*写了也没用*/
            //            ){/*自定义菜单项内容*/
            //            }
            //        }



        }
    }


    @Composable
    fun Nav(navController:NavHostController, onListShowPopup:(Boolean)->Unit={}/*参数回调(其实不必)*/ ){
        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigator跨平台Screen界面导航 绑定当前界面的导航控制器*/

        var showPopup by remember{ mutableStateOf(false) }/*列表项弹窗状态*/
        var popupOffset by remember{ mutableStateOf(Offset.Zero) }/*列表子项在容器布局中的坐标，每次赋值会重组发射新位置信息*/
        val density=LocalDensity.current/*列表项弹窗位置控制*/
        NavHost/*导航图主体组件*/(navController=navController,/*绑定导航控制器*/ startDestination="message"/*初始导航界面*/,
                                  Modifier.fillMaxSize() ){/*改导航界面路由后，不要忘了改初始导航页界面路由名称*/
            composable("message"){/*消息界面*/
                Box(Modifier.fillMaxSize()
                    , ){
                    val listState=rememberLazyListState()/*LazyList有序列表状态*/
                    val listAlreadyExistsFriendItem=mutableSetOf<String>()/*列表已存在好友项 记录集合*/
                    val contactMessageItems=remember{ mutableStateListOf<AccountFriendLocalData>() }/*在任何地方调用add/remove/addAll 都会自动触发LazyColumn列表重组的 列表项集合*/

                    if(accountData!=null){/*若当前账号数据不为空*/
                        val currentAccount_FriendDBTableDao=getDatabase("${accountData!!.id}friend")/*获取 当前账号(不为空则调用)好友本地数据 数据库实例*/.friendDao()/*获取数据库中的 好友表Dao*/
//                        val allFriendsFlow by currentAccount_FriendDBTableDao.getAllFriend_Flow()/*获取当前账号好友数据库表中所有数据 Flow(数据变化自动发射新数据)*/.collectAsState(initial=emptyList()/*初始值*/ )

                        val scope=rememberCoroutineScope()
                        val lifecycleOwner=LocalLifecycleOwner.current/*lifecycle协程，绑定 Activity(活动) 或 Fragment(界面片段) 生命周期*/
//                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO/*数据库、通信必须在输入输出流线程执行 否则切换导航界面时会崩溃*/){
//                            Log.msg("Room数据库","已获取数据库：${accountData!!.id}friend")/*输出LogCat消息日志*/
//                            val allFriends=currentAccount_FriendDBTableDao.getAllFriend()/*获取当前账号好友数据库表中所有数据*/
//                            /*contactMessageItems列表项集合 和 Room返回的表数据集合 元素必须用同一个对象类型*/
//                            allFriends.forEach{/*遍历List集合元素，默认每次赋值给it*/
//                                contactMessageItems.add(it)/*列表项集合添加元素*/
//                                listAlreadyExistsFriendItem.add(it.id)/*在 列表已存在好友项记录集合中 添加对应好友ID*/
//                            }
//                            tcpLongConnClient(accountData!!.id, accountData!!.token){
////                                if(it["type"]==""){
////                                }
//                            }
//                        }
                        contactMessageItems.addAll(/*初始化列表项集合*/
                                listOf(/*假设收到的最新每一条消息集，联系人消息集*/
                                       AccountFriendLocalData("11110000000", "小明", "你好", accountData!!.id+"and11110000000"),
                                       AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", accountData!!.id+"and11110000001"),
                                       AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", accountData!!.id+"and11110000002"),
                                       AccountFriendLocalData("11110000000", "小明", "你好", accountData!!.id+"and11110000000"),
                                       AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", accountData!!.id+"and11110000001"),
                                       AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", accountData!!.id+"and11110000002"),
                                       AccountFriendLocalData("11110000000", "小明", "你好", accountData!!.id+"and11110000000"),
                                       AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", accountData!!.id+"and11110000001"),
                                       AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", accountData!!.id+"and11110000002"),
                                       AccountFriendLocalData("11110000000", "小明", "你好", accountData!!.id+"and11110000000"),
                                       AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", accountData!!.id+"and11110000001"),
                                       AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", accountData!!.id+"and11110000002"),
                                       AccountFriendLocalData("11110000000", "小明", "你好", accountData!!.id+"and11110000000"),
                                       AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", accountData!!.id+"and11110000001"),
                                       AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", accountData!!.id+"and11110000002"),
                                       AccountFriendLocalData("11110000000", "小明", "你好", accountData!!.id+"and11110000000"),
                                       AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", accountData!!.id+"and11110000001"),
                                       AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", accountData!!.id+"and11110000002"),
                                      ),
                        )
//                        LaunchedEffect(Unit){/*LaunchedEffect监听参数变化自动重载 协程作用域*/
//                            currentAccount_FriendDBTableDao.getAllFriend_Flow()/*获取当前账号好友数据库表中所有数据*/.collect{ allFriendsListFlow->/*收集Room返回的Flow，每次将List集合赋值给friendList变量名(若不写则默认赋值给it)*/
//                                contactMessageItems.clear()/*直接替换整个列表(避免手动去重)，由于是初始化(将本地已知数据加入) 所以不必*/
//                                contactMessageItems.addAll(allFriendsListFlow)/*添加集合全部内容，自动刷新列表*/
//                            }
//                            contactMessageItems.addAll(allFriends)/*在列表项集合中 添加 全部元素(全部好友数据集合)，不方便记录列表已存在好友项*/
//                        }

                    }

                    /*有时候 同层级或子层级 代码块中无法调用已存在参数 可能是前边某处代码多了个}，同样也不能多{ 否则后边函数调用处都报错*/
                    LazyColumn/*垂直有序列表*/(Modifier.fillMaxSize(1f), state=listState){
                        items/*遍历列表多项*/(contactMessageItems, ){ contactMessageItem->/*Lambda表达式，赋值每次遍历值的变量名(若不写则默认赋值给it)*/
                            Column{
                                PopupItem(contactMessageItem.name, contactMessageItem.newMessage,
                                          onTap={ selectedFriend=contactMessageItem; navigator.push(Session() )/*跳转 消息会话界面*/ },
                                          listOf("标为未读","取消置顶","移除选项"),
                                          listOf(
                                              {

                                              },{

                                              },{

                                              },
                                              ),
                                         ){
//                                    showPopup=it
                                }
                            }

                        }
                    }

                }


            }
            composable("contact"){//联系人界面
                Column(Modifier.fillMaxSize() ){
                    Text("联系人页界面")
                }

            }
            composable("dynamic"){//动态界面
                Column(Modifier.fillMaxSize() ){
                    Text("动态页界面")
                }

            }


        }
    }
}



data class NavItem(
    val title:String,//导航项标题
    val route:String,//导航页
    val icon:ImageVector,//导航项图标
    val badgeCount:Int=0,//导航项徽章消息数
    var topAppBarTitle:String="顶部应用栏",/*顶部应用栏标题*/
//    val icon1: ImageVector,/*导航键图标选中状态*/
)
