package com.nineeditcloud.editletterchat.database

import com.attafitamim.kabin.annotations.Database
import com.attafitamim.kabin.core.database.KabinDatabase
import com.attafitamim.kabin.core.database.configuration.KabinDatabaseConfiguration
import com.attafitamim.kabin.core.migration.KabinMigrationStrategy
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlin.collections.emptyList
import com.attafitamim.kabin.core.database.configuration.KabinExtendedConfig

expect fun createKabinConfig(dbName:String):KabinDatabaseConfiguration/*平台相关的配置创建函数*/

/*通用数据库初始化(单例)*/
object DatabaseFactory {
    private var database: KabinDatabase?=null

//    @OptIn(InternalCoroutinesApi::class)
//    fun getDatabase(db:KabinDatabase, dbName:String): KabinDatabase{
//        return database ?:/*为空则继续赋值，不为空则直接返回*/ synchronized(lock=this)/*同步块，this指当前单例对象DatabaseFactory本身，同一时间只有当前线程可进入此代码块*/{
//            database ?: initDatabase(db, dbName).also{ database=it }
//            /*进入同步块后再次检查database是否为null，原因是当多个线程同时发现第一层检查为null时，都会排队等锁，第一个拿到锁的线程初始化了database，后续线程拿到锁后必须再次检查，否则会重复初始化*/
//        }
//    }
//
//    private fun initDatabase(database:KabinDatabase, dbName:String):KabinDatabase{
//        val database=database::class.newInstance(
//            configuration=createKabinConfig(dbName),/*传递 对应平台构建器KabinDatabaseConfiguration配置创建*/
//            migrations=emptyList(),/*数据库迁移列表*/
//            migrationStrategy=KabinMigrationStrategy.DESTRUCTIVE/*破坏性迁移策略*/
//                                                )
//        return database
//    }

    private data class DB/*存储单个数据库名称和实例，放进全局List的结构对象*/(
        val dbName:String,  /*数据库名称*/
        val dbConn:Database /*数据库实例*/)



}