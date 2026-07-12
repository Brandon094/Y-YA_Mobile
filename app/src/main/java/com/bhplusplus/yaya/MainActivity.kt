package com.bhplusplus.yaya

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.navigation.AppNavigation
import com.bhplusplus.yaya.ui.theme.YYATheme

/**
 * ACTIVIDAD PRINCIPAL (PUNTO DE ENTRADA)
 * Configura el entorno de Jetpack Compose y lanza el sistema de navegación global.
 */
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializamos Supabase con persistencia de sesión
        SupabaseManager.initialize(applicationContext)

        // Habilita el diseño de borde a borde (edge-to-edge) para una experiencia moderna
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones en Android 13+
        askNotificationPermission()

        setContent {
            // Aplicamos el Tema Global de la aplicación definido en UI/Theme
            YYATheme {
                // Lanzamos el componente de Navegación que controla todas las pantallas
                AppNavigation()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
