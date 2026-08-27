package com.bhplusplus.yaya.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

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

    var isDeletingAccount by mutableStateOf(false)
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
                    
                    // 1. Intentar traer el registro de la tabla profiles
                    try {
                        val profile = SupabaseManager.client.postgrest["profiles"]
                            .select {
                                filter { eq("id", user.id) }
                            }
                            .decodeSingle<UserProfile>()
                        
                        userProfile = profile
                        Log.d("ProfileVM", "Perfil cargado exitosamente: ${profile.full_name}")
                    } catch (e: Exception) {
                        Log.e("ProfileVM", "Error al leer tabla profiles: ${e.message}")
                        
                        // 2. Si no existe en la tabla, intentamos recuperar de Metadata
                        val metadata = user.userMetadata
                        if (metadata != null) {
                            val name = metadata["full_name"]?.jsonPrimitive?.content ?: "Usuario"
                            val role = metadata["role"]?.jsonPrimitive?.content ?: "client"
                            
                            userProfile = UserProfile(
                                id = user.id,
                                full_name = name,
                                role = role,
                                phone = "",
                                address = "",
                                document_id = "",
                                birth_date = ""
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error crítico al obtener perfil: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Inicia el proceso de borrado de cuenta.
     * Nota: En Supabase, el borrado de usuario desde el cliente suele requerir una Edge Function.
     * Por ahora, eliminamos el registro del perfil y cerramos sesión para cumplir el flujo de Google.
     */
    fun deleteAccount(onComplete: () -> Unit) {
        val userId = userProfile?.id ?: return
        
        viewModelScope.launch {
            isDeletingAccount = true
            try {
                // 1. Eliminar datos del perfil en public.profiles
                SupabaseManager.client.postgrest["profiles"].delete {
                    filter { eq("id", userId) }
                }
                
                // 2. Cerrar sesión (La eliminación real de auth.users se maneja por consola o Edge Function Admin)
                SupabaseManager.client.auth.signOut()
                
                onComplete()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error al procesar borrado: ${e.message}")
            } finally {
                isDeletingAccount = false
            }
        }
    }
}
