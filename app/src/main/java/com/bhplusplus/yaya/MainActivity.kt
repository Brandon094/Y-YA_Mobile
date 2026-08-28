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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.navigation.AppNavigation
import com.bhplusplus.yaya.ui.components.molecules.YayaOfflineBanner
import com.bhplusplus.yaya.ui.theme.YYATheme

/**
 * ACTIVIDAD PRINCIPAL (PUNTO DE ENTRADA)
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        
        // Mantenemos el Splash visible hasta que el ViewModel determine la ruta final
        splashScreen.setKeepOnScreenCondition {
            viewModel.isCheckingSession
        }

        SupabaseManager.initialize(applicationContext)
        enableEdgeToEdge()
        askNotificationPermission()

        setContent {
            YYATheme {
                Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    YayaOfflineBanner(isOffline = viewModel.isOffline)
                    
                    // Solo renderizamos la navegación cuando el chequeo de sesión ha terminado
                    // y tenemos una ruta válida. Esto evita el parpadeo de la WelcomeScreen.
                    viewModel.initialRoute?.let { route ->
                        Box(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                            AppNavigation(startRoute = route)
                        }
                    }
                }
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
