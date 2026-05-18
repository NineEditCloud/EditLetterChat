package com.nineeditcloud.editletterchat.database

//import com.attafitamim.kabin.core.database.configuration.KabinDatabaseConfiguration
//import kotlinx.cinterop.ExperimentalForeignApi
//import platform.Foundation.NSFileManager
//import platform.Foundation.NSDocumentDirectory
//import platform.Foundation.NSUserDomainMask
//
//@ExperimentalForeignApi
//actual fun createKabinConfig(dbName:String):KabinDatabaseConfiguration{
//    val documentsDir = NSFileManager.defaultManager.URLForDirectory(
//        directory = NSDocumentDirectory,
//        inDomain = NSUserDomainMask,
//        appropriateForURL = null,
//        create = false,
//        error = null
//                                                                   )!!
//    val dbPath = "${documentsDir.path}/$dbName.db"
//    return KabinDatabaseConfiguration(name="$dbName.db")
//}