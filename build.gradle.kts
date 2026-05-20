plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false

    alias(libs.plugins.ksp) apply false/*引入 KSP插件*/

    alias(libs.plugins.realm) apply false/*引入 Realm插件*/
//    alias(libs.plugins.krdb) apply false/*引入 Krdb插件(Realm新版，支持Kotlin2.1+)，下载失败*/

//    alias(libs.plugins.exoquery) apply false/*引入 ExoQuery插件，下载失败*/

//    id("dev.icerock.mobile.multiplatform-resources") version "${libs.versions.mokoResources.get()}" apply false/*引入 MokoResources综合资源库 插件*/
//    alias(libs.plugins.mokoResources) apply false/*引入 MokoResources综合资源库 插件*/
}