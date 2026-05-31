package com.nineeditcloud.editletterchat.common_tools

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir

/*基于FileKit的 文件操作、私有路径获取*/
val filesPath=FileKit.filesDir /*获取应用私有文件目录，   若获取失败则为空*/
val cachePath=FileKit.cacheDir /*获取应用私有临时缓存目录，若获取失败则为空*/

val filesDir=FileKit.filesDir.run{
    if(this!=null && this ?.absolutePath()/*是否为绝对路径*/ ?.isNotBlank()/*是否不为空*/!!) return@run this
}?:run{/*若获取失败则直接返回或处理错误*/
    println("错误：无法获取应用文件目录，请检查是否已正确初始化FileKit")/*可选：显示用户提示或记录日志*/
    return@run null/*或者抛出异常，取决于你的应用策略*/
}
val cacheDir=FileKit.cacheDir.run{
    if(this!=null && this ?.absolutePath()/*是否为绝对路径*/ ?.isNotBlank()/*是否不为空*/!!) return@run this
}?:run{/*若获取失败则直接返回或处理错误*/
    println("错误：无法获取应用文件目录，请检查是否已正确初始化FileKit")/*可选：显示用户提示或记录日志*/
    return@run null/*或者抛出异常，取决于你的应用策略*/
}

fun filesPath():PlatformFile?{
    if(filesPath!=null && filesPath ?.absolutePath()/*是否为绝对路径*/ ?.isNotBlank()/*是否不为空*/!!) return filesPath
    else
        print("外部私有文件路径获取失败！！！")
        return null
}
fun cachePath():PlatformFile?{
    if(cachePath!=null && filesPath ?.absolutePath() ?.isNotBlank()!!) return cachePath
    else
        print("外部私有临时缓存路径获取失败！！！")
        return null
}





