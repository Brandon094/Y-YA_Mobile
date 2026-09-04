package com.bhplusplus.yaya.ui.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.utils.ValidationUtils
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
        municipality: String = "La Plata",
        onResult: (Boolean) -> Unit
    ) {
        // Validaciones rigurosas con ValidationUtils (DRY)
        val nameError = ValidationUtils.getNameError(name)
        if (nameError != null) {
            _errorMessage.value = nameError
            onResult(false)
            return
        }

        val docError = ValidationUtils.getDocumentIdError(documentId)
        if (docError != null) {
            _errorMessage.value = docError
            onResult(false)
            return
        }

        val phoneError = ValidationUtils.getPhoneError(phone)
        if (phoneError != null) {
            _errorMessage.value = phoneError
            onResult(false)
            return
        }

        val emailError = ValidationUtils.getEmailError(email)
        if (emailError != null) {
            _errorMessage.value = emailError
            onResult(false)
            return
        }

        val passwordError = ValidationUtils.getPasswordError(password)
        if (passwordError != null) {
            _errorMessage.value = passwordError
            onResult(false)
            return
        }

        val addressError = ValidationUtils.getAddressError(address)
        if (addressError != null) {
            _errorMessage.value = addressError
            onResult(false)
            return
        }

        val birthDateError = ValidationUtils.getBirthDateError(birthDate)
        if (birthDateError != null) {
            _errorMessage.value = birthDateError
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Crear el usuario en Supabase Auth con metadatos completos
                SupabaseManager.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("full_name", name.trim())
                        put("role", role)
                        put("phone", phone.trim())
                        put("address", address.trim())
                        put("municipality", municipality.ifBlank { "La Plata" })
                    }
                }

                // 2. Iniciar sesión explícitamente para establecer la sesión activa y obtener el ID
                try {
                    SupabaseManager.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                } catch (e: Exception) {
                    Log.w("Register", "Autologin inmediato diferido: ${e.message}")
                }

                // 3. Obtener el ID del usuario autenticado
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                    ?: SupabaseManager.client.auth.currentSessionOrNull()?.user?.id

                if (userId != null) {
                    // 4. Crear obligatoriamente el perfil en la tabla 'public.profiles'
                    val newProfile = UserProfile(
                        id = userId,
                        full_name = name.trim(),
                        phone = phone.ifBlank { null },
                        document_id = documentId.ifBlank { null },
                        birth_date = birthDate.ifBlank { null },
                        address = address.ifBlank { null },
                        municipality = municipality.ifBlank { "La Plata" },
                        role = role
                    )

                    SupabaseManager.client.postgrest["profiles"].upsert(newProfile)
                    Log.i("Register", "Perfil insertado con éxito en 'public.profiles' para id=$userId")

                    _isLoading.value = false
                    _errorMessage.value = null
                    onResult(true)
                } else {
                    Log.w("Register", "Usuario creado en Auth. Esperando confirmación de email.")
                    _isLoading.value = false
                    _errorMessage.value = "Cuenta creada. Revisa tu correo electrónico para confirmar e iniciar sesión."
                    onResult(true)
                }

            } catch (e: Exception) {
                Log.e("Register", "Error fatal durante el registro: ${e.message}", e)
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("already registered", true) == true -> "Este correo ya está registrado"
                    else -> "Error en el registro: ${e.localizedMessage}"
                }
                onResult(false)
            }
        }
    }
}
