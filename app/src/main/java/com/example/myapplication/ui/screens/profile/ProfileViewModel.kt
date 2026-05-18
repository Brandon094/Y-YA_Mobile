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
import kotlinx.serialization.Serializable

/**
 * LÓGICA DE NEGOCIO PARA EL PERFIL
 * Combina datos de Auth (Email) y de la tabla Profiles (Nombre/Teléfono).
 */
class ProfileViewModel : ViewModel() {

    // Estados reactivos que la pantalla de Perfil observa para actualizarse
    var name by mutableStateOf("Cargando...")
        private set

    var email by mutableStateOf("Cargando...")
        private set

    // Cargamos los datos en cuanto se abre la pantalla de Perfil
    init {
        fetchProfile()
    }

    /**
     * Obtiene la información del usuario logueado.
     */
    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                // Obtenemos el usuario actualmente autenticado
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    email = user.email ?: ""
                    
                    // Consultamos el registro correspondiente en la tabla personalizada 'profiles'
                    // Usamos un filtro para traer solo el que coincida con el ID del usuario
                    val profile = SupabaseManager.client.postgrest["profiles"]
                        .select {
                            filter {
                                eq("id", user.id)
                            }
                        }
                        .decodeSingle<UserProfile>()
                    
                    name = profile.full_name
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error fetching profile", e)
                // Fallback en caso de error para que la UI no quede vacía
                if (email == "Cargando...") email = "Usuario"
            }
        }
    }
}
