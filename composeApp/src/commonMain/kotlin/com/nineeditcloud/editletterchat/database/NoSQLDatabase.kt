package com.nineeditcloud.editletterchat.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.TRUE_PREDICATE
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlin.reflect.KClass

/**
 * 通过 Realm(NoSQL非关系型面向对象数据库框架) 编写的数据库结构、表结构对象模型
 */

fun RealmDatabase/*获取数据库实例*/(dbName:String, vararg classes:KClass<out RealmObject>/*vararg instances: RealmObject*/):Realm{
    return Realm.open/*打开数据库(若不存在则自动创建)*/(RealmConfiguration.Builder(schema/*表结构模型*/=classes.toSet() )
                                                            .name("$dbName.realm")/*动态库名*/.build()/*构建数据库*/ )

}
suspend inline fun <reified T:RealmObject> RealmDB/*更便捷获取数据库实例*/(dbName/*库名*/:String, instance/*表对象*/:RealmObject):Realm{
    return RealmDatabase(dbName, instance::class)/*动态指定库名(例如按用户分库)*/
}


suspend inline fun addData/*向指定库的表添加数据*/(dbName/*库名*/:String, tableObject/*表对象*/:RealmObject, crossinline block/*数据代码块*/:() -> Unit){
    val realm=RealmDatabase(dbName, tableObject::class /*as KClass<out RealmObject>*/)/*动态指定库名(例如按用户分库)*/
    realm.write { copyToRealm( tableObject.apply{ block()/*数据代码块*/ } ) }/*增*/
}

suspend inline fun <reified T:RealmObject> queryData/*查询数据*/(dbName:String, /*tableObject*//*表对象*//*:RealmObject,*/
                                                                 query/*多字段条件*/:String=TRUE_PREDICATE, vararg args/*多字段赋值*/:Any?):T?{
    val realm=RealmDatabase(dbName, T::class)/*动态指定库名*/
    return realm.query<T>(query, args).find()/*执行查询返回结果列表 获取：返回值?.字段*/ /*.first()*//*获取第一个元素*/ .firstOrNull()
}

/*改*/
//val realm=RealmDB("库名", 表结构对象类() )
//数据库对象.write { 查询方法返回的对象?.字段=21 }

suspend inline fun <reified T:RealmObject> deleteData/*删除数据*/(dbName:String, instance:RealmObject,
                                                                  query/*多查询字段*/:String=TRUE_PREDICATE, vararg args/*多字段赋值*/:Any?){
    val realm=RealmDatabase(dbName, instance::class)/*动态指定库名*/
    realm.write { query<T>(query, args).find()/*执行查询返回结果列表*/.forEach/*遍历整条数据*/{ delete(it)/*删除字段数据*/ } }
}

@PersistedName("userAccount_localData")/*用户账号本地数据 表结构对象模型 表名注解*/
class UserAccountLocalData:RealmObject{/*此表不带头像路径，头像保存在账号对应的路径*/
    @PrimaryKey/*关键字段 注解(常规表结构必须有)*/
    var id:String=""/*账号Id*/
    var name:String=""/*昵称*/
    var passwd:String=""/*密码*/
    var token:String=""/*令牌*/
    var user_status:String=""/*用户状态*/
    @PersistedName("current_use")/*字段名，NoSQL字段名虽然可包含大写，但推荐通用数据库字段命名方式*/
    var currentUse: Boolean=true/*是否为当前正在使用的账号*/
}

//@RealmModule(classes=[AccountFriendLocalData::class, AccountFriendMessage::class])/*定义包含多个 表对象模型类 的模块 (旧版Java注解，新版Kotlin已废弃)*/
//class FriendAndMessage
val friendAndMessage=setOf(AccountFriendLocalData::class, AccountFriendMessage::class)/*包含多个 对象模型类 的集合*/

@PersistedName("friend")/*账号好友本地数据 表结构对象模型 表名注解*/
class AccountFriendLocalData:RealmObject{/*ContactMessageItem也会使用，此表不保存头像路径，头像保存在账号对应的路径*/
    @PrimaryKey/*关键字段*/
    val id:String=""/*好友账号或群聊ID*/
    var name:String=""/*用户名*/
    var newMessage:String=""/*最新消息简略*/
    @PersistedName(name="friend_message_session")/*字段名*/
    val withFriendMessageSession:String=""/*与好友的消息会话*/

    var user_status:String="这家伙很忙，没发表状态"/*好友发表的用户状态，若调用处不传参数则 默认状态*/
    var top:Boolean=false        /*是否为置顶，若调用处不传参数则默认false*/
    var message_list:Boolean=true/*是否在消息列表中，若调用处不传参数则默认true*/
    var menu:Boolean=false/*长按菜单状态*/
}

@PersistedName("message")/*账号好友本地数据 表结构对象模型 表名注解*/
class AccountFriendMessage:RealmObject{
    @PrimaryKey/*关键字段*/
    val id:String=""/*发消息的账号*/
    var message:String=""/*消息内容*/

    @PersistedName(name="message_session")/*字段名*/
    val messageSession:String=""/*消息会话*/
}


