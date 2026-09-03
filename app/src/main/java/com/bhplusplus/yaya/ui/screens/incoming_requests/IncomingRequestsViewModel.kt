package com.bhplusplus.yaya.ui.screens.incoming_requests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.ServiceRequest
import com.bhplusplus.yaya.utils.FormatterUtils
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Modelo de UI para una solicitud recibida por el prestador.
 * Centraliza el formateo para mantener la View "tonta".
 */
data class IncomingRequestUiState(
    val domain: ServiceRequest,
    val clientName: String,
    val serviceTitle: String,
    val status: String,
    val address: String,
    val formattedPrice: String,
    val formattedDate: String,
    val description: String,
    val isClientOfferPending: Boolean,
    val clientAvatarUrl: String?
)

/**
 * VIEWMODEL PARA SOLICITUDES ENTRANTES (VISTA PRESTADOR)
 * Filtra las solicitudes de la tabla 'requests' que pertenecen a los servicios del prestador actual.
 */
class IncomingRequestsViewModel : ViewModel() {

    var requests by mutableStateOf<List<IncomingRequestUiState>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchIncomingRequests()
    }

    /**
     * Obtiene las solicitudes uniendo con 'services' para filtrar por el prestador actual
     * y con 'profiles' para obtener los datos del cliente.
     */
    fun fetchIncomingRequests() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    // Consulta compleja con Joins:
                    // 1. Traemos todo de 'requests' (*)
                    // 2. Unimos con 'services' (!inner asegura el filtro)
                    // 3. Unimos con 'profiles' (renombrado a client por client_id)
                    val result = SupabaseManager.client.postgrest["requests"]
                        .select(Columns.raw("*, services!inner(*), profiles:client_id(*)")) {
                            filter {
                                eq("services.provider_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                    
                    val sorted = result.sortedByDescending { it.created_at }
                    requests = sorted.map { mapToUiState(it) }

                    // Activar suscripción Realtime para solicitudes
                    subscribeToIncomingRequests(userId)
                }
            } catch (e: Exception) {
                Log.e("IncomingReqVM", "Error fetching requests: ${e.message}")
                errorMessage = "No se pudieron cargar las solicitudes."
            } finally {
                isLoading = false
            }
        }
    }

    private fun mapToUiState(request: ServiceRequest): IncomingRequestUiState {
        val desc = request.request_description ?: ""
        
        // Turno del prestador si el cliente hizo la última oferta (inicial o contraoferta)
        val lastIsClient = desc.contains("Oferta inicial") || desc.contains("Nueva oferta Cliente")
        val lastIsProvider = desc.contains("Contraoferta Prestador")
        
        // canAccept: Es pending y el último mensaje fue del cliente (y no hay contraoferta posterior del prestador)
        val canAccept = request.status == "pending" && 
                        (desc.lastIndexOf("Cliente") > desc.lastIndexOf("Prestador") || 
                        (lastIsClient && !lastIsProvider))

        return IncomingRequestUiState(
            domain = request,
            clientName = request.client?.full_name ?: "Cliente",
            serviceTitle = request.services?.title ?: "Servicio Solicitado",
            status = request.status,
            address = request.service_address,
            formattedPrice = FormatterUtils.formatCurrency(request.final_price),
            formattedDate = FormatterUtils.formatDate(request.scheduled_date),
            description = desc,
            isClientOfferPending = canAccept,
            clientAvatarUrl = request.client?.avatar_url
        )
    }

    /**
     * Se suscribe a cambios en tiempo real en la tabla 'requests'.
     */
    private fun subscribeToIncomingRequests(userId: String) {
        val channel = SupabaseManager.client.channel("incoming_requests_realtime")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "requests"
            }.onEach {
                // Refrescamos la lista completa ante cualquier cambio (Insert, Update, Delete)
                // Esto asegura que los Joins (servicios, perfiles) se mantengan actualizados.
                fetchIncomingRequestsSilently()
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("IncomingReqVM", "Error subscribing: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("IncomingReqVM", "Error setting up postgresChangeFlow: ${e.message}")
        }
    }

    /**
     * Carga las solicitudes sin activar el estado isLoading para no interrumpir la UX.
     */
    private fun fetchIncomingRequestsSilently() {
        viewModelScope.launch {
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val result = SupabaseManager.client.postgrest["requests"]
                        .select(Columns.raw("*, services!inner(*), profiles:client_id(*)")) {
                            filter {
                                eq("services.provider_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                    
                    val sorted = result.sortedByDescending { it.created_at }
                    requests = sorted.map { mapToUiState(it) }
                }
            } catch (e: Exception) {
                Log.e("IncomingReqVM", "Error silent fetch: ${e.message}")
            }
        }
    }

    /**
     * Determina si hay una oferta pendiente por parte del cliente que el prestador deba revisar.
     */
    fun isClientOfferPending(request: ServiceRequest): Boolean {
        return request.status == "pending" && 
               request.request_description?.contains("Nueva oferta Cliente") == true
    }

    /**
     * Formatea la fecha para visualización simple (YYYY-MM-DD).
     */
    fun formatDate(date: String?): String {
        return date?.take(10) ?: "Fecha no definida"
    }

    /**
     * Envía una contraoferta (nuevo precio) al cliente.
     */
    fun sendCounterOffer(request: ServiceRequest, newPrice: String) {
        viewModelScope.launch {
            try {
                val priceVal = newPrice.toDoubleOrNull() ?: 0.0
                val formattedPrice = "$${priceVal.toInt()}"
                val updatedDescription = "${request.request_description}\n🚩 Contraoferta Prestador: $formattedPrice"
                
                SupabaseManager.client.postgrest["requests"].update({
                    set("request_description", updatedDescription)
                    set("final_price", priceVal)
                    set("status", "pending") // Aseguramos que siga pendiente
                }) {
                    filter { eq("id", request.id!!) }
                }
                fetchIncomingRequests()
            } catch (e: Exception) {
                Log.e("IncomingReqVM", "Error en contraoferta: ${e.message}")
            }
        }
    }

    /**
     * Actualiza el estado de una solicitud (Aceptar/Rechazar).
     */
    fun updateRequestStatus(requestId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["requests"].update({
                    set("status", newStatus)
                }) {
                    filter { eq("id", requestId) }
                }
                // Refrescamos la lista localmente
                fetchIncomingRequests()
            } catch (e: Exception) {
                Log.e("IncomingReqVM", "Error updating status: ${e.message}")
                errorMessage = "No se pudo actualizar el estado."
            }
        }
    }
}
