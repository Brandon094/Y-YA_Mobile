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

/**
 * CONFIGURACIÓN DEL TEMA OSCURO
 * Define cómo se comportan los colores cuando el sistema está en modo noche.
 */
private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,           // Color principal para botones
    secondary = NavyBlue,           // Color para barras superiores
    background = BackgroundDark,    // Fondo general de la app
    surface = SurfaceDark,          // Fondo de tarjetas y listas
    onPrimary = White,              // Texto sobre color primario
    onBackground = TextPrimaryDark, // Texto sobre fondo general
    onSurface = TextPrimaryDark,    // Texto sobre tarjetas
    onSecondary = White,            // Texto/Iconos sobre la barra azul
    error = Color(0xFFFF453A)       // Rojo estándar para errores en modo oscuro
)

/**
 * CONFIGURACIÓN DEL TEMA CLARO
 * Colores estándar para uso diurno.
 */
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

    // Aplicamos el tema a toda la jerarquía de la aplicación
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usa la tipografía definida en Type.kt
        content = content
    )
}
