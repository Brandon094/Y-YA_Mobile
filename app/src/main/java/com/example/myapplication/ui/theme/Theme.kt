package com.example.myapplication.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    secondary = NavyBlue, // Cambiado de White a NavyBlue
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSecondary = White, // Letras blancas sobre el fondo azul/gris
    error = Color(0xFFFF453A)
)

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    secondary = NavyBlue,
    background = BackgroundLight,
    surface = White,
    onPrimary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSecondary = White
)

@Composable
fun YYATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
