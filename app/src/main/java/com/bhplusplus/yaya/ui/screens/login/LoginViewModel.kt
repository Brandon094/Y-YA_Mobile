package com.bhplusplus.yaya.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

/**
 * LÓGICA DE NEGOCIO PARA EL LOGIN
 * Maneja el estado de la autenticación y la comunicación con Supabase.
 */
class LoginViewModel : ViewModel() {
    
    // Estado de carga para mostrar el Spinner en la UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Mensaje de error para mostrar al usuario si algo falla
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Función principal para iniciar sesión.
     * @param email Correo electrónico ingresado.
     * @param password Contraseña ingresada.
     * @param onResult Callback para avisar a la UI si el login fue exitoso.
     */
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        // Validación básica inicial
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        // Ejecutamos el login en una corrutina (hilo secundario) para no bloquear la app
        viewModelScope.launch {
            try {
                // Llamada oficial a Supabase Auth
                SupabaseManager.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Hito 4: Sincronizar Token de Notificaciones tras login exitoso
                SupabaseManager.syncFcmToken()

                _isLoading.value = false
                onResult(true) // Notificamos éxito
            } catch (e: Exception) {
                _isLoading.value = false
                val errorText = e.message ?: ""
                
                // Mapeo de errores técnicos a mensajes amigables en español
                _errorMessage.value = when {
                    errorText.contains("Invalid login credentials", ignoreCase = true) -> 
                        "Correo o contraseña incorrectos"
                    errorText.contains("Email not confirmed", ignoreCase = true) -> 
                        "Por favor, confirma tu correo electrónico"
                    errorText.contains("network", ignoreCase = true) -> 
                        "Sin conexión a internet"
                    else -> "Error: Verifique sus datos"
                }
                onResult(false)
            }
        }
    }
}
