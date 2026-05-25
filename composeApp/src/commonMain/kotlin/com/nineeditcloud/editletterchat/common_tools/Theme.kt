package com.nineeditcloud.editletterchat.common_tools

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*KMP跨平台 Compose应用主题*/
@Composable
fun AppTheme(darkTheme:Boolean=isSystemInDarkTheme(), content:@Composable ()->Unit ){
    val colors=if(darkTheme) DarkColors else LightColors

    MaterialTheme(colorScheme=colors, typography=AppTypography, shapes=AppShapes, content=content)
}

val LightColors=lightColorScheme(
    primary=Color(0xFF6200EA),    /*深紫色*/
    secondary=Color(0xFF3C03DA),
    background=Color.White,
    )
val DarkColors=darkColorScheme(
    primary=Color(0xFFBB86FC),    /*淡紫色*/
    secondary=Color(0xFF1803DA),
    background=Color(0xFF121212), /*深灰色*/
    )

val AppTypography=Typography(
    headlineLarge=TextStyle(fontWeight=FontWeight.Bold, fontSize=30.sp, lineHeight=36.sp),
    bodyMedium=TextStyle(fontSize=16.sp),/*...更多样式*/
    )
val AppShapes=Shapes(small=RoundedCornerShape(4.dp), medium=RoundedCornerShape(8.dp), large=RoundedCornerShape(16.dp) )