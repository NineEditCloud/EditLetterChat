package com.nineeditcloud.editletterchat.database

import androidx.room.ColumnInfo
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Update
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

/**
 * 通过Room框架写的数据库结构、以及包含的表结构
 * SQLite每个数据库是一个.db扩展名的文件，.db文件中包含多个表的 结构和多行数据，SQLite分库可提升查找性能，但分表仍在同一文件中 在库文件数据中查找的定位部分不变
 * Room框架内部依赖各平台Koltin协程库：
 * org.jetbrains.kotlinx:kotlinx-coroutines-core(内置接口核心和苹果协程)、kotlinx-coroutines-android(安卓)、kotlinx-coroutines-swing(JVM桌面)
 * 同步依赖后清除AndroidStudio旧缓存，重启，再次尝试即可成功
 *
 * 改了数据库的表结构后，会发生错误报错，JVM桌面端要自行删除旧的数据库，打包成Win应用安装运行的可卸载重装 直接测试运行的要手动在数据库路径删文件
 * Room-JVM桌面端数据库文件默认路径是程序执行路径
 */

/*账号 数据库类*/
@Database(entities=[UserAccountLocalData::class,AccountFriendLocalData::class, AccountMessage::class]/*列出此数据库包含的所有表Entity实例*/,
          version=1/*数据库版本号，客户端库/表结构升级时需增加*/, exportSchema=false/*导出数据库架构信息，可选*/)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase/*账号数据库类*/:RoomDatabase(){
    abstract fun userAccountDao():UserAccountDao        /*提供DAO实例(数据访问对象)，对于用户账号数据表的 数据访问对象*/
    abstract fun friendDao():FriendDao                  /*对于好友数据表的 数据访问对象(Dao实例)*/
    abstract fun messageDao():AccountMessageDao   /*对于消息数据表的 数据访问对象(Dao实例)*/
}

/*用来Room编译器内部生成 `actual` implementations*/
@Suppress("KotlinNoActualForExpect"/*禁用 期望函数的 无各平台actual警告(因为Room是编译时生成各平台actual)*/,
          "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"/*禁用 期望函数的编译警告*/)
expect object AppDatabaseConstructor :RoomDatabaseConstructor<AppDatabase>{
    override fun initialize():AppDatabase
}

expect fun getDatabaseBuilder(dbName:String):RoomDatabase.Builder<AppDatabase>/*期望函数，用来给对应平台数据库构建器动态传递库名参数 并获取返回的构建器*/
fun getDatabase/*获取数据库操作实例*/(dbName:String):AppDatabase{
    val builder=getDatabaseBuilder(dbName)/*获取对应平台数据库构建器*/
    return builder.setDriver(BundledSQLiteDriver() ).setQueryCoroutineContext(Dispatchers.IO) .build()
}


/*结构：
* 用户账号数据库只存储所有已添加的账号信息
* 另外每个账号一个额外数据库存储好友信息和消息
*/

@Entity(tableName="user_accounts_local"/*表名*/)/*用户账号本地数据 表结构实体类，并定义表名，此表不带头像路径，头像保存在账号对应的路径*/
data class UserAccountLocalData(
    @PrimaryKey/*关键字段 注解，每个表结构必须有*/
    var id: String="",/*账号Id*/

    var name: String="",/*昵称*/
    var passwd: String="",/*密码*/
    var token: String="",/*令牌*/
    var user_status: String="",/*用户状态*/

    @ColumnInfo("current_use")/*字段名*/
    var currentUse: Boolean=true,/*是否为当前正在使用的账号*/
)
@Dao/*对于用户账号数据表的 数据访问(DAO实例)*/
interface UserAccountDao{
    /*Room跨平台支持非安卓端的Dao实例中方法不能用suspend声明*/
    @Insert(onConflict=OnConflictStrategy.REPLACE)/*插入单个用户账号*/
    suspend fun insertAccount(userAccountTableData:UserAccountLocalData):Long/*返回插入的行ID(Long类型)*/

    @Update/*更新用户账号信息*/
    suspend fun updateAccountInfo(userAccountTableData:UserAccountLocalData)

    @Query("DELETE FROM user_accounts_local WHERE id=:accountId")/*根据ID删除 本地保存的 用户账号，注意SQL语句中表名别写错(否则找不到表实例)*/
    suspend fun deleteAccountById(accountId:String):Int/*返回 被删除数据的 行索引*/


    @Query("SELECT * FROM user_accounts_local")/*查询所有用户账号*/
    fun getAllAccount():Flow<List<UserAccountLocalData> >/*返回值用Flow类型 数据变化时自动发射新数据*/

    @Query("SELECT token FROM user_accounts_local WHERE id==:accountId")/*根据账号ID查询用户Token身份令牌*/
    suspend fun getAccountCookieById(accountId:String):String?/*返回数据类型不可选表类型*/


    @Query("SELECT COUNT(*) > 0 FROM user_accounts_local WHERE current_use==1")/*查询是否存在正在使用的账号(current_use字段值为true的数据量 >0)*/
    suspend fun getHisCurrentUseAccount():Boolean

    @Query("SELECT * from user_accounts_local where current_use==1")/*查询正在用的账号数据，若不存在current_use字段值为true的数据时 返回null*/
    suspend fun getCurrentUseAccountIdByCurrentUse():UserAccountLocalData?/*返回值类型加? 不存在current_use字段值为true的数据时 返回默认值null 否则抛异常*/

    /*除查询账号外的 其它账号current_use字段都改成false，将指定账号外的其它账号改为未在使用，如果没有复合条件的数据也不会报错，只是该更新操作会影响0行*/
    @Query("UPDATE user_accounts_local SET current_use = 0 WHERE id!=:currentUseAccountId")/* “:”符号在此处表示使用变量传参 */
    suspend fun updateUnusedState_excludeCurrentUse/*更改为未使用状态_排除当前使用*/(currentUseAccountId:String):Int

    /*将查询账号的 current_use字段改为true，将指定账号改为正在使用中(若无符合条件的数据也不会报错 只是该更新操作会影响0行)*/
    @Query("UPDATE user_accounts_local SET current_use=:newValue WHERE id==:accountId")
    suspend fun updateCurrentUseState/*更改当前使用状态*/(newValue:Boolean=true, accountId:String):Int


//    @Query("SELECT * FROM user_account_local WHERE age BETWEEN :minAge AND :maxAge")/*根据年龄范围查询 本地保存的 账号*/
//    suspend fun getUsersByAgeRange(minAge: Int, maxAge: Int): List<UserAccountLocal>

}

@Entity(tableName="friend")/*账号好友本地数据 表结构对象模型，ContactMessageItem也会使用，此表不保存头像路径，头像保存在账号对应的路径*/
data class AccountFriendLocalData(
    @PrimaryKey/*关键字段(每个表结构必须有)*/
    val id: String,/*好友账号或群聊ID*/
    var name: String,/*用户名*/
    var newMessage: String,/*最新消息简略*/
    @ColumnInfo(name="friend_message_session")/*字段名*/
    val withFriendMessageSession: String,/*与好友的消息会话*/
    var user_status: String="这家伙很忙，没发表状态",/*好友发表的用户状态，若调用处不传参数则 默认状态*/
    var top: Boolean=false,         /*是否为置顶，若调用处不传参数则默认false*/
    var message_list: Boolean=true, /*是否在消息列表中，若调用处不传参数则默认true*/
    var menu: Boolean=false,/*长按菜单状态*/
)
@Dao/*对于好友数据表的 数据访问(DAO实例)*/
interface FriendDao{
    @Insert(onConflict=OnConflictStrategy.REPLACE)/*插入单个好友数据*/
    suspend fun insertFriend(friendTableData:AccountFriendLocalData):Long/*返回插入的行ID(Long类型)*/

    @Update/*更新好友信息*/
    suspend fun updateFriendInfo(friendTableData:AccountFriendLocalData)

    @Query("DELETE FROM friend WHERE id=:friendId")/*根据ID删除 本地保存的 好友，注意SQL语句中表名别写错(否则找不到表实例)*/
    suspend fun deleteFriendById(friendId:String):Int/*返回 被删除数据的 行索引*/

    @Query("SELECT * FROM friend")/*查询 好友表(friend)中 所有好友数据*/
    suspend fun getAllFriend():List<AccountFriendLocalData>/*List<数据类对象>是listOf集合 元素是数据类对象*/

    @Query("SELECT * FROM friend")/*查询 好友表(friend)中 所有好友数据*/
    fun getAllFriend_Flow():Flow<List<AccountFriendLocalData> >/*返回值用Flow类型 数据变化时自动发射新数据，若表无数据则发射空表(emptyList() )，List<数据类对象>是listOf集合 元素是数据类对象*/
}

@Entity(tableName="msg")
/*账号好友消息 表结构实体类*/
data class AccountMessage(
    @PrimaryKey/*关键字段(每个表结构必须有)*/
    @ColumnInfo("msg_id")/*字段名*/
    val msgID:String,/*消息ID*/
    var msg:String,/*消息内容*/
    @ColumnInfo("sender_id") val senderID:String,/*发送者账号ID*/
    @ColumnInfo("message_session") val messageSession:String,/*消息会话*/
)
@Dao/*对于消息数据表的 数据访问(DAO实例)*/
interface AccountMessageDao{
    @Insert(onConflict=OnConflictStrategy.REPLACE)/*插入单条消息数据*/
    suspend fun insertFriend(messageTableData:AccountMessage):Long/*返回插入的行ID(Long类型)*/

    @Query("DELETE FROM msg WHERE msg_id=:msgID")/*根据ID删除(撤回) 当前会话本地保存的 消息，注意SQL语句中表名别写错(否则找不到表实例)*/
    suspend fun deleteFriendById(msgID:String):Int/*返回 被删除数据的 行索引*/

    @Query("SELECT * FROM msg")/*查询 消息表(msg)中 所有消息数据*/
    suspend fun getAllFriend():List<AccountMessage>/*List<数据类对象>是listOf集合 元素是数据类对象*/

    @Query("SELECT * FROM msg")/*查询 消息表(msg)中 所有消息数据*/
    fun getAllFriend_Flow():Flow<List<AccountMessage> >/*返回值用Flow类型 数据变化时自动发射新数据，若表无数据则发射空表(emptyList() )，List<数据类对象>是listOf集合 元素是数据类对象*/
}


