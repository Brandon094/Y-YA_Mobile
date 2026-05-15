package com.example.myapplication.ui.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA EL REGISTRO DE USUARIOS
 * Gestiona el proceso de creación de nuevas cuentas mediante Supabase Auth.
 */
class RegisterUserViewModel : ViewModel() {

    // Estado para controlar el progreso visual (Spinner)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Estado para manejar mensajes de error durante el registro
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Registra un nuevo usuario con Email y Contraseña.
     * @param email Correo electrónico.
     * @param password Contraseña elegida.
     * @param onResult Callback para retornar el éxito o fallo a la vista.
     */
    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        // Validación local preventiva
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Llamada a la API de Supabase para registro de usuarios
                SupabaseManager.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                _isLoading.value = false
                onResult(true) // Registro exitoso
            } catch (e: Exception) {
                // Registro de error en consola para depuración técnica
                Log.e("Register", "Error completo: ", e)
                _isLoading.value = false
                _errorMessage.value = "Error al registrar: ${e.message}"
                onResult(false)
            }
        }
    }
}
