package com.bhplusplus.yaya.ui.screens.my_services

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Service
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA LA GESTIÓN DE SERVICIOS PROPIOS (VISTA PRESTADOR)
 * Permite al prestador administrar sus publicaciones: listar, activar/pausar y eliminar.
 */
class MyServicesViewModel : ViewModel() {

    var services by mutableStateOf<List<Service>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchMyServices()
    }

    /**
     * Obtiene los servicios publicados por el usuario actual.
     */
    fun fetchMyServices() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val result = SupabaseManager.client.postgrest["services"]
                        .select {
                            filter {
                                eq("provider_id", userId)
                            }
                        }
                        .decodeList<Service>()
                    
                    services = result.sortedByDescending { it.created_at }
                }
            } catch (e: Exception) {
                Log.e("MyServicesVM", "Error fetching services: ${e.message}")
                errorMessage = "No se pudieron cargar tus servicios."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Cambia el estado del servicio entre 'active' e 'inactive'.
     */
    fun toggleServiceStatus(serviceId: String, currentStatus: String) {
        val newStatus = if (currentStatus == "active") "inactive" else "active"
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["services"].update({
                    set("status", newStatus)
                }) {
                    filter { eq("id", serviceId) }
                }
                fetchMyServices() // Refrescamos la lista
            } catch (e: Exception) {
                Log.e("MyServicesVM", "Error toggling status: ${e.message}")
            }
        }
    }

    /**
     * Elimina permanentemente un servicio.
     */
    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["services"].delete {
                    filter { eq("id", serviceId) }
                }
                fetchMyServices()
            } catch (e: Exception) {
                Log.e("MyServicesVM", "Error deleting service: ${e.message}")
            }
        }
    }
}
