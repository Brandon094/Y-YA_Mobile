package com.bhplusplus.yaya.ui.screens.service_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log

class ServiceDetailViewModel : ViewModel() {
    var service by mutableStateOf<Service?>(null)
        private set
    
    var provider by mutableStateOf<UserProfile?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchServiceById(serviceId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Obtener los detalles del servicio
                val serviceResult = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("id", serviceId) } }
                    .decodeSingle<Service>()
                
                service = serviceResult

                // 2. Obtener la información del prestador (perfil)
                serviceResult.provider_id?.let { providerId ->
                    val providerResult = SupabaseManager.client.postgrest["profiles"]
                        .select { filter { eq("id", providerId) } }
                        .decodeSingle<UserProfile>()
                    provider = providerResult
                }
                
            } catch (e: Exception) {
                Log.e("ServiceDetailVM", "Error: ${e.message}")
                errorMessage = "No se pudo cargar la información completa del servicio."
            } finally {
                isLoading = false
            }
        }
    }
}
