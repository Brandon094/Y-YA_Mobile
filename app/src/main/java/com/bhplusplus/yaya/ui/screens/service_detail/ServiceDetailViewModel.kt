package com.bhplusplus.yaya.ui.screens.service_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.models.Report
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth
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

    var isReporting by mutableStateOf(false)
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

    /**
     * Envía una denuncia contra el prestador actual.
     */
    fun submitReport(reason: String, onResult: (Boolean) -> Unit) {
        val reportedUserId = provider?.id ?: return
        val reporterId = SupabaseManager.client.auth.currentUserOrNull()?.id ?: return

        viewModelScope.launch {
            isReporting = true
            try {
                val report = Report(
                    reporter_id = reporterId,
                    reported_user_id = reportedUserId,
                    reason = reason
                )
                
                SupabaseManager.client.postgrest["reports"].insert(report)
                onResult(true)
            } catch (e: Exception) {
                Log.e("ServiceDetailVM", "Error al enviar reporte: ${e.message}")
                onResult(false)
            } finally {
                isReporting = false
            }
        }
    }
}
