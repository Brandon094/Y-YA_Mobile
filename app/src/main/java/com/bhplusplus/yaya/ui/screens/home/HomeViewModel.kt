package com.bhplusplus.yaya.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Category
import com.bhplusplus.yaya.data.models.Rating
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.ServiceRequest
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.utils.FormatterUtils
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

/**
 * Modelo de UI para un servicio en el catálogo.
 * Centraliza el formateo para mantener la View "tonta".
 */
data class ServiceUiState(
    val domain: Service,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val categoryName: String?,
    val formattedTimeRange: String,
    val workingDays: List<Int>,
    val providerAvatarUrl: String?,
    val averageRating: Double,
    val totalRatings: Int
)

/**
 * LÓGICA DE NEGOCIO PARA LA PANTALLA PRINCIPAL
 * Gestiona el listado, la búsqueda y el filtrado por categorías.
 */
class HomeViewModel : ViewModel() {

    // Lista original (sin filtrar) traída de Supabase
    private var allServices = emptyList<Service>()

    // Lista que se muestra en la UI (procesada y filtrada)
    var filteredServices by mutableStateOf<List<ServiceUiState>>(emptyList())
        private set

    // Lista de categorías para el selector horizontal
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    // Estado del filtro actual
    var searchQuery by mutableStateOf("")
    var selectedCategoryId by mutableStateOf<String?>(null)
    var selectedMunicipality by mutableStateOf<String?>("La Plata")
        private set

    fun onMunicipalitySelect(municipality: String?) {
        selectedMunicipality = municipality
        applyFilters()
    }

    var userRole by mutableStateOf<String?>(null)
    var userProfile by mutableStateOf<UserProfile?>(null)
    var notificationCount by mutableStateOf(0)
    var unreadMessagesCount by mutableStateOf(0)
    var isLoading by mutableStateOf(false)
        private set

    // Mapa para cachear calificaciones y no recalcular en cada filtro
    private var serviceRatingsMap = emptyMap<String, List<Rating>>()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Obtener categorías reales de la DB
                categories = SupabaseManager.client.postgrest["categories"]
                    .select()
                    .decodeList<Category>()

                // 2. Obtener todos los servicios (Filtrando por estado activo para usuarios normales) con Join del prestador
                val servicesResult = SupabaseManager.client.postgrest["services"]
                    .select(Columns.raw("*, provider_profile:provider_id(*)")) {
                        filter {
                            eq("status", "active") // Solo servicios aprobados
                        }
                    }
                    .decodeList<Service>()
                
                allServices = servicesResult

                // 3. Obtener todas las calificaciones para estos servicios
                val serviceIds = allServices.mapNotNull { it.id }
                if (serviceIds.isNotEmpty()) {
                    val ratingsResult = SupabaseManager.client.postgrest["ratings"]
                        .select()
                        .decodeList<Rating>()
                    
                    // Agrupamos por provider_id (ya que las calificaciones son al prestador)
                    serviceRatingsMap = ratingsResult.groupBy { it.provider_id }
                }

                applyFilters() // Inicializamos la lista filtrada

                // Activar suscripción Realtime para servicios
                subscribeToServices()
                subscribeToRatings()

                // 3. Obtener rol del usuario y contar notificaciones
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    // Sincronizar Token de Notificaciones proactivamente
                    SupabaseManager.syncFcmToken()

                    try {
                        val profile = SupabaseManager.client.postgrest["profiles"]
                            .select { filter { eq("id", user.id) } }
                            .decodeSingle<UserProfile>()
                        userRole = profile.role
                        userProfile = profile
                        if (!profile.municipality.isNullOrBlank()) {
                            selectedMunicipality = profile.municipality
                        }
                        
                        // Contar notificaciones según el rol (Hito 4)
                        fetchNotificationCount(user.id, profile.role)
                        fetchUnreadMessagesCount(user.id)

                        // Activar suscripciones reactivas para Badges
                        subscribeToRequests(user.id, profile.role)
                        subscribeToUnreadMessages(user.id)
                    } catch (e: Exception) {
                        Log.w("HomeViewModel", "Perfil no encontrado en DB, usando metadata.")
                        // Recuperamos el rol desde los metadatos de Auth para no bloquear la UI
                        userRole = user.userMetadata?.get("role")?.jsonPrimitive?.content ?: "client"
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "ERROR: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Se suscribe a cambios en tiempo real en la tabla 'services'.
     */
    private fun subscribeToServices() {
        val channel = SupabaseManager.client.channel("services_home")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return
        
        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "services"
            }.onEach { action ->
                when (action) {
                    is PostgresAction.Insert -> {
                        val newService = action.decodeRecord<Service>()
                        if (newService.status == "active") {
                            allServices = allServices + newService
                        }
                    }
                    is PostgresAction.Update -> {
                        val updatedService = action.decodeRecord<Service>()
                        allServices = if (updatedService.status == "active") {
                            allServices.filter { it.id != updatedService.id } + updatedService
                        } else {
                            allServices.filter { it.id != updatedService.id }
                        }
                    }
                    is PostgresAction.Delete -> {
                        val deletedId = action.oldRecord["id"]?.jsonPrimitive?.content
                        allServices = allServices.filter { it.id != deletedId }
                    }
                    else -> {}
                }
                applyFilters()
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error subscribing to services: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error setting up services flow: ${e.message}")
        }
    }

    /**
     * Se suscribe a cambios en las calificaciones para actualizar el promedio visual.
     */
    private fun subscribeToRatings() {
        val channel = SupabaseManager.client.channel("ratings_home")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "ratings"
            }.onEach {
                // Recargamos el mapa de calificaciones ante cualquier cambio
                val ratingsResult = SupabaseManager.client.postgrest["ratings"]
                    .select()
                    .decodeList<Rating>()
                
                serviceRatingsMap = ratingsResult.groupBy { it.provider_id }
                applyFilters()
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error subscribing to ratings: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error setting up ratings flow: ${e.message}")
        }
    }

    /**
     * Cuenta solicitudes pendientes según el rol para mostrar en el badge (Hito 4).
     */
    private fun fetchNotificationCount(userId: String, role: String) {
        viewModelScope.launch {
            try {
                if (role == "provider" || role == "admin") {
                    // FIX: Filtrar solicitudes que pertenecen a los servicios DEL PRESTADOR actual
                    val pendingCount = SupabaseManager.client.postgrest["requests"]
                        .select(Columns.raw("id, services!inner(provider_id)")) {
                            filter {
                                eq("status", "pending")
                                eq("services.provider_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                        .size
                    notificationCount = pendingCount
                } else {
                    // OPCIONAL: Para clientes, contar las solicitudes que esperan su Handshake
                    val clientPending = SupabaseManager.client.postgrest["requests"]
                        .select {
                            filter {
                                eq("client_id", userId)
                                eq("status", "accepted")
                            }
                        }
                        .decodeList<ServiceRequest>()
                        .size
                    notificationCount = clientPending
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al contar notificaciones: ${e.message}")
            }
        }
    }

    /**
     * Cuenta mensajes no leídos dirigidos al usuario actual.
     */
    private fun fetchUnreadMessagesCount(userId: String) {
        viewModelScope.launch {
            try {
                val count = SupabaseManager.client.postgrest["messages"]
                    .select {
                        filter {
                            eq("receiver_id", userId)
                            eq("is_read", false)
                        }
                    }
                    .decodeList<com.bhplusplus.yaya.data.models.Message>()
                    .size
                unreadMessagesCount = count
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al contar mensajes no leídos: ${e.message}")
            }
        }
    }

    /**
     * Se suscribe a cambios en las solicitudes para actualizar el contador de notificaciones.
     */
    private fun subscribeToRequests(userId: String, role: String) {
        val channel = SupabaseManager.client.channel("requests_notifications")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "requests"
            }.onEach {
                // Refrescamos el conteo real ante cualquier cambio (Insert, Update, Delete)
                fetchNotificationCount(userId, role)
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error subscribing to requests: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error setting up requests flow: ${e.message}")
        }
    }

    /**
     * Se suscribe a mensajes nuevos para actualizar el contador de no leídos.
     */
    private fun subscribeToUnreadMessages(userId: String) {
        val channel = SupabaseManager.client.channel("messages_notifications")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "messages"
            }.onEach { action ->
                // Si llega un mensaje nuevo donde somos el receptor, o si se marca algo como leído
                fetchUnreadMessagesCount(userId)
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error subscribing to unread messages: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error setting up unread messages flow: ${e.message}")
        }
    }

    /**
     * Filtra y mapea los servicios a su estado de UI.
     */
    fun applyFilters() {
        val filtered = allServices.filter { service ->
            val matchesSearch = service.title.contains(searchQuery, ignoreCase = true) ||
                               service.description.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategoryId == null || service.category_id == selectedCategoryId
            
            val serviceMuni = service.municipality ?: service.provider?.municipality ?: "La Plata"
            val matchesMunicipality = selectedMunicipality.isNullOrEmpty() ||
                                      selectedMunicipality.equals("Todos", ignoreCase = true) ||
                                      serviceMuni.equals(selectedMunicipality, ignoreCase = true)

            matchesSearch && matchesCategory && matchesMunicipality
        }

        filteredServices = filtered.map { service ->
            val category = categories.find { it.id == service.category_id }
            val providerRatings = serviceRatingsMap[service.provider_id] ?: emptyList()
            
            ServiceUiState(
                domain = service,
                title = service.title,
                description = service.description,
                formattedPrice = FormatterUtils.formatCurrency(service.price),
                categoryName = category?.name,
                formattedTimeRange = if (service.start_time.isNotEmpty()) {
                    "${FormatterUtils.formatTime(service.start_time)} - ${FormatterUtils.formatTime(service.end_time)}"
                } else "",
                workingDays = service.working_days,
                providerAvatarUrl = service.provider?.avatar_url,
                averageRating = if (providerRatings.isNotEmpty()) providerRatings.map { it.score }.average() else 0.0,
                totalRatings = providerRatings.size
            )
        }
    }

    /**
     * Al cambiar el texto de búsqueda
     */
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        applyFilters()
    }

    /**
     * Al seleccionar o deseleccionar una categoría
     */
    fun onCategorySelect(categoryId: String?) {
        selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
        applyFilters()
    }
}
