package com.bhplusplus.yaya.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.utils.ValidationUtils
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

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
        val emailError = ValidationUtils.getEmailError(email)
        if (emailError != null) {
            _errorMessage.value = emailError
            onResult(false)
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "Por favor, ingresa tu contraseña"
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

                // Asegurar la existencia del perfil en 'public.profiles' tras login
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    ensureProfileExists(user)
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

    private suspend fun ensureProfileExists(user: UserInfo) {
        try {
            val count = SupabaseManager.client.postgrest["profiles"]
                .select { filter { eq("id", user.id) } }
                .decodeList<UserProfile>().size

            if (count == 0) {
                android.util.Log.w("LoginVM", "Perfil no encontrado en 'public.profiles' para id=${user.id}. Creando automáticamente...")
                val fullName = user.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "Usuario YÁYA"
                val role = user.userMetadata?.get("role")?.jsonPrimitive?.content ?: "client"
                val phone = user.userMetadata?.get("phone")?.jsonPrimitive?.content
                val address = user.userMetadata?.get("address")?.jsonPrimitive?.content
                val municipality = user.userMetadata?.get("municipality")?.jsonPrimitive?.content ?: "La Plata"

                val newProfile = UserProfile(
                    id = user.id,
                    full_name = fullName,
                    role = role,
                    phone = phone,
                    address = address,
                    municipality = municipality
                )

                SupabaseManager.client.postgrest["profiles"].upsert(newProfile)
                android.util.Log.i("LoginVM", "Perfil sincronizado con éxito en 'public.profiles' para id=${user.id}")
            }
        } catch (e: Exception) {
            android.util.Log.e("LoginVM", "Error al verificar/crear perfil en 'public.profiles': ${e.message}")
        }
    }
}
