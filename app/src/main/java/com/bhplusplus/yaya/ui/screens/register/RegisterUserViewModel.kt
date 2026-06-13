package com.bhplusplus.yaya.ui.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA EL REGISTRO DE USUARIOS
 * Gestiona la creación de la cuenta de autenticación y el perfil detallado del usuario.
 */
class RegisterUserViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Registra un nuevo usuario con todos los datos requeridos por el modelo de negocio.
     */
    fun register(
        name: String, 
        email: String, 
        password: String, 
        phone: String, 
        address: String,
        documentId: String,
        birthDate: String,
        role: String,
        onResult: (Boolean) -> Unit
    ) {
        // Validación de campos obligatorios
        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() || 
            address.isBlank() || documentId.isBlank() || birthDate.isBlank()) {
            _errorMessage.value = "Por favor, completa todos los campos"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Crear el usuario en Supabase Auth
                SupabaseManager.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                // 2. Obtener el ID del usuario creado
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id

                if (userId != null) {
                    // 3. Crear perfil completo
                    val newProfile = UserProfile(
                        id = userId,
                        full_name = name,
                        phone = phone,
                        document_id = documentId,
                        birth_date = birthDate,
                        address = address,
                        role = role
                    )

                    // 4. Guardar en la tabla 'profiles'
                    SupabaseManager.client.postgrest["profiles"].insert(newProfile)
                    
                    _isLoading.value = false
                    onResult(true)
                } else {
                    throw Exception("No se pudo obtener el ID de usuario.")
                }

            } catch (e: Exception) {
                Log.e("Register", "Error: ", e)
                _isLoading.value = false
                _errorMessage.value = "Error: ${e.localizedMessage}"
                onResult(false)
            }
        }
    }
}
