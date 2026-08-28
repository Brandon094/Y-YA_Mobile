package com.bhplusplus.yaya.ui.screens.my_services

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.utils.FormatterUtils
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import java.text.NumberFormat
import java.util.Locale

/**
 * Modelo de UI para un servicio en la gestión del prestador.
 */
data class MyServiceUiState(
    val domain: Service,
    val title: String,
    val formattedPrice: String,
    val description: String,
    val statusLabel: String,
    val statusColor: Long, // Color Hex
    val isActive: Boolean,
    val isPending: Boolean
)

/**
 * VIEWMODEL PARA LA GESTIÓN DE SERVICIOS PROPIOS (VISTA PRESTADOR)
 * Permite al prestador administrar sus publicaciones: listar, activar/pausar y eliminar.
 */
class MyServicesViewModel : ViewModel() {

    var services by mutableStateOf<List<MyServiceUiState>>(emptyList())
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
                    
                    val sorted = result.sortedByDescending { it.created_at }
                    services = sorted.map { mapToUiState(it) }
                    
                    // Activar suscripción Realtime para mis servicios
                    subscribeToMyServices(userId)
                }
            } catch (e: Exception) {
                Log.e("MyServicesVM", "Error fetching services: ${e.message}")
                errorMessage = "No se pudieron cargar tus servicios."
            } finally {
                isLoading = false
            }
        }
    }

    private fun mapToUiState(service: Service): MyServiceUiState {
        val isPending = service.status == "pending_approval"
        return MyServiceUiState(
            domain = service,
            title = service.title,
            formattedPrice = FormatterUtils.formatCurrency(service.price),
            description = service.description,
            statusLabel = when (service.status) {
                "active" -> "Activo"
                "pending_approval" -> "En Revisión"
                else -> "Pausado"
            },
            statusColor = when (service.status) {
                "active" -> 0xFF4CAF50
                "pending_approval" -> 0xFFFF9800
                else -> 0xFF9E9E9E
            },
            isActive = service.status == "active",
            isPending = isPending
        )
    }

    /**
     * Se suscribe a cambios en tiempo real en la tabla 'services' filtrando por mis servicios.
     */
    private fun subscribeToMyServices(userId: String) {
        val channel = SupabaseManager.client.channel("my_services_realtime")
        
        channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "services"
        }.onEach { action ->
            when (action) {
                is PostgresAction.Insert -> {
                    val newService = action.decodeRecord<Service>()
                    if (newService.provider_id == userId) {
                        val currentList = services.map { it.domain }
                        val updatedList = (listOf(newService) + currentList).sortedByDescending { it.created_at }
                        services = updatedList.map { mapToUiState(it) }
                    }
                }
                is PostgresAction.Update -> {
                    val updatedService = action.decodeRecord<Service>()
                    if (updatedService.provider_id == userId) {
                        val currentList = services.map { it.domain }
                        val updatedList = currentList.map { if (it.id == updatedService.id) updatedService else it }
                                                     .sortedByDescending { it.created_at }
                        services = updatedList.map { mapToUiState(it) }
                    }
                }
                is PostgresAction.Delete -> {
                    val deletedId = action.oldRecord["id"]?.jsonPrimitive?.content
                    services = services.filter { it.domain.id != deletedId }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                channel.subscribe()
            } catch (e: Exception) {
                Log.e("MyServicesVM", "Error subscribing: ${e.message}")
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
                // El suscriptor Realtime actualizará la lista
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
                // El suscriptor Realtime actualizará la lista
            } catch (e: Exception) {
                Log.e("MyServicesVM", "Error deleting service: ${e.message}")
            }
        }
    }
}
