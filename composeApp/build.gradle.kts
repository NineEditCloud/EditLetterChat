@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*若用了shared区分模块，composeApp部分负责共享应用GUI(不包括将Compose用于HTML)，若未用shared模块 则composeApp模块包括KMP项目全部内容*/
plugins{
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)/*Kotlin2.0.21版的ComposeCompiler插件有Bug*/
//    alias(libs.plugins.composeHotReload)/*仅支持Kotlin2.1.20+，在2.0.x版本不兼容，会自动注入参数：androidx.compose.compiler.plugins.kotlin:generateFunctionKeyMetaAnnotations=true*/
//    kotlin("jvm") version libs.versions.kotlin.get()

//    id("kotlin-kapt")/*kapt依赖插件，Kotlin的Room框架注解处理器包含此类依赖*/
    alias(libs.plugins.ksp)/*应用 KSP插件(替代kapt)，由于kapt打包问题，尝试KSP，KSP必须与Kotlin兼容*/
    alias(libs.plugins.androidx.room)/*应用 Room插件*/
//    alias(libs.plugins.realm)/*应用 Realm插件(对象型数据存储框架)*/
//    alias(libs.plugins.krdb)/*应用 Krdb插件(Realm新版，支持Kotlin2.1+)*/

//    alias(libs.plugins.exoquery)/*应用 ExoQuery插件*/
//    alias(libs.plugins.multiplatform.mokoResources)/*应用 MokoResources综合资源库 插件*/
    kotlin("plugin.serialization") version "${libs.versions.kotlin.get()}"/*应用Kotlin-serialization序列化插件*/
//    alias(libs.plugins.kotlin.serialization)/*应用 Kotlin-serialization序列化插件，闪退*/
}
/*若依赖丢失导致项目报错，请先连接VPN，点击 Gradle -> 重新加载所有Gradle项目，等待依赖下载完成，
在顶部菜单点击 文件 -> 从磁盘全部重新加载 (或快捷键Ctrl+Alt+Y)

若Gradle丢失，先完全退出AndroidStudio(确保Gradle守护进程已停止)，将C:\Users\Administrator\.gradle\caches 路径下的对应版本gradle文件夹删除，
重新打开AS，Gradle插件会自动同步并重新下载所有依赖

若清理.gradle文件夹后，build.gradle.kts内容全爆红，点击：File -> 修复IDE -> 重新扫描项目索引 -> 重新打开项目

若 Jvm或安卓 的java包报错，说明JDK丢失，
点击前往：File → Settings → Build,Execution,Deployment → Build Tools → Gradle → Gradle JVM criteria → Version
更改一下JDK版本(17或21)，点 Apply(应用) 选项，AndroidStudio会自动下载并保存JDK

推荐构建版本组合：
Gradle构建工具9.0.0(KMP兼容) + AndroidGradlePlugin8.13.0(Gradle9.0.0兼容) + Kotlin 2.3.21-2.0.21 + JDK17以上
Gradle9.3.0(兼容AGP9.0.0) + AGP9.0.0 + kotlin2.3.21 + JDK17以上
Gradle8.7 + AGP8.5.0 + Kotlin1.9.20-2.0.20 + JDK17以上：旧版兼容Realm2.3.0-3.0.0

要更新AndroidGradlePlugin版本的话，打开 Tools -> AGP Upgrade Assistant，查看最新版本，
并在 “项目文件夹/gradle/libs.versions.toml” 文件中更改versions中的agp浮点值
注意：安卓应用运行目标SDK版本只要未弃用，后续更新的系统版本 依旧可运行 较旧SDK版本的应用，一般兼容安卓 4.4或5.0 - 16 范围内版本即可

安卓打包失败不一定全是依赖和代码问题，有时候内存不足也会编译失败(建议关闭AndroidStudio再重启)
有时候软件闪退不一定全是依赖问题，有时数据库程序的Bug，安卓软件数据清理下

——必要AndroidSDK版本API
SDK Platforms：最新版、24(创建项目时最低选择安卓版本API)
SDK Platforms-Tools：36.0.2
SDK Build-Tools：最新版、35、34
Android Emulator 25.3.11

KMP框架能将Kotlin+Java项目 编译到Win、MacOS、Linux、Android、IOS、WEB的JS(但不支持将Compose应用于HTML等，JS和wasmJS的依赖只能放在shared部分)

KMP框架有时候Gradle找不到依赖 可能不是依赖仓库问题，而是找不到对应操作系统平台的依赖(比如找不到IOS版依赖)，原因是库的 发布链接和版本 没有某些平台的依赖

KMP可编写GitHub云编译配置文件，并在GitHub对项目使用 GitHub Actions 执行编译配置文件
Git提交了项目的多个变化版本时，选择推送时间最新的(这样使用的是最新项目状态)，否则可能推送失败

清理Gradle缓存
-1.停守护进程
gradlew --stop
-2.清理构建产物
gradlew clean
-3.删除 Gradle 缓存（关键！metadata 缓存在这）
Windows PowerShell：Remove-Item -Recurse -Force ~/.gradle/caches
cmd：rmdir /s /q %USERPROFILE%\.gradle\caches
或手动删对应依赖路径
-4.重新同步/构建
gradlew build --refresh-dependencies
*/

/*---KMP跨平台最方便好用的数据库框架
*分表情况：SQLlin(安卓6.0+，库已停止发布)，Exposed(未来KMP跨平台计划项目中)
*不分表情况：
* Kabin(基础增删改查 可能兼容安卓5.0 开发阶段 主依赖Room社区)，仿Room注解生成增删改SQL语句(使用SQLDelight作为驱动)，比Room写各平台构建器更简便
* ExoQuery(功能强大且独特 发展中 新社区)，依赖编译器插件编译时转换成原生SQL，JS即将支持，无需为各平台单独写构建器-通过不同的运行器(runner)模块配置(平台差异由框架内部处理)
* RealmKotlinSDK(功能完备稳定 不支持Kotlin2.1.0 兼容安卓4.1 最低维护状态 MongoDB团队)，编译器插件 操作、持久化 Kotlin对象，无为各平台写构建器-用本地Configuration对象一件初始化(平台差异在SDK内部处理)
* Krdb(Realm的新版 支持Kotlin2.1+ 兼容安卓4.1)
*/

kotlin{
    androidTarget/*安卓目标*/{
        compilerOptions/*编译选项*/{
            jvmTarget.set(JvmTarget.JVM_21)
//            optIn.add("kotlin.RequiresOptIn")
        }
        compilations.all{
            @Suppress("DEPRECATION") kotlinOptions{
                freeCompilerArgs += listOf("-Xjvm-default=all")
                freeCompilerArgs += listOf("-Xcontext-receivers")
//                freeCompilerArgs += listOf("-P", "plugin:androidx.compose.compiler.plugins.kotlin:functionKeyMetaClasses=true")
            }
        }
    }

//    iosX64();iosArm64();iosSimulatorArm64()
    listOf(iosX64(), iosArm64(), iosSimulatorArm64() ).forEach{ iosTarget ->/*遍历多个IOS架构，每次赋值给iosTarget(若不写传参名 则默认it)*/
        iosTarget.binaries.framework{/*IOS目标二进制框架*/
            baseName="TodoApp"
            isStatic=true
//            linkerOpts.add("-lsqlite3")/*Required when using NativeSQLiteDriver*/
//            export(libs.androidx.lifecycle.viewmodelCompose)/*导出 ViewModel依赖API，以便从Swift进行访问*/
        }
    }
    
    jvm()/*JVM桌面目标*/
//    linuxX64();linuxArm64()
//    mingwX64()

//    js{
//        browser()
//        binaries.executable()
//    }
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs{
//        browser()
//        binaries.executable()
//    }
    
    sourceSets/*源依赖集合*/{
        commonMain.dependencies/*常规依赖*/{
//            implementation(projects.shared)/*应用 shared(非UI共享代码) 模块*/

            implementation("org.jetbrains.compose.runtime:runtime:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.foundation:foundation:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.ui:ui:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.composeMultiplatform.get()}")/*CMP跨平台 返回键事件库*/
            implementation("org.jetbrains.compose.components:components-resources:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.material:material:${libs.versions.material.get()}")/*Material组件与主题属性，跨平台版，最高1.7.0*/
//            implementation("org.jetbrains.compose.material:material-icons-extended:${libs.versions.material.get()}")/*MaterialIcons图标库 跨平台通用版*/
            implementation("org.jetbrains.compose.material3:material3:1.9.0")/*Compose基础Material包控件、组件，最高1.4.0兼容安卓5.0，但不支持wasmJS，1.6.0兼容安卓5.0*/
//            implementation("org.jetbrains.compose.animation:animation")

//            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${libs.versions.androidx.lifecycle.get()}")/*lifecycle-ViewModelCompose，KMP跨平台 ViewModel-Compose协程库*/
//            implementation("androidx.lifecycle:lifecycle-runtime-compose:${libs.versions.androidx.lifecycle.get()}")
            implementation(libs.androidx.lifecycle.viewmodelCompose)/*lifecycle-ViewModelCompose，KMP跨平台 ViewModel-Compose协程库*/
            implementation(libs.androidx.lifecycle.runtimeCompose)
//            api("androidx.lifecycle:lifecycle-viewmodel:${libs.versions.androidx.lifecycle}")/*KMP跨平台 ViewModel协程库*/

            val navVersion="2.9.1"/*jetbrains发布的跨平台版Navigation版本，2.9.0-beta01为首个支持KMP版 兼容1.8.0，2.9.1支持CMP1.9.0-rc01 且兼容安卓5.0*/
            implementation("org.jetbrains.androidx.navigation:navigation-compose:$navVersion")/*Nav Compose导航图组件 跨平台版，功能特性，不可缺少，其实这一个就够了*/
//            implementation("org.jetbrains.androidx.navigation:navigation-fragment:$nav_version")/*Java的 Nav内嵌导航界面*/
//            implementation("org.jetbrains.androidx.navigation:navigation-ui:$nav_version")/*Java的 Nav UI*/
//            implementation("org.jetbrains.androidx.navigation:navigation-fragment-ktx:$navVersion")/*Kotlin Nav内嵌导航界面 跨平台，用来自己写导航图和导航*/
//            implementation("org.jetbrains.androidx.navigation:navigation-ui-ktx:$navVersion")/*Kotlin NavUI 跨平台*/
//            implementation("org.jetbrains.androidx.navigation:navigation-dynamic-features-fragment:$navVersion")/*Nav功能特性模块 跨平台*/
//            implementation("org.jetbrains.androidx.navigation3:navigation3-ui:1.0.0-alpha05")/*navigation3(navigation新版)，所有新版皆支持KMP跨平台*/

            implementation("cafe.adriel.voyager:voyager-navigator:${libs.versions.voyager.get()}")/*Voyager-Navigation 跨平台通用界面导航依赖，1.1.0-beta03*/
            implementation("cafe.adriel.voyager:voyager-screenmodel:${libs.versions.voyager.get()}")/*Voyager-Screen模块*/
            implementation("cafe.adriel.voyager:voyager-transitions:${libs.versions.voyager.get()}")

            implementation(compose.materialIconsExtended)/*MaterialIcons图标库 跨平台通用版，自动根据项目配置 为所有目标平台解析正确依赖，该库包含所有Material图标，体积庞大，务必启用 R8/ProGuard 以缩减包体积*/
//            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)/*OptIn选项可能引发报错？*/
//            implementation(compose.components.resources)/*compose通用资源，可能含painterResource用的composeResources资源、Res类 和 @Preview预览注解等(但Android端会被 actual 绕过)*/
//            implementation("io.github.rabehx:iconsax-compose:2.1.1")/*Iconsax-Compose，imageVector用的超千款图标*/
//            implementation("br.com.devsrsouza.compose.icons:simple-icons:${libs.versions.composeIcons.get()}")/*Compose-Icons Simple简易图标库*/
//            implementation("br.com.devsrsouza.compose.icons:tabler-icons:${libs.versions.composeIcons.get()}")/*Compose-Icons Tabler图标包*/
            implementation("br.com.devsrsouza.compose.icons:octicons:${libs.versions.composeIcons.get()}")/*Compose-Icons Octicons图标库，imageVector用的多套开源图标包之一*/
//            implementation("br.com.devsrsouza.compose.icons:font-awesome:${libs.versions.composeIcons.get()}")/*Compose-Icons Font-Awesome*/
//            implementation("br.com.devsrsouza.compose.icons:line-awesome:${libs.versions.composeIcons.get()}")/*Compose-Icons Line-Awesome*/
//            api("dev.icerock.moko:resources:${libs.versions.mokoResources.get()}")/*mokoResources综合资源 核心依赖*/
//            api("dev.icerock.moko:resources-compose:${libs.versions.mokoResources.get()}")/*mokoResources综合资源 Compose支持，含painterResource用的图标资源*/

            implementation("com.darkrockstudios:mpfilepicker:3.1.0")/*基于ComposeMultiplatform框架的 跨平台 文件选择器组件*/

//            implementation("io.github.dokar3:sonner:0.3.1")/*Compose-Sonner，跨平台Toast底部弹窗提示(与布局有绑定关系)*/
            implementation("io.github.the-best-is-best:compose_toast:${libs.versions.composeToast.get()}")/*Compose_Toast跨平台底部弹窗提示，兼容安卓5.0，自定义UI依赖Box堆叠容器，含自适应原生弹窗功能不依赖布局定位*/
//            implementation("io.github.the-best-is-best:compose-utils:${libs.versions.composeToast.get()}")/*Compose-Utils*/
//            implementation("io.github.the-best-is-best:kadaptiveui:1.2.0")/*KAdaptiveUI 跨平台Toast底部弹窗提示，仅兼容安卓7.0+，含自适应原生弹窗功能不依赖布局定位，且可自定义弹窗内容为Compose组件*/
//            implementation("io.github.khubaibkhan4:alert-kmp:0.0.4")/*Alert-KMP，兼容安卓7.0+，极致便捷的跨平台底部弹窗提示，完全不依赖Box或布局绑定*/
//            implementation("network.chaintech:cmptoast:1.0.8")/*CMPToast 跨平台底部弹窗提示，兼容安卓5.0，但与安卓name获取Context冲突*/





            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${libs.versions.kotlinx.get()}")/*Kotlin序列化核心库-跨平台，ExoQuery和JSON所需依赖，*/
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${libs.versions.kotlinx.get()}")/*Kotlin序列化-Json，包含JSON 编解码/序列化/反序列化*/
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.kotlinx.get()}")/*Kotlin协程-跨平台核心(为各平台分配协程依赖或内置主线程调度器，内置Kotlin/Native作为IOS依赖)，Room内部依赖需要，各平台依赖提供Dispatchers.Main，可在后台线程做复杂操作，并自动回到主线程更新UI*/

            implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")/*Ktor-客户端功能共享核心，不带引擎*/
//            implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")/*Ktor-CIO 纯Kotlin跨平台通用网路请求引擎(建议给各平台加各自的，不加的话HttpClient方法传CIO)，或apache、java*/
            implementation("io.ktor:ktor-network:${libs.versions.ktor.get()}")/*Ktor-Network模块，提供原始TCP和UDP 套接字支持*/
            implementation("io.ktor:ktor-client-content-negotiation:${libs.versions.ktor.get()}")/*Ktor-内容协商*/
            implementation("io.ktor:ktor-serialization-kotlinx-json:${libs.versions.ktor.get()}")/*Ktor协商-serialization适配器-序列化JSON(需内容协商)*/

            implementation("androidx.room:room-runtime:${libs.versions.room.get()}")/*Room核心库，Room2.x会导致KSP反射Bug，3.x不兼容安卓5.0*/
            implementation("androidx.sqlite:sqlite-bundled")/*SQLite数据库依赖*/
//            implementation("com.attafitamim.kabin:core:${libs.versions.kabin.get()}")/*Kabin核心库，机制防Room*/
//            implementation("io.realm.kotlin:library-base:${libs.versions.realm.get()}")/*Realm 对象型数据存储框架*/

            implementation("io.github.vinceglb:filekit-core:${libs.versions.filekit.get()}")/*FileKit核心库(仅兼容Kotlin2.1+)，跨平台 文件操作 和 应用私有路径访问*/
            implementation("io.github.vinceglb:filekit-dialogs:${libs.versions.filekit.get()}")
            implementation("io.github.vinceglb:filekit-dialogs-compose:${libs.versions.filekit.get()}")
            implementation("io.github.vinceglb:filekit-coil:${libs.versions.filekit.get()}")/*fileKit选取图片文件，内部依赖不兼容Kotlin旧版，建议用AsyncImage(model=selectedFile.path)*/
//            implementation("org.jetbrains.kotlinx:kotlinx-io-core:${libs.versions.kotlinxIo.get()}")/*Kotlinx-IO(疑似依赖链接失效) 字节流/字符流、缓冲、协程读写*/
//            implementation("org.jetbrains.kotlinx:kotlinx-io-bytestring:${libs.versions.kotlinxIo.get()}")/*Kotlinx-IO-ByteString高效字节串*/
//            implementation("org.jetbrains.kotlinx:kotlinx-files:${libs.versions.kotlinxIo.get()}")/*Kotlinx跨平台文件系统API(Path,Directory,FileSystem)*/
//            implementation("com.squareup.okio:okio:${libs.versions.okio.get()}")/*OkIO(疑似依赖链接失效) 文件操作核心库*/
//            implementation("com.squareup.okio:okio-okfile:${libs.versions.okio.get()}")/*OkIO-文件系统扩展*/
//            implementation("dev.zwander:kmpfile:${libs.versions.kmpFile.get()}")/*KMPFile，文件操作、应用私有路径获取框架*/
//            implementation("dev.zwander:kmpfile-filekit:${libs.versions.kmpFile.get()}")/*KMPFile与FileKit协作，包含其依赖*/
//            implementation("dev.zwander:kmpfile-okio:${libs.versions.kmpFile.get()}")   /*KMPFile与Okio协作，包含其依赖*/

        }
        commonTest.dependencies/*常规测试共享依赖*/{
            implementation("org.jetbrains.kotlin:kotlin-test:${libs.versions.kotlin.get()}")/*Kotlin测试依赖*/
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${libs.versions.kotlinx.get()}")/*Kotlin协程依赖测试版*/
        }

        androidMain.dependencies/*安卓依赖*/{
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:${libs.versions.composeMultiplatform.get()}")/*Compose 1.9.0-rc01版本兼容安卓5.0，但不兼容IOS*/
            implementation("androidx.activity:activity-compose:1.11.0")/*安卓专用工具库，1.11.0版本兼容安卓5.0，绝对不可更改为更高版本！！！*/
            implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")/*安卓系统栏透明库，0.36.0兼容安卓5.0*/



            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.kotlinx.get()}")/*Kotlin协程-安卓，Room内部依赖需要*/
            implementation("io.ktor:ktor-client-okhttp:${libs.versions.ktor.get()}")/*Ktor-安卓端底层OkHttp引擎*/

//            implementation("androidx.room:room-sqlite-wrapper")/*Room需要的SQLite库，Room2.8+引入的库(2.8+可用)*/
//            implementation("org.jetbrains.exposed:exposed-core:1.2.0")/*Exposed核心模块(必须)，由于Room不方便分表，SQLlin不兼容安卓5.0，所以安卓用Exposed*/
//            implementation("org.jetbrains.exposed:exposed-jdbc:1.2.0")/*Exposed数据传输模块(必须)：使用 JDBC 作为传输层*/
//            implementation("org.jetbrains.exposed:exposed-dao:1.2.0")/*Exposed DAO模块(可选)：提供更高层级的 DAO API*/
//            implementation("com.h2database:h2:2.1.214")/*数据库驱动，以H2为例*/
        }
        iosMain.dependencies/*IOS端依赖*/{
//            implementation("org.jetbrains.compose.window:window:${libs.versions.composeMultiplatform.get()}")

            implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")/*Ktor-IOS端底层Darwin引擎*/

//            implementation("com.ctrip.sqllin:sqllin-dsl:${libs.versions.sqllin.get()}")/*SQLlin，跨平台且方便分库分表的DSL数据库框架，KSP用于编译时生成代码，dsl模块已包含必要注解，无需额外添加*/
//            implementation("com.ctrip.sqllin:sqllin-driver:${libs.versions.sqllin.get()}")/*一套通用的多平台SQLite低阶API，DSL的底层依赖*/
        }

        jvmMain.dependencies/*JVM桌面运行依赖*/{
            implementation(compose.desktop.currentOs)/*桌面端GUI预览引擎依赖，1.7.x已自动包含，手补以防万一*/
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${libs.versions.kotlinx.get()}")/*Kotlin协程-JVM桌面 Room内部依赖需要，包含Dispatchers.Main*/
            implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")/*Ktor-纯Kotlin跨平台网路请求引擎(建议给各平台加各自的，不加的话HttpClient方法传CIO)，或apache、java*/



//            implementation("com.ctrip.sqllin:sqllin-dsl:${libs.versions.sqllin.get()}")/*SQLlin，跨平台且方便分库分表的DSL数据库框架，KSP用于编译时生成代码，dsl模块已包含必要注解，无需额外添加*/
//            implementation("com.ctrip.sqllin:sqllin-driver:${libs.versions.sqllin.get()}")/*一套通用的多平台SQLite低阶API，DSL的底层依赖*/
//            implementation("io.exoquery:exoquery-runner-jdbc:${libs.versions.exoqueryRun.get()}")/*JVM runner*/
//            implementation("org.postgresql:postgresql:42.7.0")/*JDBC驱动*/
        }

        nativeMain.dependencies/*Kotlin/Native IOS/MacOS/Linux*/{
//            implementation("io.exoquery:exoquery-runner-native:1.0.0")/*Native runner*/
//            implementation("app.cash.sqldelight:native-driver:2.0.2")/*SQLDelight native driver (可选)*/
        }

//        jsMain.dependencies/*JS运行依赖*/{
//        }
//        wasmJsMain.dependencies/*wasmJS运行依赖*/{
//            implementation("org.jetbrains.compose.material3:material3:1.10.0")/*支持wasmJS的material3版本*/
//        }
    }

    sourceSets.configureEach{
        kotlin.srcDir("${layout.buildDirectory.get().asFile}/generated/ksp/$name/kotlin/")/*指定 Room Schema 的导出路径(对KSP同样需要)，buildDir已弃用，使用新的API获取构建路径*/
//        kotlin.srcDir("${layout.buildDirectory.get().asFile}/generated/ksp/metadata/commonMain/kotlin/")/*Kabin，关键：让项目识别KSP生成的代码*/
    }
}
dependencies/*可用于部分平台调用的共享依赖*/{
//    implementation(platform("androidx.compose:compose-bom:2024.09.00"))/*Compose-Bom物料清单(必备，否则下载包不全)，最高2024.09.00支持安卓5.0，改了更高版本会有内容缺失*/
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:${libs.versions.composeMultiplatform.get()}")



//    implementation("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")/*Kotlin标准库*/
//    implementation("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")/*Kotlin反射依赖，KSP必须！！！*/
    implementation(kotlin("stdlib"))/*Kotlin标准库，用Kotlin插件添加对应版本*/
    implementation(kotlin("reflect"))/*Kotlin反射依赖，用Kotlin插件添加对应版本*/



    listOf(
//        "kspCommonMainMetadata"/*commonMain(用于处理共享代码)，设备不足以编译至所有目标时添加会导致异常*/,
        "kspAndroid"/*安卓目标*/,
        "kspIosArm64", "kspIosX64", "kspIosSimulatorArm64", /*IOS系列架构目标，设备不足以编译时添加会导致异常*/
        "kspJvm",/*Kotlin块中JVM桌面目标的命名*/
//        "kspJs",/* Kotlin/JS 目标*/
//        "kspNative"/* Kotlin/Native 目标*/
        ).forEach{ target ->/*循环遍历 每次赋值给target*/
            add(target, libs.androidx.room.compiler)/*为各平台添加Room处理器，缺少此依赖会导致异常：Caused by: java.lang.ClassNotFoundException: com.nineeditcloud.editletterchat.database.AppDatabase_Impl*/
//            add(target, "com.ctrip.sqllin:sqllin-processor:${libs.versions.sqllin.get()}")/*除安卓外，配置KSP以处理使用SQLlin依赖中的注解*/
    }
    /*在AndroidX库的更新中，collection-ktx的功能已被合并进了collection主要库中，Room2.7.0内部仍然请求的是collection-ktx，所以需强制所有依赖底层用旧版collection-ktx库*/
//    implementation("androidx.collection:collection:1.2.0")/*强制所有依赖底层用指定的 collection库版本，避免版本冲突*/

//    implementation("com.attafitamim.kabin:compiler:${libs.versions.kabin.get()}")/*Kabin编译库*/

//    implementation("io.coil-kt.coil3:coil:${libs.versions.coil.get()}")/*fileKit-coil的内部底层依赖*/
//    implementation("io.coil-kt.coil3:coil-compose:${libs.versions.coil.get()}")/*fileKit-coil的内部底层依赖*/
}
configurations.all{/*全部配置*/
    resolutionStrategy.eachDependency{/*遍历依赖*/
//        if(requested.name/*依赖包名称*/.contains("filekit-coil") ){
//        }
        if(requested.group/*依赖包位置*/=="org.jetbrains.kotlin"){
            useVersion(libs.versions.kotlin.get())/*强制更改Kotlin所有库(包括stdlib) 与Koltin插件版本统一*/
        }
        if(requested.group=="io.coil-kt.coil3"){
            useVersion(libs.versions.coil.get())/*强制更改Coil为统一版本*/
        }
        if(requested.name.contains("kotlinx-io-bytestring")||requested.name.contains("kotlinx-io-core") ){
            useVersion(libs.versions.kotlinxIo.get())/*强制更改Kotlin-IO版本*/
        }
    }
}
//tasks.withType<KotlinCompile>{/*MokoResources资源问题-为解决KSP 在KotlinMultiplatform中的元数据依赖问题*/
//    if(name != "kspCommonMainKotlinMetadata"){
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}
room{/*Room配置*/
    schemaDirectory("$projectDir/schemas")/*Room架构导出目录*/
}

//multiplatformResources{/*moko-resources 配置块*/
//    resourcesPackage.set("com.nineeditcloud.editletterchat")/*【必需】生成的资源类包名*/
//    resourcesClassName.set("MR")                /*【可选】生成的资源类名，默认为MR*/
//    resourcesVisibility.set(MRVisibility.Public)/*【可选】资源类可见性，默认为Public*/
//    iosBaseLocalizationRegion.set("en")         /*【可选】iOS基础本地化区域*/
//    iosMinimalDeploymentTarget.set("11.0")      /*【可选】iOS最低版本*/
//}

android/*安卓目标配置*/{
    namespace="com.nineeditcloud.editletterchat"/*应用包名*/
    compileSdk=libs.versions.android.compileSdk.get().toInt()/*编译SDK版本*/
    defaultConfig{
        applicationId="com.nineeditcloud.editletterchat"/*应用包名*/
        minSdk=libs.versions.android.minSdk.get().toInt()/*最低兼容SDK版本*/
        targetSdk=libs.versions.android.targetSdk.get().toInt()/*目标SDK版本*/
        versionCode=1/*版本代码*/
        versionName="1.0"/*版本名*/
        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
    }
    packaging{
        resources{
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes/*编译类型*/{
//        release{
//            isMinifyEnabled=false/*是否开启 安装包体积最小化，不建议启用，会混淆类(容易导致某些类互相影响)，MultiDex需要手动配置，容易导致很麻烦*/
//            multiDexEnabled=true/*以防安卓包体积最小化会混淆类，导致出现异常，启用此参数*/
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
//            signingConfig/*签名配置*/=signingConfigs.getByName/*根据签名名称获取*/("release")
//        }
        getByName/*根据签名名称获取*/("release"){
            isMinifyEnabled=false/*是否开启 安装包体积最小化，不建议启用(保持false，注释会闪退)，会混淆类(容易导致某些类互相影响)，MultiDex需要手动配置，容易导致很麻烦*/
        }
    }
    compileOptions/*编译选项*/{
        sourceCompatibility=JavaVersion.VERSION_21
        targetCompatibility=JavaVersion.VERSION_21
    }
//    experimental { enableAndroidResources=true }
//    @Suppress("UnstableApiUsage") @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
//    experimentalProperties["android.experimental.kmp.enableAndroidResources"]=true/*实验性功能：将commonMain的资源 合并为Android资源*/
}

compose.desktop/*Compose桌面目标配置*/{
    application/*应用*/{
        mainClass="com.nineeditcloud.editletterchat.MainKt"/*主类*/
        nativeDistributions{
            targetFormats/*桌面目标系统平台*/(TargetFormat.Msi/*Win老安装包*/, TargetFormat.Exe/*Win执行包*/, TargetFormat.Dmg/*MacOS安装包*/,
                                              TargetFormat.Rpm/*RedHatLinux软件包*/,TargetFormat.Deb/*DebianLinux软件包*/)
            packageName="EditLetterChat"/*包名，含中文时build.gradle.kts配置文件要用GBK中文编码？*/
            packageVersion="1.0.0"/*包版本*/
        }
    }
}
