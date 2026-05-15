import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*shared部分负责共享非UI代码*/
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
//    id("kotlin-kapt")/*kapt依赖插件，Kotlin的Room框架注解处理器包含此类依赖*/
//    id("com.google.devtools.ksp") version "2.1.21" apply false /*KSP依赖插件(替代kapt)，由于kapt打包问题，尝试KSP，KSP必须与Kotlin兼容*/
//    id("androidx.room") version "2.6.1" apply false
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
//    alias(libs.plugins.realm.kotlin)/*应用 Realm插件*/
//    alias(libs.plugins.krdb)/*应用 Krdb插件(Realm新版，支持Kotlin2.1+)*/

//    alias(libs.plugins.exoquery)/*应用 ExoQuery插件*/
}


kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
//    iosX64();iosArm64();iosSimulatorArm64()
//    listOf(iosArm64(), iosSimulatorArm64() ).forEach { iosTarget ->/*IOS目标*/
//        iosTarget.binaries.framework /*IOS目标 二进制框架*/{
//            baseName="ComposeApp"
//            isStatic=true/*是否静态*/
//        }
//    }

    jvm()
    
//    js {
//        browser()
//    }
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//    }
    
    sourceSets{
        commonMain.dependencies/*常规共享依赖*/{ // put your Multiplatform dependencies here

        }
        commonTest.dependencies/*常规测试共享依赖*/{

        }

        androidMain.dependencies/*安卓端依赖部分*/{

        }
        iosMain.dependencies/*IOS端依赖部分*/{


        }

        jvmMain.dependencies/*JVM桌面端依赖部分*/{

        }

        nativeMain.dependencies/*Kotlin/Native IOS/MacOS/Linux*/{
        }

//        jsMain.dependencies/*JS运行依赖*/{
//        }
//        wasmJsMain.dependencies/*wasmJS运行依赖*/{
////            implementation("org.jetbrains.compose.material3:material3:1.10.0")/*支持wasmJS的material3版本*/
//        }
    }

//    sourceSets.configureEach {
//        kotlin.srcDir("${layout.buildDirectory.get().asFile}/generated/ksp/$name/kotlin/")/*指定 Room Schema 的导出路径(对KSP同样需要)，buildDir已弃用，使用新的API获取构建路径*/
//        kotlin.srcDir("${layout.buildDirectory.get().asFile}/generated/ksp/metadata/commonMain/kotlin/")/*Kabin，关键：让项目识别KSP生成的代码*/
//    }

}
dependencies{
//    add("kspCommonMainMetadata", "androidx.room:room-compiler:${libs.versions.room}")/*添加处理器到 commonMain(用于处理共享代码)，设备不足以编译所有平台的话这样加会报错*/
//    add("kspAndroid", "androidx.room:room-compiler:${libs.versions.room}")/*为 Android 平台添加处理器*/

    /*为各iOS架构添加处理器*/
//    add("kspIosSimulatorArm64", "androidx.room:room-compiler:${libs.versions.room}")
//    add("kspIosX64", "androidx.room:room-compiler:${libs.versions.room}")
//    add("kspIosArm64", "androidx.room:room-compiler:${libs.versions.room}")

//    add("kspDesktop", "androidx.room:room-compiler:${libs.versions.room}")/*为桌面平台添加处理器*/
//    add("kspJvm", ...)/*如果你的JVM目标命名为"jvm"*/
//    add("kspJs", ...)

    add("kspCommonMainMetadata", "com.attafitamim.kabin:compiler:${libs.versions.kabin.get()}")/*Kabin配置KSP依赖，否则无法生成代码*/

    listOf("kspCommonMainMetadata"/*commonMain(用于处理共享代码)*/,
           "kspAndroid"/*安卓*/,
//           "kspIosArm64", "kspIosX64", "kspIosSimulatorArm64",/*IOS系列架构*/
           "kspJvm",/*由kotlin块中JVM目标的命名决定*/
//           "kspDesktop",/*ComposeMultiplatform项目中 JVM桌面目标常用*/
//           "kspJs",/*适用于 Kotlin/JS 目标*/
//           "kspNative"/*适用于 Kotlin/Native 目标*/
          ).forEach{ target ->/*循环遍历 每次赋值给target*/
        add(target, libs.androidx.room.compiler)/*为各平台添加Room处理器*/

    }


//    ksp("androidx.room:room-compiler:${libs.versions.room}")/*Room注解处理器 (Kotlin项目使用kapt 或 更新的KSP 插件)*/
//    implementation("androidx.room:room-ktx:${libs.versions.room}")/*可选-为Room添加Kotlin扩展和协程支持，提供更便捷的挂起函数*/
//    implementation("androidx.room:room-rxjava2:${libs.versions.room}")/*RoomRXJava2扩展*/
//    implementation("androidx.room:room-rxjava3:${libs.versions.room}")/*RoomRXJava3扩展*/
//    testImplementation("androidx.room:room-testing:${libs.versions.room}")/*Room测试支持扩展*/
//    implementation(libs.androidx.sqlite.bundled)

//    listOf(
//        "kspIosArm64", "kspIosX64", "kspIosSimulatorArm64",/*IOS系列架构*/
//        "kspJvm"/*由kotlin块中JVM目标的命名决定*/
//        "kspDesktop",/*ComposeMultiplatform项目中 JVM桌面目标常用*/
//        "kspJs",/*适用于 Kotlin/JS 目标*/
//        "kspNative"/*适用于 Kotlin/Native 目标*/
//          ).forEach{ target ->/*循环遍历 每次赋值给target*/
//              add(target, "com.ctrip.sqllin:sqllin-processor:${libs.versions.sqllin.get()}")/*除安卓外，配置KSP以处理使用SQLlin依赖中的注解*/
//        }


}
room{/*Room配置*/
    schemaDirectory("$projectDir/schemas")/*Room架构导出目录*/
}

/*以下是为了解决 KSP 在 Kotlin Multiplatform 中的元数据依赖问题*/
tasks.withType<KotlinCompile>{
    if(name != "kspCommonMainKotlinMetadata"){
        dependsOn("kspCommonMainKotlinMetadata")
    }
}



android {
    namespace = "com.nineeditcloud.editletterchat.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
