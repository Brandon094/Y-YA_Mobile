package com.bhplusplus.yaya.ui.screens.reset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import android.util.Log

/**
 * VIEWMODEL PARA RESTABLECER CONTRASEÑA
 * Maneja el flujo de recuperación de acceso mediante correo electrónico.
 */
class ResetPasswordViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    /**
     * Solicita a Supabase el envío de un enlace de recuperación.
     * @param email Correo del usuario que olvidó la clave.
     */
    fun sendResetPasswordEmail(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Ingresa tu correo electrónico"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Función nativa de Supabase para disparar el flujo de Reset Password
                SupabaseManager.client.auth.resetPasswordForEmail(email)
                _isSuccess.value = true // Cambiamos el estado para mostrar el mensaje de éxito en la UI
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("ResetPassword", "Error: ", e)
                _isLoading.value = false
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Permite al usuario actualizar su contraseña actual.
     * (Usado habitualmente después de entrar con un enlace de recuperación).
     */
    fun updatePassword(newPassword: String, onResult: (Boolean) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                SupabaseManager.client.auth.updateUser {
                    password = newPassword
                }
                _isLoading.value = false
                onResult(true)
            } catch (e: Exception) {
                Log.e("ResetPassword", "Error updating password: ", e)
                _isLoading.value = false
                _errorMessage.value = "Error al actualizar: ${e.localizedMessage}"
                onResult(false)
            }
        }
    }
}
