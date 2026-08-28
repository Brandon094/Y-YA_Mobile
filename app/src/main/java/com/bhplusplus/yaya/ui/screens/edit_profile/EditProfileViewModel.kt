package com.bhplusplus.yaya.ui.screens.edit_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import android.util.Log

/**
 * LÓGICA PARA EDITAR EL PERFIL
 * Maneja el estado del formulario de edición y el envío de cambios a Supabase.
 */
class EditProfileViewModel : ViewModel() {

    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var documentId by mutableStateOf("")
    var birthDate by mutableStateOf("")
    var address by mutableStateOf("")
    var avatarUrl by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchCurrentData()
    }

    private fun fetchCurrentData() {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    val profile = SupabaseManager.client.postgrest["profiles"]
                        .select { filter { eq("id", user.id) } }
                        .decodeSingle<UserProfile>()
                    
                    name = profile.full_name
                    phone = profile.phone ?: ""
                    documentId = profile.document_id ?: ""
                    birthDate = profile.birth_date ?: ""
                    address = profile.address ?: ""
                    avatarUrl = profile.avatar_url
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error al cargar datos actuales: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadAvatar(byteArray: ByteArray) {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull() ?: return@launch
                val fileName = "${user.id}_avatar.jpg"
                avatarUrl = SupabaseManager.uploadImage("avatars", fileName, byteArray)
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error al subir avatar: ${e.message}")
                errorMessage = "No se pudo subir la foto."
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProfile(onComplete: () -> Unit) {
        if (name.isBlank()) {
            errorMessage = "El nombre es obligatorio"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    SupabaseManager.client.postgrest["profiles"].update(
                        {
                            set("full_name", name)
                            set("phone", phone)
                            set("document_id", documentId)
                            set("birth_date", if(birthDate.isBlank()) null else birthDate)
                            set("address", address)
                            set("avatar_url", avatarUrl)
                        }
                    ) {
                        filter { eq("id", user.id) }
                    }
                    onComplete()
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error al actualizar perfil: ${e.message}")
                errorMessage = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
