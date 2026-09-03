package com.bhplusplus.yaya.ui.screens.my_orders

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.ServiceRequest
import com.bhplusplus.yaya.data.models.Rating
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
 * Modelo de UI para un pedido realizado por el cliente.
 * Centraliza el formateo para mantener la View "tonta".
 */
data class MyOrderUiState(
    val domain: ServiceRequest,
    val serviceTitle: String,
    val providerName: String,
    val status: String,
    val address: String,
    val formattedPrice: String,
    val formattedDate: String,
    val description: String,
    val isCounterOfferActive: Boolean,
    val providerAvatarUrl: String?,
    val isRated: Boolean = false,
    val ratingScore: Int? = null,
    val ratingComment: String? = null
)

/**
 * VIEWMODEL PARA LA PANTALLA DE MIS PEDIDOS
 * Recupera el historial de solicitudes y permite negociar contraofertas.
 */
class MyOrdersViewModel : ViewModel() {

    var orders by mutableStateOf<List<MyOrderUiState>>(emptyList())
        private set

    private var clientRatings = emptyList<Rating>()

    var isLoading by mutableStateOf(false)
        private set

    var isSubmittingRating by mutableStateOf(false)
        private set

    init {
        fetchMyOrders()
    }

    /**
     * Consulta la tabla 'requests' uniendo con 'services' para ver títulos.
     */
    fun fetchMyOrders() {
        viewModelScope.launch {
            isLoading = true
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    // 1. Obtener pedidos
                    val result = SupabaseManager.client.postgrest["requests"]
                        .select(Columns.raw("*, services(*, provider_profile:provider_id(*))")) {
                            filter {
                                eq("client_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                    
                    // 2. Obtener calificaciones ya hechas por el cliente para estos pedidos
                    clientRatings = SupabaseManager.client.postgrest["ratings"]
                        .select { filter { eq("client_id", userId) } }
                        .decodeList<Rating>()

                    val sorted = result.sortedByDescending { it.created_at }
                    orders = sorted.map { mapToUiState(it) }

                    // Activar suscripción Realtime para pedidos
                    subscribeToMyOrders(userId)
                }
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al cargar pedidos: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    private fun mapToUiState(order: ServiceRequest): MyOrderUiState {
        val existingRating = clientRatings.find { it.request_id == order.id }
        val desc = order.request_description ?: ""
        
        // Turno del cliente si el prestador hizo la última contraoferta
        val canAccept = order.status == "pending" && desc.contains("Contraoferta Prestador")

        return MyOrderUiState(
            domain = order,
            serviceTitle = order.services?.title ?: "Servicio",
            providerName = order.services?.provider?.full_name ?: "Prestador Independiente",
            status = order.status,
            address = order.service_address,
            formattedPrice = FormatterUtils.formatCurrency(order.final_price),
            formattedDate = FormatterUtils.formatDate(order.scheduled_date),
            description = desc,
            isCounterOfferActive = canAccept,
            providerAvatarUrl = order.services?.provider?.avatar_url,
            isRated = existingRating != null,
            ratingScore = existingRating?.score,
            ratingComment = existingRating?.comment
        )
    }

    /**
     * Se suscribe a cambios en tiempo real en la tabla 'requests'.
     */
    private fun subscribeToMyOrders(userId: String) {
        val channel = SupabaseManager.client.channel("my_orders_realtime")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "requests"
            }.onEach {
                // Refrescamos silenciosamente para mantener Joins actualizados
                fetchMyOrdersSilently()
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("MyOrdersVM", "Error subscribing: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("MyOrdersVM", "Error setting up postgresChangeFlow: ${e.message}")
        }
    }

    /**
     * Carga los pedidos sin activar el estado isLoading.
     */
    private fun fetchMyOrdersSilently() {
        viewModelScope.launch {
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val result = SupabaseManager.client.postgrest["requests"]
                        .select(Columns.raw("*, services(*, provider_profile:provider_id(*))")) {
                            filter {
                                eq("client_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                    
                    clientRatings = SupabaseManager.client.postgrest["ratings"]
                        .select { filter { eq("client_id", userId) } }
                        .decodeList<Rating>()

                    val sorted = result.sortedByDescending { it.created_at }
                    orders = sorted.map { mapToUiState(it) }
                }
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error silent fetch: ${e.message}")
            }
        }
    }

    /**
     * Determina si hay una contraoferta activa por parte del prestador que el cliente deba revisar.
     */
    fun isCounterOfferActive(request: ServiceRequest): Boolean {
        return request.status == "pending" && 
               request.request_description?.contains("Contraoferta Prestador") == true
    }

    /**
     * Formatea la fecha para visualización simple (YYYY-MM-DD).
     */
    fun formatDate(date: String?): String {
        return date?.take(10) ?: "Fecha no definida"
    }

    /**
     * El cliente acepta la propuesta del prestador (Cambia estado a accepted).
     */
    fun acceptProposal(requestId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["requests"].update({
                    set("status", "accepted")
                }) {
                    filter { eq("id", requestId) }
                }
                fetchMyOrders()
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al aceptar: ${e.message}")
            }
        }
    }

    /**
     * El cliente confirma el trato final (Mueve de 'accepted' a 'in_progress').
     */
    fun confirmWorkStart(requestId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["requests"].update({
                    set("status", "in_progress")
                }) {
                    filter { eq("id", requestId) }
                }
                fetchMyOrders()
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al confirmar inicio: ${e.message}")
            }
        }
    }

    /**
     * El cliente envía una nueva contraoferta.
     */
    fun sendNewOffer(request: ServiceRequest, newPrice: String) {
        viewModelScope.launch {
            try {
                val priceVal = newPrice.toDoubleOrNull() ?: 0.0
                val basePrice = request.services?.price ?: 0.0

                // Validación de seguridad: No ofertar menos del precio base
                if (priceVal < basePrice) {
                    Log.w("MyOrdersVM", "Oferta rechazada: Menor al precio base ($basePrice)")
                    return@launch
                }

                val formattedPrice = "$${priceVal.toInt()}"
                val updatedDescription = "${request.request_description}\n🔹 Nueva oferta Cliente: $formattedPrice"
                
                SupabaseManager.client.postgrest["requests"].update({
                    set("request_description", updatedDescription)
                    set("final_price", priceVal)
                    set("status", "pending")
                }) {
                    filter { eq("id", request.id!!) }
                }
                fetchMyOrders()
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al contraofertar: ${e.message}")
            }
        }
    }

    /**
     * Envía una calificación para un servicio completado.
     */
    fun submitRating(request: ServiceRequest, score: Int, comment: String, onResult: (Boolean) -> Unit) {
        val clientId = request.client_id
        val providerId = request.services?.provider_id ?: return
        val requestId = request.id ?: return

        viewModelScope.launch {
            isSubmittingRating = true
            try {
                val rating = Rating(
                    request_id = requestId,
                    client_id = clientId,
                    provider_id = providerId,
                    score = score,
                    comment = comment
                )
                
                SupabaseManager.client.postgrest["ratings"].insert(rating)
                
                // Marcamos la orden como "calificada" (opcional: podrías añadir una columna calificada a requests)
                // Por ahora solo refrescamos la lista
                fetchMyOrders()
                onResult(true)
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al enviar calificación: ${e.message}")
                onResult(false)
            } finally {
                isSubmittingRating = false
            }
        }
    }

    /**
     * El cliente cancela la solicitud.
     */
    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["requests"].update({
                    set("status", "cancelled")
                }) {
                    filter { eq("id", requestId) }
                }
                fetchMyOrders()
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al cancelar: ${e.message}")
            }
        }
    }
}
