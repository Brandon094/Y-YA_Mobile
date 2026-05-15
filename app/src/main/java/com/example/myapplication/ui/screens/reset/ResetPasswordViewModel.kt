package com.example.myapplication.ui.screens.reset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import android.util.Log

class ResetPasswordViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun sendResetPasswordEmail(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "Ingresa tu correo electrónico"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                SupabaseManager.client.auth.resetPasswordForEmail(email)
                _isSuccess.value = true
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("ResetPassword", "Error: ", e)
                _isLoading.value = false
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

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
