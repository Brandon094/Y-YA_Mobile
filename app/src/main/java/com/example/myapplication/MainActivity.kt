package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.theme.YYATheme

/**
 * ACTIVIDAD PRINCIPAL (PUNTO DE ENTRADA)
 * Configura el entorno de Jetpack Compose y lanza el sistema de navegación global.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilita el diseño de borde a borde (edge-to-edge) para una experiencia moderna
        enableEdgeToEdge()

        setContent {
            // Aplicamos el Tema Global de la aplicación definido en UI/Theme
            YYATheme {
                // Lanzamos el componente de Navegación que controla todas las pantallas
                AppNavigation()
            }
        }
    }
}
