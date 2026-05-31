// kspPlugin/src/main/kotlin/WebPageProcessor.kt
//import com.google.devtools.ksp.processing.*
//import com.google.devtools.ksp.symbol.*
//import com.google.devtools.ksp.validate
//import java.io.OutputStream
//
//@Target(AnnotationTarget.FUNCTION)
//@Retention(AnnotationRetention.SOURCE)
//annotation class GenerateWebUi
//
//@GenerateWebUi
//@androidx.compose.runtime.Composable
//fun compose(comp:@Composable( () -> Unit)?/*接收Comp作为参数 slot模式*/=null){
//    comp?.invoke()/*开头 图标等组件，?.invoke：如果传递Comp不为空则调用*/
//}
//
//class WebPageProcessor(private val codeGenerator:CodeGenerator, private val logger:KSPLogger):SymbolProcessor{
//    override fun process(resolver: Resolver):List<KSAnnotated>{
//        val symbols=resolver.getSymbolsWithAnnotation("app.WebPage")
//        val validSymbols=symbols.filter{ it is KSFunctionDeclaration && it.validate() }
//
//        for(symbol in validSymbols){
//            val func=symbol as KSFunctionDeclaration
//            val body=func.simpleFunctionBody() ?: continue/*获取函数体(通过访问 Compose 调用表达式)*/
//            val generatedCode=generateWebPage(func.simpleName.asString(), body)/*解析并生成 HTML 代码*/
//            val file=codeGenerator.createNewFile(/*写入生成文件*/
//                dependencies=Dependencies(true, func.containingFile!!),
//                packageName="generated",
//                fileName=func.simpleName.asString() + "Web",
//                )
//            file.writer().use{ it.write(generatedCode) }
//        }
//        return symbols.filterNot { it.validate() }.toList()
//    }
//
//    // 极简 AST 解析：只处理直接嵌套的 Column/Row/Text/Button 调用
//    private fun generateWebPage(name:String, body:KSNode):String{
//        // 实际项目建议使用 KSP 的 visitor 或 PSI 进行深度解析
//        // 这里用伪代码演示：从函数体字符串中提取关键调用
//        val bodyText=body.toString() // 这只是字符串，不能真正解析，实际需要遍历 AST
//
//        // 将 Compose 组件映射为 Compose HTML DSL
//        val htmlCalls = bodyText
//            .replace("Column", "Div")          // 使用 Div，默认 block 布局
//            .replace("Row", "Div")             // 实际应该用 flex-direction: row
//            .replace("Text(\"", "P { Text(\"")
//            .replace("Button(onClick", "Button(attrs = { onClick")
//
//        return """
//package generated
//
//import androidx.compose.runtime.Composable
//import org.jetbrains.compose.web.dom.*
//import org.jetbrains.compose.web.css.*
//
//@Composable
//fun ${name}Web(){
//    /*注意：这里只是示意，真实转换需要精确处理*/
//    $htmlCalls
//}
//        """.trimIndent()
//    }
//}
//
//class WebPageProcessorProvider : SymbolProcessorProvider{
//    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
//        return WebPageProcessor(environment.codeGenerator, environment.logger)
//    }
}