package com.nineeditcloud.editletterchat

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/*安卓端 获取数据库构建器*/
fun getDatabaseBuilder(context:Context, dbName:String): RoomDatabase.Builder<RoomDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("$dbName.db")
    return Room.databaseBuilder<RoomDatabase>(context=appContext, name=dbFile.absolutePath)
}