package com.nineeditcloud.editletterchat.database

import androidx.room.ColumnInfo
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor


/*好友和消息 数据库类*/
@Database(entities=[AccountFriendMessage::class]/*列出此数据库包含的所有 Entity注解的 表结构实体类*/,
          version=1/*数据库版本号，升级时需增加*/, exportSchema=false/*导出数据库架构信息，可选*/)
@ConstructedBy(AccountFriendMessage_DatabaseConstructor::class)
/*account+"_FriendMessageDatabase.db" 数据库文件名(某账号的 消息本地存储数据库)*/
abstract class AccountFriendMessage_Database/*账号好友消息数据库*/:RoomDatabase(){

}

/*Room编译器生成 `actual` implementations*/
@Suppress("KotlinNoActualForExpect")
expect object AccountFriendMessage_DatabaseConstructor :RoomDatabaseConstructor<AccountFriendMessage_Database> {
    override fun initialize():AccountFriendMessage_Database
}

