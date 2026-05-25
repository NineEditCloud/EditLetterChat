package com.nineeditcloud.editletterchat.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/*深色主题*/
private val DarkColorScheme=darkColorScheme(
    primary=Purple80,
    secondary=PurpleGrey80,
    tertiary=Pink80,
    surface=Color(0xFF171515)/*深色底色*/
)
/*浅色主题*/
private val LightColorScheme=lightColorScheme(
    primary=Purple40,
    secondary=PurpleGrey40,
    tertiary=Pink40,
    surface=Color(0xffffffff)/*浅色底色*/,
    background=Color(0xffffffff)/*浅色背景*/

    /* Other default colors to override
    background=Color(0xFFFFFBFE),
    surface=Color(0xFFFFFBFE),
    onPrimary=Color.White,
    onSecondary=Color.White,
    onTertiary=Color.White,
    onBackground=Color(0xFF1C1B1F),
    onSurface=Color(0xFF1C1B1F),
    */
)

@Composable
fun Theme(
    darkTheme:Boolean=isSystemInDarkTheme(),
    dynamicColor:Boolean=true,/*Dynamic color is available on Android12+*/
    content:@Composable (() -> Unit) ){
    val colorScheme=when{
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context=LocalContext.current
            if(darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme=colorScheme, typography=Typography, content=content)
}