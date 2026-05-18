package com.example.myapplication.ui.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import com.example.myapplication.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA EL REGISTRO DE USUARIOS
 * Gestiona la creación de credenciales y la inicialización del perfil en la base de datos.
 */
class RegisterUserViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Registra un nuevo usuario.
     * 1. Crea la cuenta en Auth.
     * 2. Crea el perfil en la tabla 'profiles' con el nombre proporcionado.
     */
    fun register(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // PASO 1: Registro en Supabase Auth
                val authResult = SupabaseManager.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                // Obtenemos el ID del usuario recién creado
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id

                if (userId != null) {
                    // PASO 2: Crear el perfil usando nuestro modelo UserProfile
                    val newProfile = UserProfile(
                        id = userId,
                        full_name = name,
                        role = "client" // Por defecto se registra como cliente
                    )

                    SupabaseManager.client.postgrest["profiles"].insert(newProfile)
                    
                    _isLoading.value = false
                    onResult(true)
                } else {
                    throw Exception("No se pudo obtener el ID del usuario")
                }

            } catch (e: Exception) {
                Log.e("Register", "Error completo: ", e)
                _isLoading.value = false
                _errorMessage.value = "Error al registrar: ${e.localizedMessage}"
                onResult(false)
            }
        }
    }
}
