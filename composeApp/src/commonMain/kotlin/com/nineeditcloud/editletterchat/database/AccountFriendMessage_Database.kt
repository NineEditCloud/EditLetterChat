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
@Database(entities=[AccountFriendLocalData::class,AccountFriendMessage::class]/*列出此数据库包含的所有 Entity注解的 表结构实体类*/,
    version=1/*数据库版本号，升级时需增加*/, exportSchema=false/*导出数据库架构信息，可选*/)
@ConstructedBy(Account_DatabaseConstructor::class)
abstract class AppDatabase:RoomDatabase(){
    abstract fun FriendDao():FriendDao                  /*对于好友数据表的 数据访问对象(Dao实例)*/
    abstract fun MessageDao():AccountFriendMessageDao   /*对于消息数据表的 数据访问对象(Dao实例)*/
//    companion object{/*单例模式，避免创建多个数据库实例*/
//        @Volatile
//        private var INSTANCE:AppDatabase? =null
//        fun getDatabase(context:Context, account:String):AppDatabase{
//            return INSTANCE ?: synchronized(this){
//                val instance = Room.databaseBuilder(context.applicationContext,
//                    AppDatabase::class.java,
//                    account+"_FriendAndMessageLocal.db"/*数据库文件名(某账号的 好友和消息 本地存储数据库)*/
//                ).build()
//                INSTANCE=instance
//                instance
//            }
//        }
//    }
}
/*Room编译器生成 `actual` implementations*/
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor :RoomDatabaseConstructor<Account_Database> {
    override fun initialize():Account_Database
}

/*账号好友本地数据 表结构实体类*/
@Entity(tableName="friend")
data class AccountFriendLocalData/*ContactMessageItem也会使用，账号好友数据 结构实体类，此表不保存头像路径，头像保存在账号对应的路径*/(
    @PrimaryKey/*关键字段(每个表结构必须有)*/
    val id: String,/*好友账号或群聊ID*/

    var name: String,/*用户名*/
    var newMessage: String,/*最新消息简略*/

    @ColumnInfo(name = "friend_message_session")/*字段名*/
    val withFriendMessageSession: String,/*与好友的消息会话*/

    var user_status: String="这家伙很忙，没发表状态",/*好友发表的用户状态，若调用处不传参数则 默认状态*/
    var top: Boolean=false,         /*是否为置顶，若调用处不传参数则默认false*/
    var message_list: Boolean=true, /*是否在消息列表中，若调用处不传参数则默认true*/
    var menu: Boolean=false/*长按菜单状态*/
)
@Dao/*对于好友数据表的 数据访问(DAO实例)*/
interface FriendDao{

}

/*消息 表结构实体类*/
@Entity(tableName="message")
data class AccountFriendMessage(
    @PrimaryKey/*关键字段(每个表结构必须有)*/
    val id: String,/*发消息的账号*/

    var message: String,/*消息内容*/

    @ColumnInfo(name = "message_session")/*字段名*/
    val messageSession: String,/*消息会话*/
                  )
@Dao/*对于消息数据表的 数据访问(DAO实例)*/
interface AccountFriendMessageDao{

}

