package com.bhplusplus.yaya.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CONFIGURACIÓN DEL TEMA OSCURO (PREMIUM DARK)
 * Implementa una paleta equilibrada de alto contraste basada en tonos Slate.
 */
private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    primaryContainer = Color(0xFF991B1B), // Rojo oscuro para contenedores
    onPrimaryContainer = Color(0xFFFEE2E2),
    
    secondary = NavyBlue,
    onSecondary = White,
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFF1F5F9),

    tertiary = TertiaryGold,
    onTertiary = Color(0xFF451A03),
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    
    outline = Color(0xFF475569),
    error = Color(0xFFEF4444),
    onError = White
)

/**
 * CONFIGURACIÓN DEL TEMA CLARO
 */
private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    primaryContainer = Color(0xFFFEE2E2),
    
    secondary = NavyBlue,
    onSecondary = White,
    secondaryContainer = Color(0xFFE2E8F0),
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    
    outline = Color(0xFFCBD5E1)
)

/**
 * FUNCIÓN DEL TEMA GLOBAL (YYATheme)
 * Este componente envuelve toda la aplicación y aplica los colores automáticamente
 * detectando si el celular del usuario está en modo oscuro o claro.
 */
@Composable
fun YYATheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detecta el modo del sistema
    dynamicColor: Boolean = false,              // Desactivamos colores dinámicos de Android 12+ para mantener marca
    content: @Composable () -> Unit
) {
    // Seleccionamos la paleta de colores basándonos en el tema del sistema
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Manejo de las barras del sistema (Status Bar y Navigation Bar) para una experiencia pro
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Aplicamos el tema a toda la jerarquía de la aplicación
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usa la tipografía definida en Type.kt
        content = content
    )
}
