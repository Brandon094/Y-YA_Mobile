package com.example.myapplication.ui.screens.profile

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
 * LÓGICA DE NEGOCIO PARA EL PERFIL
 * Consulta la información completa del usuario desde Auth y la tabla 'profiles'.
 */
class ProfileViewModel : ViewModel() {

    // El perfil completo del usuario
    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    var email by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchProfile()
    }

    /**
     * Obtiene los datos del servidor.
     */
    fun fetchProfile() {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    email = user.email ?: ""
                    
                    // Traemos el registro completo de la tabla profiles
                    val profile = SupabaseManager.client.postgrest["profiles"]
                        .select {
                            filter { eq("id", user.id) }
                        }
                        .decodeSingle<UserProfile>()
                    
                    userProfile = profile
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error al obtener perfil: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
