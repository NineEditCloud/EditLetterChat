@file:Suppress("UnstableApiUsage")

rootProject.name="EditLetterChat"
//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement{
    repositories{
        google{
            mavenContent{
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()/*本地仓库*/
        maven{ url=uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }/*JetBrains-ComposeMultiplatform专用仓库(关键！)*/
        maven{ url=uri("https://plugins.gradle.org/m2/") }

        maven{ url=uri("https://maven.aliyun.com/repository/gradle-plugin") }/*1.配置Gradle插件镜像 阿里云源*/
        maven{ url=uri("https://maven.aliyun.com/repository/public") }/*2.配置阿里云公共仓库镜像*/
        maven{ url=uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/") }/*腾讯Maven仓库*/
    }
}

dependencyResolutionManagement{
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories{
        google{
            mavenContent{
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
        mavenCentral()/*公共仓库*/
        mavenLocal()/*本地仓库*/
        /*maven{url=uri("")}依赖仓库综合写法，maven("")依赖仓库便捷写法*/
        maven{ url=uri("https://company/com/maven2") }/*Company仓库*/
        maven{ url=uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }/*JetBrains-ComposeMultiplatform专用仓库(关键！)*/
        maven{ url=uri("https://dl.google.com/dl/android/maven2") }/*谷歌官方仓库，若上述仓库仍找不到，可尝试添加此仓库*/
//        maven("https://jitpack.io/")/*GitHub项目仓库方案*/
//        maven("https://github.com/")

        maven{ url=uri("https://maven.aliyun.com/repository/central") }/*阿里云源mavenCentral */
        maven{ url=uri("https://maven.aliyun.com/repository/public") }/*阿里云公共仓库镜像(用于大多数开源库)*/
        maven{ url=uri("https://maven.aliyun.com/repository/google") }/*阿里云源Google仓库镜像(用于Firebase、AndroidX等) */
//        maven("https://mirrors.tencent.com/repository/maven/kuikly")/*腾讯Kuikly跨平台适应原生界面框架-仓库，已用于其它*/
        maven{ url=uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/") }/*腾讯Maven仓库*/



//        flatDir{/*文件目录*/
//            dirs("libs")
//        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
//    id("dev.icerock.moko:resources-generator") version "0.26.0"/*引入 MokoResources综合资源库*/
}

include(":composeApp")/*导入 composeApp(Compose程序共享代码) 模块*/
//include(":server")/*导入 server(应用内置服务) 模块*/
//include(":shared")/*导入 shared(非UI共享代码) 模块*/
/*若不用shared(非UI区分)模块 将composeApp里的implementation(project(":shared"))注释掉 否则Gradle因找不到模块而报错
* 迁移代码：将shared/src/commonMain/常规共享 以及 各平台特定代码 搬到 composeApp对应同名路径
* 迁移依赖项：shared模块引用的所有第三方库(如Ktor SQLDelight kotlinx.coroutines等)，都加到 composeApp/build.gradle.kts 的 commonMain 依赖块里
* 检查插件配置：shared模块 的 build.gradle.kts 里若声明了某些插件(如kotlin("plugin.serialization") )，而现在composeApp 需要这些能力，要确认 composeApp 里也加了这些插件
*/


