package com.bhplusplus.yaya.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.models.ServiceRequest
import com.bhplusplus.yaya.data.models.Message
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.Rating
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

    // Estados de Notificación (Feedback visual)
    var pendingRequestsCount by mutableIntStateOf(0)
    var unreadMessagesCount by mutableIntStateOf(0)
    var pendingAdminServicesCount by mutableIntStateOf(0)

    // Reputación del Prestador
    var averageRating by mutableStateOf(0.0)
        private set
    var totalRatings by mutableIntStateOf(0)
        private set
    var providerRatings by mutableStateOf<List<Rating>>(emptyList())
        private set

    init {
        fetchProfile()
    }

    /**
     * Obtiene los datos del servidor e inicia la escucha de notificaciones.
     */
    fun fetchProfile() {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    email = user.email ?: ""
                    
                    // 1. Cargar Perfil
                    val profile = loadProfileData(user.id, user.userMetadata)
                    userProfile = profile

                    // 2. Cargar conteos iniciales y reputación del prestador
                    fetchNotificationCounts(user.id, profile.role)
                    if (profile.role == "provider" || profile.role == "admin") {
                        fetchProviderRatings(user.id)
                    }

                    // 3. Activar suscripciones Realtime
                    subscribeToNotifications(user.id, profile.role)
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error crítico al obtener perfil: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun loadProfileData(userId: String, metadata: kotlinx.serialization.json.JsonObject?): UserProfile {
        return try {
            SupabaseManager.client.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingle<UserProfile>()
        } catch (e: Exception) {
            val name = metadata?.get("full_name")?.jsonPrimitive?.content ?: "Usuario"
            val role = metadata?.get("role")?.jsonPrimitive?.content ?: "client"
            UserProfile(id = userId, full_name = name, role = role)
        }
    }

    private fun fetchProviderRatings(providerId: String) {
        viewModelScope.launch {
            try {
                val ratingsResult = SupabaseManager.client.postgrest["ratings"]
                    .select { filter { eq("provider_id", providerId) } }
                    .decodeList<Rating>()

                providerRatings = ratingsResult.sortedByDescending { it.created_at }
                totalRatings = ratingsResult.size
                averageRating = if (ratingsResult.isNotEmpty()) {
                    ratingsResult.map { it.score }.average()
                } else 0.0
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error al cargar calificaciones del prestador: ${e.message}")
            }
        }
    }

    private fun fetchNotificationCounts(userId: String, role: String) {
        viewModelScope.launch {
            // Conteo de Mensajes (Para todos)
            unreadMessagesCount = SupabaseManager.client.postgrest["messages"]
                .select { filter { eq("receiver_id", userId); eq("is_read", false) } }
                .decodeList<Message>().size

            // Conteo de Solicitudes (Prestadores/Admin)
            if (role == "provider" || role == "admin") {
                pendingRequestsCount = SupabaseManager.client.postgrest["requests"]
                    .select(Columns.raw("id, services!inner(provider_id)")) {
                        filter { eq("status", "pending"); eq("services.provider_id", userId) }
                    }
                    .decodeList<ServiceRequest>().size
            }

            // Conteo de Aprobaciones Pendientes (Solo Admin)
            if (role == "admin") {
                pendingAdminServicesCount = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("status", "pending_approval") } }
                    .decodeList<Service>().size
            }
        }
    }

    private fun subscribeToNotifications(userId: String, role: String) {
        val channel = SupabaseManager.client.channel("profile_notifications")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            // Escuchar Mensajes
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "messages"
            }.onEach { fetchNotificationCounts(userId, role) }.launchIn(viewModelScope)

            // Escuchar Solicitudes
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "requests"
            }.onEach { fetchNotificationCounts(userId, role) }.launchIn(viewModelScope)

            // Escuchar Servicios (Para Admin)
            if (role == "admin") {
                channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "services"
                }.onEach { fetchNotificationCounts(userId, role) }.launchIn(viewModelScope)
            }

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Error subscribing to notifications: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error setting up notifications flow: ${e.message}")
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
