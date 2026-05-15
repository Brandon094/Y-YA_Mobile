package com.example.myapplication.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val full_name: String,
    val phone: String? = null
)

class ProfileViewModel : ViewModel() {

    var name by mutableStateOf("Cargando...")
        private set

    var email by mutableStateOf("Cargando...")
        private set

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    email = user.email ?: ""
                    
                    // Consultamos el nombre real de la tabla 'profiles'
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
                // Si falla, al menos mostramos el email
                if (email == "Cargando...") email = "Usuario"
            }
        }
    }
}
