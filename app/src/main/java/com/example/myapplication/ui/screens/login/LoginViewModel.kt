package com.example.myapplication.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginViewModel : ViewModel() {
    val TAG = "LoginScreen"
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                SupabaseManager.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                _isLoading.value = false
                onResult(true)
            } catch (e: Exception) {
                _isLoading.value = false
                val errorText = e.message ?: ""
                
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
