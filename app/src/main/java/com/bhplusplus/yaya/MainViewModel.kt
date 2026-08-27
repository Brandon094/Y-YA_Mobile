package com.bhplusplus.yaya

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.navigation.HomeRoute
import com.bhplusplus.yaya.navigation.WelcomeRoute
import com.bhplusplus.yaya.utils.network.ConnectivityObserver
import com.bhplusplus.yaya.utils.network.NetworkConnectivityObserver
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * VIEWMODEL PRINCIPAL
 * Gestiona el arranque de la aplicación y la decisión de navegación inicial.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val connectivityObserver = NetworkConnectivityObserver(application)

    var isCheckingSession by mutableStateOf(true)
        private set

    var isOffline by mutableStateOf(false)
        private set

    // Usamos null inicial para forzar que no haya navegación prematura
    var initialRoute: Any? by mutableStateOf(null)
        private set

    init {
        checkSession()
        observeNetwork()
    }

    private fun observeNetwork() {
        connectivityObserver.observe().onEach { status ->
            isOffline = status != ConnectivityObserver.Status.Available
        }.launchIn(viewModelScope)
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
