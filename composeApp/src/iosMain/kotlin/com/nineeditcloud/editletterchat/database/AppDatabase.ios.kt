package com.nineeditcloud.editletterchat.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
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


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object AppDatabaseConstructor:RoomDatabaseConstructor<AppDatabase> {
    actual override fun initialize():AppDatabase {
        TODO("Not yet implemented")
    }
}