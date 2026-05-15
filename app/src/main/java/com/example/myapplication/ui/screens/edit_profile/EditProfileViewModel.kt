package com.example.myapplication.ui.screens.edit_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SupabaseManager
import com.example.myapplication.ui.screens.profile.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log

class EditProfileViewModel : ViewModel() {

    var name by mutableStateOf("")
    var phone by mutableStateOf("")

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

    fun updateProfile(onComplete: () -> Unit) {
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
                    SupabaseManager.client.postgrest["profiles"].update(
                        {
                            set("full_name", name)
                            set("phone", phone)
                        }
                    ) {
                        filter {
                            eq("id", user.id)
                        }
                    }
                    onComplete()
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
