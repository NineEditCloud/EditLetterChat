package com.nineeditcloud.editletterchat.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder/*JVM桌面平台-获取数据库构建器*/(dbName:String):RoomDatabase.Builder<AppDatabase>{
    val dbFile=File(System.getProperty("java.io.tmpdir"), "$dbName.db")
    return Room.databaseBuilder<AppDatabase>(name=dbFile.absolutePath)
}