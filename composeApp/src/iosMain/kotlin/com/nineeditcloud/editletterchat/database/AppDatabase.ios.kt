package com.nineeditcloud.editletterchat.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun getDatabaseBuilder/*IOS平台-获取数据库构建器*/(dbName:String):RoomDatabase.Builder<AppDatabase>{
    val dbFilePath=documentDirectory() +"/$dbName.db"
    return Room.databaseBuilder<AppDatabase>(name=dbFilePath)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory():String{
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory=NSDocumentDirectory,
        inDomain=NSUserDomainMask,
        appropriateForURL=null,
        create=false,
        error=null, )
    return requireNotNull(documentDirectory?.path)
}