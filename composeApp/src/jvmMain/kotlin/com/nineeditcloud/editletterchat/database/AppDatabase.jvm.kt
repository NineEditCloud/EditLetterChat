package com.nineeditcloud.editletterchat.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.nineeditcloud.editletterchat.common_tools.Log
import java.io.File

actual fun getDatabaseBuilder/*JVM桌面平台-获取数据库构建器*/(dbName:String):RoomDatabase.Builder<AppDatabase>{
    val dbPath=System.getProperty("user.home")/*系统上下文调用 获取应用文件路径，
    java.io.tmpdir是临时文件路径：C:\Users\桌面系统用户名\AppData\Local\Temp\，
    user.home是用户文件路径：C:\Users\桌面系统用户名 */
    val dbFile=File(dbPath, "$dbName.db")
    val pathHierarchy=/*根据路径字符串 最后一个字符是不是路径层级符(\) 决定是否补充\，\\表示\字符*/
        if(dbPath[dbPath.length-1]=='\\') ""
        else "\\"
    Log.msg("Room-JVM桌面端","用了数据库文件：$dbPath$pathHierarchy$dbName.db")
    return Room.databaseBuilder<AppDatabase>(name=dbFile.absolutePath)
}
