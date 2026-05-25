package com.nineeditcloud.editletterchat.client

import com.nineeditcloud.editletterchat.common_tools.Log
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.IO
import kotlinx.serialization.serializer

object EditLettrtChat_HTTPApiClient{
    private val client=HttpClient()/* Ktor跨平台客户端实例(引擎根据平台自动选择 若未完整添加Ktor各平台适配依赖可能出现兼容问题异常闪退) */
    private val json=Json{/* 使用 kotlinx.serialization 替换 Gson */
        ignoreUnknownKeys=true      /* 忽略服务端返回的未知字段 */
        prettyPrint = true
        isLenient=true              /* 宽松解析，如允许单引号 */
    }
    private val jsonType/*JSON请求头类型*/="application/json; charset=UTF-8"

    suspend fun post(uri/*路径*/:String, requestBody:Any, type:String="json"/*接收请求类型，默认为json*/):Result/*返回所用的密封类对象*/{
        return withContext(Dispatchers.IO){/*在IO协程线程执行网络请求*/
            val jsonType1="application/json; charset=UTF-8"/*Json 内容类型信息，UTF-8编码*/
            val multipartFormdata="multipart/form-data; charset=UTF-8"/*多部分/表单 内容类型信息*/
            val contentType=if(type=="json") jsonType1 else multipartFormdata

            try{
                val response:HttpResponse=client.post("http://192.168.1.47:8080${uri}"){
                    header(HttpHeaders.ContentType, contentType)

                    when(requestBody){/* 根据请求体类型设置 body */
                        is String ->                    setBody(requestBody)/* JSON字符串 */
                        is MultiPartFormDataContent ->  setBody(requestBody)/* 多部分表单 */
                        else -> throw IllegalArgumentException("不支持的请求体类型")
                    }
                }

                val body=response.bodyAsText()
                val resp=json.decodeFromString<Response>(body)

                if(response.status.isSuccess() && body.isNotEmpty() ){/*如果响应成功 且 响应主体不为空*/
                    if(resp.success){/*如果注册成功*/
                        Result.Success(resp.accountId, resp.token)/*调用结果密封类中的 成功类型生效(返回值为Result密封类，自动返回)，并传递 账号Id、Token令牌 参数*/
                        /*注：Result密封类中的 Success数据类 或 Error数据类 都会返回它的整个 Result密封类，post方法返回值为Result密封类*/
                    }else{/*注册失败*/
                        Result.Error(resp.message)/*调用结果密封类中的 错误类型生效，并传递 消息 参数*/
                    }
                }else{/*网络请求失败，状态码400说明两端对接有问题*/
                    Result.Error("${response.status.value}：${resp.message}")/*调用结果密封类中的 错误类型生效，并传递 消息 参数*/
                }
            }catch(e:Exception){/*请求异常，说明服务端不存在于可访问的网络(也可能服务器内存问题导致上次服务程序进程没关掉 占用了端口 本次服务程序进程没监听到对应端口 实在不行重启电脑)，或客户端执行报错问题*/
//                e.printStackTrace()/*在 logcat/控制台 查看具体异常类型和消息*/
                Log.e("Ktor连接异常", e.message!!, e)/*在LogCat/控制台 打印 具体异常类型和消息，使用 tag:System.out 过滤，注意，不要放在末尾，否则会作为返回值 影响正确的返回值*/
                Result.Error("连接异常:\n${e.message}")/*调用结果密封类中的 错误类型生效，并传递 消息 参数*/
            }as Result/*由于此IO线程直接将执行结果出返回值，将catch块的返回值 强制转换为Result类型*/
        }
    }

    suspend fun signUp/*注册*/(username:String, mobilePhoneNum:String, password:String):Result{
        val jsonStr/*Json数据*/=json.encodeToString(serializer(), mapOf(
            "username" to username,
            "mobilePhoneNum" to mobilePhoneNum,
            "password" to password,
            ) ) /*转为请求主体，使用 kotlinx.serialization 生成 JSON */
        return post("/signup", jsonStr)
    }

    suspend fun signIn/*登录*/(id:String, password:String):Result{
        val jsonStr/*Json数据*/ = json.encodeToString(serializer(), mapOf(
            "id" to id,
            "password" to password,
            ) ) /*转为请求主体*/
        return post("/signin", jsonStr)
    }

    suspend fun updatePassword/*更新密码*/(id:String, password:String, newPassword:String):Result{
        val jsonStr/*Json数据*/=json.encodeToString(serializer(), mapOf(
            "id" to id,
            "password" to password,
            "newPassword" to newPassword,
            ) ) /*转为请求主体*/
        return post("/update_password", jsonStr)
    }

    suspend fun setAccountInfo/*设置账号信息*/(id:String, token:String, avatarPath:String="", username:String, mobilePhoneNum:String):Result{
        val jsonStr/*Json数据*/=json.encodeToString(serializer(), mapOf(
            "id" to id,
            "token" to token,
            "username" to username,
            "mobilePhoneNum" to mobilePhoneNum,
            ) )
        if (avatarPath.isNotEmpty() ){/*如果头像路径不为空，选择了头像*/
            val avatarBytes = FileKit.readBytes(avatarPath)/* 使用跨平台文件库 FileKit 读取字节，避免 java.io.File */
            val requestBody=formData{/* 构建 multipart/form-data 请求体 */
                append("avatarImage"/*表单主体名*/, avatarBytes, Headers.build{
                    append(HttpHeaders.ContentType, "image/*")
                    append(HttpHeaders.ContentDisposition, "filename=\"avatar.png\"") /* 跨平台可写死默认名，或从路径提取 */
                }) /*添加部分表单 图片文件字段*/
                append("key-value", jsonStr)/*添加部分表单 键值对数据(表单键值对 或 Json键值对)*/
            }
            return post("/account_info/set", requestBody, "multipart")/*发送 multipart/form-data (多部分/表单) 类型内容*/
        }else{/*未选择头像*/
            return post("/account_info/set", jsonStr)
        }
    }

    suspend fun getAccountInfo/*获取账号信息，不带头像，头像是服务端单独的HTTP映射头像路径*/(id:String):Result{
        val jsonStr/*Json数据*/=json.encodeToString(serializer(), mapOf("id" to id)) /*转为请求主体*/
        return post("/account_info/get", jsonStr)
    }
}

object FileKit{/* 跨平台文件读取抽象，不使用 expect/actual，通过全局属性注入实现，根据实际依赖调整*/
    var reader:(suspend(String)->ByteArray)?=null/* 读取文件为字节数组的函数接口 */
    suspend fun readBytes(path:String):ByteArray{/* 调用注入的实现 */
        return reader?.invoke(path) ?: throw IllegalStateException("FileKit reader not initialized")
    }
}

@Serializable
data class Response/*响应，数据类*/(/*Serializable库的JSON数据模型中 字段 类型尽量和服务端完全相同、提供默认值，String和String?相互对接可能无法解析？*/
    val success:Boolean=false,/*是否成功*/
    val message:String="",/*消息*/
    val accountId:String="",/*账号，成功时必有值，失败时可能无此字段*/
    val username:String="",/*用户名*/
    val mobilePhoneNum:String="",/*手机号*/
    val token:String=""/*Token令牌，成功时必有值，失败时可能无此字段*/,
    )

sealed class Result/*结果 密封类，当被调用其中一个数据类时，密封类的类型 只有被调用的数据类 类型，返回的密封类 中只有 被调用的数据类*/{
    data class Success/*成功 数据类*/(val accountId:String, val token:String):Result()
    data class Error/*错误 数据类*/(val message:String):Result()
    /*数据类返回值类型写上一级密封类()：将此次调用的整个Result密封类返回，这里也表示数据类参数可包含Result密封类*/
}