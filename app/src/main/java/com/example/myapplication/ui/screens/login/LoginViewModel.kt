package com.example.myapplication.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------- ViewModel ----------
class LoginViewModel : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            _errorMessage.value = "Completa todos los campos"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            delay(1500)

            val success = username == "test@example.com" && password == "123456"

            _isLoading.value = false
            if (success) {
                onResult(true)
            } else {
                _errorMessage.value = "Usuario o contraseña incorrectos"
                onResult(false)
            }
        }
    }
}