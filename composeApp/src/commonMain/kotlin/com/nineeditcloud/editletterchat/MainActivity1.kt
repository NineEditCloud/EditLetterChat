package com.nineeditcloud.editletterchat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.nineeditcloud.editletterchat.database.AccountFriendLocalData
import com.nineeditcloud.editletterchat.database.Account_Database
import editletterchat.composeapp.generated.resources.Res
import editletterchat.composeapp.generated.resources.cover07
import editletterchat.composeapp.generated.resources.name_edit
import editletterchat.composeapp.generated.resources.new_user
import org.jetbrains.compose.resources.painterResource
import java.io.File
import java.io.FileOutputStream

/*首页 导航图 界面*/

var account:String="10000000001"
var selectedId=""
var selectedName=""

var imagePath:String=""
var backgroundColor:Color=Color.White/*全局背景色初始化值*/

//lateinit var userAccountDao: UserAccountDao/*lateinit为延迟初始化，Dao操作实例(数据访问对象)*/
//val accountDatabase=Account_Database.getDatabase(this)/*获取数据库实例*/
//val userAccountDao=accountDatabase.userAccountDao()/*获取数据库中的 已登录账号本地数据 表实例*/
//lifecycleScope.launch{/*协程*/
//    if(userAccountDao.getHisCurrentUseAccount()){/*如果存在正在使用的账号*/
//        account=userAccountDao.getCurrentUseAccountIdByCurrentUse()/*获取当前使用账号*/
//    }
//}


class MainActivity1:Screen{
    @Composable
    override fun Content() {
        val drawerState=remember{ DrawerState(DrawerValue.Closed) }/*抽屉状态对象*/
        val scope=rememberCoroutineScope()/*协程作用域(抽屉控制器操作执行工具)*/

        val navController=rememberNavController()//常量 导航控制器对象(导航图和导航栏共用同一个导航控制器，实现控制导航图)
        //    var presses by remember { mutableIntStateOf(0) }
        val items=listOf(//导航项信息列表
            NavItem("消息", "message", Icons.Default.Home, badgeCount = 0,"用户名"),
            NavItem("联系人", "contact", Icons.Default.Person, badgeCount = 23,"联系人"),
            NavItem("动态", "dynamic", Icons.Default.Favorite, badgeCount = 100,"动态")
                        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()/*创建NavBSE对象*/
        val currentRoute=navBackStackEntry?.destination?.route//获取当前导航页 对象

        val navigator=LocalNavigator.currentOrThrow/*Voyager-Navigation 绑定当前界面的导航控制器*/



        /*这部分是把好友头像保存到本地，如果正式上架的话要把头像改成遍历所有接收到此账号好友的头像*/
        /*获取外部私有 文件路径和缓存路径
        应用外部文件路径：/storage/emulated/0/Android/data/包名/files
        应用外部缓存路径：/storage/emulated/0/Android/data/包名/cache
        Context需要处在主函数可用this调用当前类所继承的Context类，其它可用LocalContext.current创建的对象
        */
        val filesPath=context.getExternalFilesDir(null)?.absolutePath ?: "n"/*如果获取失败返回n*/
        //        val cachePath=this.externalCacheDir?.absolutePath ?: "n"/*如果获取失败返回n*/

        if (filesPath != "n"){/*如果确实获取到了外部私有文件路径*/
            val dir= File(filesPath/*目录*/,"avatar"/*文件夹名称*/)/*拼接头像文件夹路径*/
            if(!dir.exists()){/*如果文件夹不存在，(!代表反面，文件夹存在的反面)*/
                dir.mkdirs()/*创建文件夹，否则在不存在此文件夹的情况下，向不存在的目录保存文件会报错并闪退*/
            }

            val imageFile = File("$filesPath/avatar", "new_user.png")//创建文件
            val bitmap = BitmapFactory.decodeResource(context.resources, Res.drawable.new_user)//从资源(res)获取图片Bitmap

            FileOutputStream(imageFile).use { outputStream ->/*文件输出流*/
                //            Bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)/*创建一个图片*/
                bitmap.compress(Bitmap.CompressFormat.PNG/*将图片输出为Png格式*/, 90, outputStream)/*保存图片Bitmap数据输出到文件*/
            }
            imagePath=imageFile.absolutePath/*获取文件路径*/
        }

        /*假设收到的最新每一条消息集，联系人消息集*/
        val contactMessageItems=listOf(
            AccountFriendLocalData("11110000000", "小明", "你好", account+"and11110000000"),
            AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", account+"and11110000001"),
            AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", account+"and11110000002"),
            AccountFriendLocalData("11110000000", "小明", "你好", account+"and11110000000"),
            AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", account+"and11110000001"),
            AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", account+"and11110000002"),
            AccountFriendLocalData("11110000000", "小明", "你好", account+"and11110000000"),
            AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", account+"and11110000001"),
            AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", account+"and11110000002"),
            AccountFriendLocalData("11110000000", "小明", "你好", account+"and11110000000"),
            AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", account+"and11110000001"),
            AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", account+"and11110000002"),
            AccountFriendLocalData("11110000000", "小明", "你好", account+"and11110000000"),
            AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", account+"and11110000001"),
            AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", account+"and11110000002"),
            AccountFriendLocalData("11110000000", "小明", "你好", account+"and11110000000"),
            AccountFriendLocalData("11110000001", "小张", "吃饭了吗？", account+"and11110000001"),
            AccountFriendLocalData("11110000002", "小王", "下午去踢足球吗？", account+"and11110000002"),
                                      )

        //    var lastDownTime by remember { mutableLongStateOf(0L) }
        /*监听长按对象，由于combinedClickable闪退Bug，所以写一个 按下抬起 监听让 超文本按钮onClick判断*/

        //    var provider=if (isSystemInDarkTheme()) Color.White else Color.Black/*根据主题选择文本颜色，否则系统不自动刷新，因为是固定的两种颜色，所以写val常量更省内存资源，请把变量放在抽屉、导航图和列表外，否则开关一次抽屉就可能出Bug，在深色模式开关一次抽屉再浅色也会出Bug*/

        var expanded by remember { mutableStateOf(false)/*默认为关闭状态*/ }/*弹出式菜单列表是否打开*/

        //    var listItemWindowExpanded by remember { mutableStateOf(false)/*默认为关闭状态*/ }/*弹出式菜单列表是否打开*/
        //    var buttonBounds by remember { mutableStateOf<Rect?>(null) }/*触发按钮的坐标信息，用于精准定位，适合列表项长按弹窗视图功能菜单*/


        backgroundColor=if(!isSystemInDarkTheme())Color(0xFFEEF2FD) else Color(0xFF1C1E1F)/*浅深主题背景色，背景色可这样判断写，文字用MaterialTheme.colorScheme.onSurface不易出错*/
        val drawerBackgroundColor=if(!isSystemInDarkTheme()) Color.White else Color(0xFF1C1E1F) /*抽屉背景色*/
        val topCoverBackground=painterResource(Res.drawable.cover07)


        var showPopup by remember { mutableStateOf(false) }
        //    var popupOffset by remember { mutableStateOf(Offset.Zero) }


        val density=LocalDensity.current
        val systemBars=WindowInsets.systemBars
        val bottomBarInsets=systemBars.getBottom(density)

        var anchorCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }/*存储屏幕坐标瞄点位置的状态*/
        var anchorSize by remember { mutableStateOf(IntSize.Zero) }/*存储锚点尺寸*/

        /*当锚点坐标变化时，计算弹窗偏移量*/
        val popupOffset=remember(anchorCoordinates){
            if(anchorCoordinates != null){
                val position=anchorCoordinates!!.positionInWindow()
                IntOffset(position.x.toInt(), position.y.toInt())
            }else{
                IntOffset.Zero
            }
        }

        val listState=rememberLazyListState()

        Box(Modifier.fillMaxSize().background(backgroundColor)/*.semantics(mergeDescendants=true){}*//*合并子组件语义*/
           ){

            ModalNavigationDrawer/*左侧抽屉，会自动适应系统 顶部状态栏和底部导航栏 部分的边距*/(
                drawerState/*绑定抽屉状态对象*/=drawerState, gesturesEnabled/*手势功能启用*/=true,
                drawerContent/*抽屉内容*/={

                    ModalDrawerSheet/*模态抽屉模板(自带与状态栏、导航栏的边距，不要在有顶部状态栏边距时放入顶部封面背景图)，若无此组件会导致点抽屉任意区域都关抽屉*/(Modifier.fillMaxSize(),
                                                                                                                                                                  windowInsets=WindowInsets(top=0,left=0,right=0,bottom=bottomBarInsets),/*关掉与顶部状态栏的边距*/
                                                                                                                                                                  drawerContainerColor=Color.Transparent/*抽屉模板颜色透明*/){
                        Box/*堆叠布局*/(Modifier.fillMaxSize() ){
                            Image/*用户顶部封面图*/(topCoverBackground,contentDescription="",
                                                    Modifier.fillMaxWidth()/*占据全部容器宽度*/.align(Alignment.TopCenter)/*在Box中 垂直居顶 水平开头*/
                                                    ,contentScale=ContentScale.FillWidth/*拉伸至组件宽度(高度按拉伸比例自适应)，自动调整图片组件高度*/
                                                   )
                            //                    CompositionLocalProvider(LocalConfiguration provides fixedConfiguration){/*使用固定字体缩放大小的组件，不受系统字体大小影响*/
                            //                    }

                            Column(Modifier.fillMaxSize()) {
                                Box/*用户卡片部分堆叠布局*/(Modifier.fillMaxWidth().padding(top=180.dp)/*顶部边距*/){
                                    Column(Modifier.fillMaxWidth()) {
                                        Column(Modifier.fillMaxWidth().height(40.dp)) { }/*填充Row水平布局名片外边距部分后边的上一半为透明*/
                                        Column(Modifier.fillMaxWidth().background(drawerBackgroundColor).height(40.dp)/*填充剩余的所有空间，但不影响其它组件空间*/) { }/*填充Row名片下一半及下面的全部 为抽屉背景颜色*/
                                    }
                                    Row/*水平布局*/(Modifier.fillMaxWidth().padding(horizontal=20.dp)/*水平外边距*/.height(80.dp)
                                                        .background(
                                                            //                                        brush=Brush.horizontalGradient(listOf(Color.Gray,Color.White)),/*水平渐变色(灰渐变白)*/
                                                            drawerBackgroundColor,RoundedCornerShape(12.dp)/*圆角背景*/)
                                                        //                                    .align(Alignment.BottomCenter)/*子项对齐方式，垂直居底 水平居中*/
                                                        //                                    .clip(RoundedCornerShape(8.dp))/*裁剪内容为圆角(包括子组件)，并不波及当前组件背景为圆角*/
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
                                    //                                HorizontalDivider()/*功能项分割线，Divider()已废弃，更名为HorizontalDivider()*/
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("钱包", color=MaterialTheme.colorScheme.onSurface) }, selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/{ drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(Icons.Default.Lock, contentDescription="钱包图标") },
                                        shape=RectangleShape/*设置直角*/
                                                                  )
                                    NavigationDrawerItem/*导航项*/(
                                        label={Text("收藏", color=MaterialTheme.colorScheme.onSurface) }, selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/{ drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon = { Icon(Icons.Default.Star, contentDescription="收藏图标") },
                                        shape = RectangleShape/*设置直角*/
                                                                  )
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("文件", color=MaterialTheme.colorScheme.onSurface) }, selected = false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/{ drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(painterResource(android.R.drawable.ic_input_get), contentDescription="文件图标",
                                                    Modifier.size(25.dp)) },
                                        shape=RectangleShape/*设置直角*/
                                                                  )
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("相册",color=MaterialTheme.colorScheme.onSurface) },
                                        selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/ { drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(painterResource(android.R.drawable.ic_menu_gallery), contentDescription="相册图标",
                                                    Modifier.size(25.dp)) },
                                        shape=RectangleShape/*设置直角*/
                                                                  )
                                    NavigationDrawerItem/*导航项*/(
                                        label={ Text("笔记", color=MaterialTheme.colorScheme.onSurface) },
                                        selected=false,
                                        onClick={
                                            scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/ { drawerState.close()/*关闭抽屉*/ }
                                        },
                                        icon={ Icon(Icons.Default.Edit, contentDescription="笔记图标",Modifier.size(25.dp)) },
                                        shape=RectangleShape/*设置直角*/
                                                                  )

                                    Spacer(Modifier.weight(1f)/*填充全部高度将Row推到底部*/)/*弹性空间*/
                                    Row{
                                        Column(horizontalAlignment = Alignment.CenterHorizontally/*子项水平居中*/, modifier = Modifier.padding(horizontal = 20.dp)) {
                                            Icon(Icons.Default.Settings, contentDescription = "设置图标")
                                            Text("设置", fontSize = 11.sp, lineHeight = 1.sp,color=MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally/*子项水平居中*/, modifier = Modifier.padding(horizontal = 20.dp)) {
                                            Icon(Icons.Default.Home, contentDescription = "主题图标")
                                            Text("个性主题", fontSize = 11.sp, lineHeight = 1.sp,color= MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally/*子项水平居中*/, modifier = Modifier.padding(horizontal = 20.dp)) {
                                            Icon(Icons.Default.LocationOn, contentDescription = "地区天气")
                                            Text("地区天气", fontSize = 11.sp, lineHeight = 1.sp,color= MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }

                            }

                        }

                    }

                }
                                                                                              ) {/*抽屉外的界面*/
                Box(Modifier.fillMaxSize()){
                    Scaffold/*脚手架*/(
                        topBar/*顶部栏*/={
                            TopAppBar/*顶部应用栏*/(
                                colors/*样式*/=topAppBarColors(
                                    containerColor=backgroundColor,/*背景色，浅色主题下白色*/
                                    titleContentColor=MaterialTheme.colorScheme.onSurface,/*标题色，浅色主题下黑色*/
                                                              ),
                                navigationIcon/*顶部左侧导航项图标按钮*/ = {
                                    IconButton/*顶部左侧导航 图标按钮*/(
                                        onClick={
                                            scope.launch { drawerState.open()/*启动左侧抽屉*/ }
                                        }
                                                                       ){
                                        Icon(imageVector/*顶部左侧导航按钮图标*/=Icons.Default.Person, contentDescription="用户头像，打开名片抽屉",
                                             Modifier.size(40.dp) )
                                    }
                                },
                                title/*顶部应用栏的标题控件集*/={
                                    //                        AnimatedVisibility(visible = currentRoute.equals("message")) {/*按条件定义是否可见*/
                                    //                            Text("用户昵称")
                                    //                        }

                                    Row(Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                        if(currentRoute.equals("message")){/*如果导航图是在导航消息界面*/
                                            Column/*竖直布局*/(
                                                Modifier.combinedClickable(
                                                    onClick = {/*单击事件*/

                                                    },
                                                    onLongClick = {/*长按事件*/

                                                    }),
                                                              ){
                                                Text("用户昵称", fontSize=10.sp/*字体大小*/, lineHeight = 1.sp/*控件行间隔高度*/)
                                                Row/*水平布局*/(verticalAlignment=Alignment.CenterVertically/*子控件垂直居中对齐*/){
                                                    Icon(imageVector=Icons.Default.AddCircle, contentDescription="状态图标", Modifier.size(12.dp))
                                                    Text("状态", fontSize=8.sp/*字体大小*/,
                                                        //                                        modifier = Modifier.border(width = 1.dp, color = Color.Blue, shape = RoundedCornerShape(8.dp)),//绘制边框查看偏移问题
                                                         lineHeight=1.sp/*文本行间隔高度*/)
                                                }
                                            }
                                        }else{/*导航图在导航其它界面*/
                                            Text(text=
                                                     if(currentRoute.equals("contact")) "联系人"/*如果导航图在导航联系人界面*/
                                                     else /*if (currentRoute.equals("dynamic"))*/ "动态"/*如果导航图在动态界面*/,
                                                 fontSize=15.sp/*字体大小*/, lineHeight=1.sp/*文本行间隔高度*/
                                                )
                                        }

                                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.End/*子项居右*/) {
                                            Icon(Icons.Default.Search, contentDescription="搜索图标", Modifier.clickable{/*搜素图标点击事件*/

                                            }.padding(horizontal=5.dp).size(30.dp)  )
                                            Icon(Icons.Default.Menu, contentDescription="功能菜单图标", Modifier.clickable{/*功能菜单点击事件*/
                                                expanded=!expanded/*打开或关闭功能菜单列表*/
                                                //                                    if(!expanded)expanded=true else expanded=false
                                            }.padding(horizontal=5.dp).size(30.dp)  )
                                        }


                                    }

                                },

                                )
                        },
                        bottomBar/*底部栏*/={
                            NavigationBar(Modifier.height(111.dp), containerColor=backgroundColor/*底部导航栏背景色*/) {//导航栏
                                items.forEach/*遍历items*/ { item/*每次赋值给新建item变量*/ ->
                                    NavigationBarItem(
                                        icon/*图标集*/={

                                        },

                                        label/*标签集*/={
                                            BadgedBox/*徽章布局 给图标旁加徽章*/(
                                                badge={//徽章集
                                                    if (item.badgeCount > 0) {/*判断徽章消息数如果大于0则执行*/
                                                        Badge/*徽章*/(containerColor=Color.Red/*徽章背景颜色*/, contentColor=Color.White/*徽章内控件颜色*/){
                                                            Text(
                                                                text=if (item.badgeCount > 99) "99+"/*徽章消息数显示上限*/
                                                                else item.badgeCount.toString()/*99以下显示完整消息数*/,
                                                                fontSize=8.sp/*徽章字体大小*/, lineHeight=1.sp/*文本行间隔高度*/
                                                                )
                                                        }
                                                    }
                                                }
                                                                                ){/*徽章布局内的其它组件*/
                                                Column(horizontalAlignment=Alignment.CenterHorizontally/*子内容水平居中对齐*/){
                                                    Icon/*图标*/(imageVector=item.icon,contentDescription=item.title,
                                                                 Modifier.size(23.dp)/*图标大小*/)
                                                    Text/*标签*/(item.title, fontSize=8.sp/*标签字体大小*/, lineHeight=1.sp/*文本行间隔高度*/,
                                                                 color=if(currentRoute==item.route) Color(0xFF6933CC) /*导航图导航页符合导航项为蓝紫色(0xFF6933CC)，Magenta为紫色*/
                                                                 else MaterialTheme.colorScheme.onSurface/*未导航此导航项页面则为该主题下文字颜色*/
                                                                )
                                                }

                                            }
                                        },

                                        selected=currentRoute==item.route,/*是否为选中状态，判断当前导航页是否符合此导航项*/
                                        onClick={/*导航项点击事件*/
                                            navController.navigate(item.route)/*导航控制器使用对应导航页*/{
                                                /*配置导航动作行为*/
                                                popUpTo/*导航前弹出回退栈中的片段*/(navController.graph.startDestinationId/*获取导航图起始页面*/) { saveState=true/*保存弹出的片段状态，以便下次导航依旧是保存的状态*/ }
                                                launchSingleTop=true//单顶模式(SingleTop)重要配置：如果目标页面已在回退栈的顶部，就不创建新实例，而是重用现有实例
                                                restoreState=true//当导航目标已访问过且其状态被保存，则自动恢复该页面状态
                                            }
                                        }
                                                     )
                                }
                            }

                        },

                        floatingActionButton/*浮动按钮*/={
                            FloatingActionButton(onClick={/*presses++*/

                            }){//包含组件
                                Icon(Icons.Default.Add, contentDescription="添加图标")/*图标*/
                            }
                        }

                                      ){ innerPadding/*用来适应顶部栏和底部栏的边距(没有水平边距)，防止界面中间内容被顶部栏和底部栏遮挡*/ ->
                        /*脚手架中间内容*/

                        Box(Modifier.fillMaxSize() ){
                            Column/*竖直布局*/(verticalArrangement=Arrangement.spacedBy(16.dp),
                                               modifier=Modifier.padding(innerPadding).background(backgroundColor)
                                              ){
                                /*放置导航图(内嵌界面加载)*/
                                /*改了导航界面路由名称，不要忘了改初始导航页界面路由名称*/
                                NavHost/*导航图主体组件*/(navController=navController,/*使用导航控制器*/ startDestination="message"/*初始导航界面*/){
                                    composable("message"){/*消息界面*/

                                        Box(Modifier.fillMaxSize()){
                                            LazyColumn/*竖直列表*/(Modifier.fillMaxSize(1f), state=listState){
                                                items(contactMessageItems){ contactMessageItem/*赋值给新建item变量*/ ->/*此Lambda表达式代表接下来使用本次变量*/

                                                    Column(Modifier.combinedClickable(/*事件，列表项事件，不能获取触摸指针位置*/
                                                                                      onClick={/*单击事件*/
                                                                                          if(!showPopup){
                                                                                              selectedId=contactMessageItem.id
                                                                                              selectedName=contactMessageItem.name
                                                                                              activity.startActivity(Intent(context, Session()::class.java))/*跳转消息会话界面*/
                                                                                          }
                                                                                      },
                                                                                      onLongClick={/*长按事件*/
                                                                                          showPopup=true
                                                                                      }
                                                                                     ).onGloballyPositioned{ coordinates ->
                                                        anchorCoordinates=coordinates/*获取列表项在屏幕上的坐标*/
                                                    }
                                                          ){
                                                        Row/*水平布局*/(Modifier.fillMaxWidth()/*填充容器全部宽度，否则如果在Button按钮容器中会默认被放置中间*/
                                                                            .padding(7.dp)/*内边距*/
                                                            //                                                .onGloballyPositioned{ layoutCoordinates ->
                                                            //                                                    buttonBounds=layoutCoordinates.boundsInWindow()/*获取按钮在屏幕上的位置和大小，用于菜单位置计算*/
                                                            //                                                }
                                                                       ){
                                                            Image(painter=painterResource(Res.drawable.new_user)
                                                                //                                                    rememberAsyncImagePainter(model=File("${contactMessageItem.id}.jpg"))/*Image图片资源，加载账号Id对应的头像路径*/
                                                                  ,contentDescription="头像圆角图片",/*Image描述(必填此项，否则报错)*/
                                                                  Modifier.size(45.dp)//设置图片尺寸
                                                                      .clip(RoundedCornerShape(5.dp))//设置圆角半径，12.dp为圆形
                                                                      .background(Color.LightGray)/*可选：添加背景色，便于观察圆角效果*/,
                                                                  contentScale=ContentScale.Crop,//可选：缩放类型，如裁剪适应
                                                                 )
                                                            Column/*竖直布局*/(Modifier.padding(start=10.dp)/*竖直布局外边距(因为是在Row水平布局中，所以是左边距)*/) {
                                                                /*此布局内是昵称和最新消息 控件*/
                                                                Text/*昵称文本*/(contactMessageItem.name,color=MaterialTheme.colorScheme.onSurface/*昵称黑白色，导航图和列表里的界面必须用MaterialTheme，否则出现不会实时跟随系统深浅主题变色的Bug*/,
                                                                                 fontSize = 10.sp, lineHeight=15.sp)
                                                                Text/*最新消息文本*/(contactMessageItem.newMessage,color=Color.Gray/*内容灰色*/,
                                                                                     fontSize = 8.sp, lineHeight=10.sp)
                                                            }

                                                        }

                                                        //                                            Divider(Modifier.padding(start = 80.dp))//列表项分割线，已废弃，更名为HorizontalDivider
                                                        HorizontalDivider(Modifier.padding(start=80.dp), color=Color.LightGray)/*水平分割线*/
                                                    }

                                                }
                                            }

                                            //


                                        }


                                    }
                                    composable("contact"){//联系人界面
                                        Column(Modifier.fillMaxSize()){
                                            Text("联系人页界面")
                                        }

                                    }
                                    composable("dynamic"){//动态界面
                                        Column(Modifier.fillMaxSize()){
                                            Text("动态页界面")
                                        }

                                    }

                                }

                            }

                            BackHandler/*拦截返回键*/{
                                if(drawerState.isClosed)/*如果抽屉是关闭状态)*/
                                    if(expanded||showPopup){/*如果有弹窗视图是打开状态*/
                                        expanded=false
                                        showPopup=false
                                    }else/*否则，抽屉是关闭状态*/ navigator.pop()/*结束当前界面*/
                                else /*否则，抽屉是打开状态*/
                                    scope.launch/*启动协程作用域(抽屉控制器操作执行工具)*/ { drawerState.close()/*关闭抽屉*/ }
                            }

                            if(expanded){/*如果弹窗视图状态为打开*/
                                /*和Popup和DropdownMenu的好处在于根据 点击输入位置弹出，适合会动的布局或控件 定位弹窗视图，将其放进脚手架中间部分适合*/
                                Popup/*Popup自定义弹窗视图(DropdownMenu底层实现)，当需求超出DropdownMenu标准范畴，可自定义*/(
                                    alignment=Alignment.TopEnd, offset=IntOffset(0,56)/*调整弹出菜单位置*/,
                                    onDismissRequest={expanded=false},/*点击外部区域则关闭菜单*/   ){
                                    /*自定义菜单项内容*/
                                    /*注：弹窗视图不能用脚手架中间的边距(由于视图弹出时不在脚手架内)，否则会向下错位*/
                                    Row/*自定义布局以实现位置和外边距*/(Modifier.fillMaxSize()
                                                                            .pointerInput(Unit){/*指针输入(默认无按下涟漪)*/
                                                                                detectTapGestures/*检测点击手势，包括按下、点击、长按、双击等*/(
                                                                                    onPress/*按下事件*/={
                                                                                        expanded=false/*关闭弹窗视图*/
                                                                                    }
                                                                                                                                           )
                                                                            }
                                                                            .background(Color.Black.copy(0.4f))/*背景黑色 透明(显示0.3 30%)*/,
                                                                        horizontalArrangement = Arrangement.End/*子项靠右*/
                                                                       ) {
                                        Column/*为不影响菜单外整页显示阴影大小，中间加个布局用来写内边距，(由于Compose的内边距会把当前布局的背景向内推)*/(Modifier
                                                                                                                                                            .padding(top = 50.dp,end = 10.dp)/*顶部和右侧内边距，防止遮挡顶部应用栏，以及右侧内边距*/
                                                                                                                                                            .fillMaxWidth(0.4f)) {
                                            Column(Modifier /*.align(Alignment.TopEnd)*/ /*居顶靠右*/    .width(180.dp)
                                                       .background(backgroundColor,RoundedCornerShape(13.dp))
                                                       .clip(RoundedCornerShape(13.dp))/*裁剪内容为圆角，以待子项最上一行和最下一行点击涟漪为圆角(必须在点击事件前裁剪)*/
                                                       .fillMaxWidth(0.4f)
                                                  ){
                                                Row(Modifier.fillMaxWidth().height(50.dp)
                                                        //                                        .clip(RoundedCornerShape(topStart = 13.dp, topEnd = 13.dp))/*裁剪顶部为圆角，以待点击圆角涟漪(必须在点击事件前裁剪)*/
                                                        .clickable{/*创建群聊选项点击事件*/
                                                            activity.startActivity(Intent(context, CreateGroupChat()::class.java))
                                                        }/*.background(Color.White,RoundedCornerShape(topStart = 13.dp, topEnd = 13.dp))*//*背景白色，顶部圆角*/,
                                                    verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/,) {
                                                    Icon(painterResource(Res.drawable.name_edit), contentDescription = "创建群聊图标",
                                                         Modifier.padding(horizontal = 10.dp))
                                                    Text("创建群聊", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
                                                HorizontalDivider(Modifier.padding(start=40.dp,top=0.dp), color=Color.LightGray)/*水平分割线*/
                                                Row(Modifier.fillMaxWidth().height(50.dp).clickable{/*添加好友选项点击事件*/
                                                    activity.startActivity(Intent(context, Add_FriendAndGroupChat()::class.java))
                                                }, verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                    Icon(painterResource(Res.drawable.new_user), contentDescription = "添加 好友/群 图标",
                                                         Modifier.padding(horizontal = 10.dp))
                                                    Text("加好友/群", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
                                                HorizontalDivider(Modifier.padding(start=40.dp,top=0.dp), color=Color.LightGray)/*水平分割线*/
                                                Row(Modifier.fillMaxWidth().height(50.dp).clickable{/*扫一扫选项点击事件*/
                                                    activity.startActivity(Intent(context, ScanQRCode()::class.java))
                                                }, verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                    Row(Modifier.padding(horizontal = 10.dp)) {
                                                        Icon(painterResource(Res.drawable.ic_menu_view), contentDescription = "扫一扫图标",
                                                             Modifier.size(25.dp))
                                                    }
                                                    Text("扫一扫", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
                                                HorizontalDivider(Modifier.padding(start=40.dp,top=0.dp), color=Color.LightGray)/*水平分割线*/
                                                Row(Modifier.fillMaxWidth().height(50.dp)
                                                        //                                        .clip(RoundedCornerShape(bottomStart = 13.dp, bottomEnd = 13.dp))/*裁剪底部为圆角，以待点击圆角涟漪(必须在点击事件前用)*/
                                                        .clickable{/*收付款选项点击事件*/
                                                            activity.startActivity(Intent(context, PaymentAndReceipt()::class.java))
                                                        }/*.background(Color.White, RoundedCornerShape(bottomStart = 13.dp, bottomEnd = 13.dp))*//*背景白色，底部圆角*/,
                                                    verticalAlignment = Alignment.CenterVertically/*子项垂直居中对齐*/) {
                                                    Row(Modifier.padding(horizontal = 10.dp)) {
                                                        Icon(Icons.Default.Check, contentDescription = "收付款图标",
                                                             Modifier.border(2.dp, Color.Black,RoundedCornerShape(5.dp))/*由于图标很小，所以尽量把变宽设细，圆角设小，否则变成圆了*/
                                                                 .size(25.dp))
                                                    }
                                                    Text("收付款", /*lineHeight = 1.sp,*/ fontSize = 10.sp,color= MaterialTheme.colorScheme.onSurface)
                                                }
                                                //                            DropdownMenuItem(onClick = {}, text = {Text("选项")})/*内容选项默认排列(没什么用处，不如直接写布局)*/
                                            }
                                        }

                                    }

                                }
                            }





                        }
                    }

                    //                if(showPopup){
                    ////                    val offset=with(density){
                    ////                        IntOffset(popupOffset.x.toInt(), popupOffset.y.toInt() )/*屏幕坐标*/
                    ////                    }
                    //
                    //
                    //                    Popup(alignment=Alignment.TopStart/*弹窗内容位置*/, offset/*指定位置显示*/=popupOffset,
                    //                        onDismissRequest/*点外部关弹窗*/={ showPopup=false },
                    //                        properties=PopupProperties(focusable=true, dismissOnBackPress=true, dismissOnClickOutside=true ),
                    //                    ){
                    //                        Row(Modifier/*.padding(end=10.dp)*/, /*horizontalArrangement=Arrangement.End*//*子项水平靠右*/){
                    //                            val listItemWindowBackground=if(!isSystemInDarkTheme()) Color.White else Color.Black
                    //                            val windowItemBackground=if(!isSystemInDarkTheme()) Color.Black else Color.White
                    //                            Row(Modifier.background(listItemWindowBackground, RoundedCornerShape(8.dp))
                    //                                .clip(RoundedCornerShape(8.dp))/*裁剪内容为圆角(为使点击涟漪不超出此布局圆角范围)*/
                    //                            ) {
                    //                                Text("标为未读", modifier=Modifier.background(windowItemBackground).clickable{
                    //
                    //                                }.padding(end = 5.dp), color=listItemWindowBackground,fontSize = 6.sp, lineHeight = 12.sp)
                    //                                Text("设为置顶", Modifier.background(windowItemBackground).clickable{
                    //
                    //                                }.padding(end = 5.dp), color=listItemWindowBackground,fontSize = 6.sp, lineHeight = 12.sp)
                    //                                Text("移除此项", Modifier.background(windowItemBackground).clickable{
                    //
                    //                                }.padding(end = 5.dp), color=listItemWindowBackground,fontSize = 6.sp, lineHeight = 12.sp)
                    //                                Text("删除消息", Modifier.background(windowItemBackground).clickable{
                    //
                    //                                }, color=listItemWindowBackground,fontSize=6.sp, lineHeight=12.sp)
                    //                            }
                    //
                    //                        }
                    //                    }
                    //                }

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



}






data class NavItem(
    val title:String,//导航项标题
    val route:String,//导航页
    val icon:ImageVector,//导航项图标
    val badgeCount:Int=0,//导航项徽章消息数
    var topAppBarTitle:String="顶部应用栏",/*顶部应用栏标题*/
//    val icon1: ImageVector,/*导航键图标选中状态*/
)


