package com.bhplusplus.yaya

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.navigation.HomeRoute
import com.bhplusplus.yaya.navigation.WelcomeRoute
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.json.jsonPrimitive

/**
 * VIEWMODEL PRINCIPAL
 * Gestiona el arranque de la aplicación y la decisión de navegación inicial.
 */
class MainViewModel : ViewModel() {

    var isCheckingSession by mutableStateOf(true)
        private set

    // Usamos null inicial para forzar que no haya navegación prematura
    var initialRoute: Any? by mutableStateOf(null)
        private set

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                // Delay de cortesía para el Splash nativo
                delay(1000.milliseconds)

                val session = SupabaseManager.client.auth.currentSessionOrNull()
                if (session != null) {
                    initialRoute = HomeRoute
                } else {
                    initialRoute = WelcomeRoute
                }
            } catch (e: Exception) {
                Log.e("MainVM", "Error validando sesión: ${e.message}")
                val session = SupabaseManager.client.auth.currentSessionOrNull()
                initialRoute = if (session != null) HomeRoute else WelcomeRoute
            } finally {
                // Solo liberamos el Splash cuando la ruta ya está definida
                isCheckingSession = false
            }
        }
    }
}
