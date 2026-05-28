package com.nineeditcloud.editletterchat.common_tools

/*自定义-键值对*/

const val delimiter="`"/*分隔符，`(反引号) 并非英文句子中常用的'(单引号)*/
fun <T:Any>/*可调用处 泛型*/ T.toData/*对象模型转数据(序列化) 定义数据类的扩展函数*/():String{
    val str=this::class.toString()/*this代表调用当前扩展函数的对象，获取数据类对象模型实例的KClass，并转为String*/
    val left=str.indexOf('(')/*获取字符串中从头开始第一个(字符的索引，若获取失败返回-1*/
    return str.substring(left+1, str.length-1)/*截取字符串中 第一个(字符和最后一个字符 之间的字符串，索引从0开始 含索引对应的字符*/
//        .replace(Regex("\\s+"), "")/*将空格、制表符、换行等所有空白字符 替换为不存在*/
        .replace(", ",delimiter)/*将", "替换为更简略的"`"分隔符*/
}

fun String?.toHashMap/*数据转哈希表键值对 定义String的扩展函数*/():HashMap<String, String>{
    val strs=this?.split(delimiter)/*?安全调用，若是String，则将调用此函数的字符串 以分割字符 分割为数组*/
    val map=hashMapOf<String, String>()/*hashMapOf集合对象中存在一个HashMap，创建集合操作对象时声明val常量 依旧能更改其对应HashMap的元素*/
    strs?.forEach{ str -> /*遍历strs数组*/
        val key=str.substringBefore('=')/*截取 =字符前的 字符串*/
        val value=str.substringAfter('=')/*截取 =字符后的 字符串*/
        map[key]=value/*map[键]=赋值，若键不存在则添加新键值对元素*/
    }
    return map
}