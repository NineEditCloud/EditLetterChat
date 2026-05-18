package com.nineeditcloud.editletterchat.database

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase

class MyApp/*用来安全获取应用Context实例的类，必须在AndroidManifest的application元素中注册 android:name=".类路径和名称" */:Application(){
    companion object{
        lateinit var context:Application
            private set/*只能由 Application 自己赋值，外部只读*/
    }
    override fun onCreate(){
        super.onCreate()
        context=this/*保存 Application 实例*/
    }
}

actual fun getDatabaseBuilder/*安卓平台-获取数据库构建器*/(dbName:String):RoomDatabase.Builder<AppDatabase>{
    val appContext=MyApp.context/*获取 应用Context(上下文调用)实例，不再依赖 Composable或Activity 的上下文*/
    val dbFile=appContext.getDatabasePath("$dbName.db")/*获取数据库路径*/
    return Room.databaseBuilder<AppDatabase>(context=appContext, name=dbFile.absolutePath)
}

