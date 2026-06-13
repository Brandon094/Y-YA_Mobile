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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
                // Pasamos Metadata para que el perfil sea recuperable incluso si falla el insert en 'profiles'
                SupabaseManager.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("full_name", name)
                        put("role", role)
                    }
                }
                
                // 2. Obtener el ID del usuario. 
                // IMPORTANTE: En supabase-kt, después de signUpWith, podemos intentar obtener el ID del usuario
                // incluso si la sesión no se ha iniciado (ej. si requiere confirmación).
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                
                if (userId != null) {
                    // 3. Intentar crear el perfil detallado en la tabla pública
                    try {
                        val newProfile = UserProfile(
                            id = userId,
                            full_name = name,
                            phone = phone,
                            document_id = documentId,
                            birth_date = birthDate,
                            address = address,
                            role = role
                        )

                        // Usamos Upsert para mayor robustez
                        SupabaseManager.client.postgrest["profiles"].upsert(newProfile)
                        
                        Log.d("Register", "Perfil creado exitosamente.")
                        _isLoading.value = false
                        onResult(true)
                    } catch (e: Exception) {
                        Log.e("Register", "Error al insertar en profiles: ${e.message}")
                        // Si falla aquí, pero el usuario ya existe en Auth, notificamos éxito parcial
                        _isLoading.value = false
                        onResult(true) 
                    }
                } else {
                    // Si no hay ID, informamos sobre la confirmación de email necesaria
                    Log.i("Register", "Usuario creado. Esperando confirmación de email.")
                    _isLoading.value = false
                    _errorMessage.value = "Cuenta creada. Revisa tu correo para confirmar y activar tu perfil."
                    onResult(true)
                }

            } catch (e: Exception) {
                Log.e("Register", "Error fatal: ${e.message}", e)
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("already registered", true) == true -> "Este correo ya está registrado"
                    else -> "Error: ${e.localizedMessage}"
                }
                onResult(false)
            }
        }
    }
}
