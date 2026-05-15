package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * CONFIGURACIÓN DE TIPOGRAFÍA
 * Define los estilos de texto (fuente, tamaño, grosor) utilizados de manera consistente
 * en toda la interfaz de usuario de YYA basándose en Material Design 3.
 */
val Typography = Typography(
    // Estilo predeterminado para bloques de texto largos (Cuerpo)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* 
     * Nota: Aquí se pueden personalizar otros estilos como:
     * - titleLarge (Títulos de pantallas)
     * - labelSmall (Textos en botones o etiquetas)
     * - headlineMedium (Enunciados destacados)
     */
)
