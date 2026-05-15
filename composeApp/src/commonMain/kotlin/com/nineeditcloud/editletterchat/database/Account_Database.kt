package com.nineeditcloud.editletterchat.database

import androidx.room.ColumnInfo
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 通过Room框架写的数据库结构、以及包含的表结构
 */

/*账号 数据库类*/
@Database(entities=[UserAccountLocalData::class]/*列出此数据库包含的所有Entity实例*/,
    version=1,/*数据库版本号，升级时需增加*/exportSchema=false/*导出数据库架构信息，可选*/)
@ConstructedBy(Account_DatabaseConstructor::class)
/*账号数据库类*/
abstract class Account_Database:RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao//提供DAO实例(数据访问对象)，对于用户账号数据表的 数据访问对象
//    companion object{/*单例模式，避免创建多个数据库实例*/
//        @Volatile
//        private var INSTANCE:Account_Database ?=null
//        fun getDatabase(context:Context): Account_Database {
//            return INSTANCE ?:synchronized(this){
//                val instance=Room.databaseBuilder(context.applicationContext, Account_Database::class.java, "accounts_local.db"/*数据库文件名*/).build()/*build()启用自动创建*/
//                INSTANCE=instance
//                instance
//            }
//        }
//    }
}
/*Room编译器生成 `actual` implementations*/
@Suppress("KotlinNoActualForExpect")
expect object Account_DatabaseConstructor :RoomDatabaseConstructor<Account_Database> {
    override fun initialize(): Account_Database
}

/*结构：
* 用户账号数据库只存储所有已添加的账号信息
* 另外每个账号一个额外数据库存储好友和消息
* */

/*用户账号本地数据 表结构实体类*/
@Entity(tableName = "user_accounts_local"/*表名*/)/*注解结构实体类，并定义表名，此表不带头像路径，头像保存在账号对应的路径*/
data class UserAccountLocalData(
    @PrimaryKey/*关键字段 注解，每个表结构必须有*/
    var id: String,/*账号Id*/

    var name: String,/*昵称*/
    var passwd: String,/*密码*/
    var token: String,/*令牌*/
    var user_status: String="",/*用户状态*/

    @ColumnInfo("current_use")/*字段名*/
    var currentUse: Boolean=true/*是否为当前正在使用的账号*/
)
@Dao/*对于用户账号数据表的 数据访问(DAO实例)*/
interface UserAccountDao {
    @Insert/*插入单个用户账号*/
    suspend fun insertAccount(userAccount: UserAccountLocalData): Long/*返回插入的行ID(Long类型)*/

    @Update/*更新用户账号信息*/
    suspend fun updateAccount(userAccount: UserAccountLocalData)

//    @Query("DELETE FROM user_account_local WHERE id = :accountId")/*根据ID删除 本地保存的 用户账号*/
//    suspend fun deleteAccountById(accountId: String): Int/*返回 被删除数据的 行索引*/


    @Query("SELECT * FROM user_accounts_local")/*查询所有用户账号*/
    fun getAllAccount(): Flow<List<UserAccountLocalData>>/*使用Flow，数据变化时自动发射新数据*/

    @Query("SELECT token FROM user_accounts_local WHERE id == :accountId")/*根据账号ID查询用户Token身份令牌*/
    suspend fun getAccountCookieById(accountId: String): String?/*返回数据类型不可选表类型*/

    @Query("SELECT id from user_accounts_local where current_use==1")/*查询正在使用的账号*/
    suspend fun getCurrentUseAccountIdByCurrentUse(): String

    /*查询是否存在正在使用的账号*/
    @Query("SELECT COUNT(*) > 0 FROM user_accounts_local WHERE current_use == 1")
    suspend fun getHisCurrentUseAccount(): Boolean

    /*除查询账号外的 其它账号current_use字段都改成false，将指定账号外的其它账号改为未在使用，如果没有复合条件的数据也不会报错，只是该更新操作会影响0行*/
    @Query("UPDATE user_accounts_local SET current_use = 0 WHERE id != :currentUseAccountId")/* “:”符号在此处表示使用变量传参 */
    suspend fun updateUnusedState_excludeCurrentUse/*更改为未使用状态_排除当前使用*/(currentUseAccountId: String): Int

    /*将查询账号的 current_use字段改为true，将指定账号改为正在使用中，如果没有复合条件的数据也不会报错，只是该更新操作会影响0行*/
    @Query("UPDATE user_accounts_local SET current_use = :newValue WHERE id == :accountId")
    suspend fun updateCurrentUseState/*更改当前使用状态*/(newValue: Boolean=true, accountId: String): Int

//    @Query("SELECT * FROM user_account_local WHERE age BETWEEN :minAge AND :maxAge")/*根据年龄范围查询 本地保存的 账号*/
//    suspend fun getUsersByAgeRange(minAge: Int, maxAge: Int): List<UserAccountLocal>



}


