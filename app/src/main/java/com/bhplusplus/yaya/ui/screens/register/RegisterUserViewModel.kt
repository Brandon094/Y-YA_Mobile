package com.bhplusplus.yaya.ui.screens.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
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

    // ESTADO Y NAVEGACIÓN DEL WIZARD EN 3 PASOS (MVVM)
    var currentStep by mutableIntStateOf(1)
        private set

    fun goToStep(step: Int) {
        if (step in 1..3) {
            currentStep = step
        }
    }

    fun isStep1Valid(name: String, documentId: String, birthDate: String): Boolean {
        return ValidationUtils.isValidName(name) && 
               ValidationUtils.isValidDocumentId(documentId) && 
               ValidationUtils.isValidBirthDate(birthDate)
    }

    fun isStep2Valid(phone: String, address: String, municipality: String): Boolean {
        return ValidationUtils.isValidPhone(phone) && 
               ValidationUtils.isValidAddress(address) && 
               municipality.isNotBlank()
    }

    fun isStep3Valid(email: String, password: String, acceptedTerms: Boolean, acceptedPrivacy: Boolean): Boolean {
        return ValidationUtils.isValidEmail(email) && 
               ValidationUtils.isSecurePassword(password) && 
               acceptedTerms && acceptedPrivacy
    }

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
        onResult: (Boolean, String) -> Unit
    ) {
        // Validaciones rigurosas con ValidationUtils (DRY)
        val nameError = ValidationUtils.getNameError(name)
        if (nameError != null) {
            _errorMessage.value = nameError
            onResult(false, role)
            return
        }

        val docError = ValidationUtils.getDocumentIdError(documentId)
        if (docError != null) {
            _errorMessage.value = docError
            onResult(false, role)
            return
        }

        val phoneError = ValidationUtils.getPhoneError(phone)
        if (phoneError != null) {
            _errorMessage.value = phoneError
            onResult(false, role)
            return
        }

        val emailError = ValidationUtils.getEmailError(email)
        if (emailError != null) {
            _errorMessage.value = emailError
            onResult(false, role)
            return
        }

        val passwordError = ValidationUtils.getPasswordError(password)
        if (passwordError != null) {
            _errorMessage.value = passwordError
            onResult(false, role)
            return
        }

        val addressError = ValidationUtils.getAddressError(address)
        if (addressError != null) {
            _errorMessage.value = addressError
            onResult(false, role)
            return
        }

        val birthDateError = ValidationUtils.getBirthDateError(birthDate)
        if (birthDateError != null) {
            _errorMessage.value = birthDateError
            onResult(false, role)
            return
        }

        if (role.isBlank() || (role != "client" && role != "provider" && role != "admin")) {
            _errorMessage.value = "Por favor selecciona tu rol principal (Cliente o Prestador)"
            onResult(false, role)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Crear el usuario en Supabase Auth y recibir el UserInfo directamente de signUpWith
                val userResponse = SupabaseManager.client.auth.signUpWith(Email) {
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

                // 2. Extraer el ID único (UUID) asignado en auth.users
                val userId = userResponse?.id
                    ?: SupabaseManager.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No se pudo obtener la identidad del usuario en Supabase Auth")

                // 3. Crear e insertar inmediatamente el perfil obligatorio en la tabla pública 'public.profiles'
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
                Log.i("Register", "Perfil insertado con éxito en 'public.profiles' para id=$userId (rol=$role)")

                // 4. Intentar iniciar sesión para dejar la sesión activa en el dispositivo
                try {
                    SupabaseManager.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                } catch (e: Exception) {
                    Log.w("Register", "Autologin posterior diferido: ${e.message}")
                }

                _isLoading.value = false
                _errorMessage.value = null
                onResult(true, role)

            } catch (e: Exception) {
                Log.e("Register", "Error fatal durante el registro: ${e.message}", e)
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("already registered", true) == true -> "Este correo ya está registrado"
                    else -> "Error en el registro: ${e.localizedMessage}"
                }
                onResult(false, role)
            }
        }
    }
}
