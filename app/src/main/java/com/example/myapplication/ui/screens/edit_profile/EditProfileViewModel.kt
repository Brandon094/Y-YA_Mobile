package com.example.myapplication.ui.screens.edit_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import com.example.myapplication.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log

/**
 * LÓGICA PARA EDITAR EL PERFIL
 * Maneja el estado del formulario de edición y el envío de cambios a Supabase.
 */
class EditProfileViewModel : ViewModel() {

    // Estados mutables vinculados a los campos de texto de la pantalla
    var name by mutableStateOf("")
    var phone by mutableStateOf("")

    // Estado para controlar la UI durante la petición al servidor
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Al iniciar, precargamos los datos actuales para que el usuario pueda verlos antes de editar
    init {
        fetchCurrentData()
    }

    /**
     * Trae los datos actuales desde Supabase para llenar el formulario.
     */
    private fun fetchCurrentData() {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    val profile = SupabaseManager.client.postgrest["profiles"]
                        .select {
                            filter {
                                eq("id", user.id)
                            }
                        }
                        .decodeSingle<UserProfile>()
                    
                    name = profile.full_name
                    phone = profile.phone ?: ""
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error fetching data", e)
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Envía los nuevos datos (Nombre y Teléfono) a la base de datos.
     * @param onComplete Callback que se ejecuta cuando la actualización es exitosa.
     */
    fun updateProfile(onComplete: () -> Unit) {
        // Validación de negocio local
        if (name.isBlank()) {
            errorMessage = "El nombre no puede estar vacío"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    // Operación UPDATE en Supabase
                    SupabaseManager.client.postgrest["profiles"].update(
                        {
                            set("full_name", name)
                            set("phone", phone)
                        }
                    ) {
                        filter {
                            eq("id", user.id) // Aseguramos que solo editamos nuestro propio perfil
                        }
                    }
                    onComplete() // Regresamos a la pantalla de perfil
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error updating profile", e)
                errorMessage = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
