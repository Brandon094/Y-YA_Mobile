package com.bhplusplus.yaya

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.navigation.AppNavigation
import com.bhplusplus.yaya.ui.theme.YYATheme

/**
 * ACTIVIDAD PRINCIPAL (PUNTO DE ENTRADA)
 * Configura el entorno de Jetpack Compose y lanza el sistema de navegación global.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalamos el Splash Screen API oficial
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        
        // Mantenemos el Splash visible hasta que el ViewModel determine la ruta inicial
        splashScreen.setKeepOnScreenCondition {
            viewModel.isCheckingSession
        }

        // Inicializamos Supabase
        SupabaseManager.initialize(applicationContext)

        // Habilita el diseño moderno de borde a borde
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones en Android 13+
        askNotificationPermission()

        setContent {
            YYATheme {
                // Pasamos la ruta inicial calculada por el ViewModel
                AppNavigation(startRoute = viewModel.initialRoute)
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
