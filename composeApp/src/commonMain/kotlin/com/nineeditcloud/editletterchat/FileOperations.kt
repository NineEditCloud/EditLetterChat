package com.nineeditcloud.editletterchat

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
val filesPath:PlatformFile?=(FileKit.filesDir ?.absolutePath()?.isNotBlank() ) as PlatformFile?/*获取应用私有文件目录，   若获取失败则为空*/
val cachePath:PlatformFile?=(FileKit.cacheDir ?.absolutePath()?.isNotBlank() ) as PlatformFile?/*获取应用私有临时缓存目录，若获取失败则为空*/
fun filePath():PlatformFile?{
    if(filesPath!=null && filesPath ?.absolutePath()?.isNotBlank()!!){
        return filesPath
    }else{
        print("外部私有文件路径获取失败！！！")
        return null
    }
}
fun cachePath():PlatformFile?{
    if(cachePath!=null && filesPath ?.absolutePath()?.isNotBlank()!!){
        return cachePath
    }else{
        print("外部私有临时缓存路径获取失败！！！")
        return null
    }
}





