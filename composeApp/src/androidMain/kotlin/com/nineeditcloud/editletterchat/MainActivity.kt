package com.nineeditcloud.editletterchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import com.nineeditcloud.editletterchat.database.Account_Database
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigator(StartupLoading())
        }
        val windowInsetsController=WindowCompat.getInsetsController(window, window.decorView)
//        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())/*沉浸界面，关闭导航栏*/
        WindowCompat.setDecorFitsSystemWindows(window,false)/*声明界面扩展到 导航栏和状态栏 背面展示*/

        val accountDatabase=Account_Database.getDatabase(this)/*获取数据库实例*/
        val userAccountDao=accountDatabase.userAccountDao()/*获取数据库中的 已登录账号本地数据 表实例*/

        lifecycleScope.launch{/*协程*/
            doesAnAccountExist=userAccountDao.getHisCurrentUseAccount()/*查询是否已存在正在使用的账号*/
        }
    }
}
