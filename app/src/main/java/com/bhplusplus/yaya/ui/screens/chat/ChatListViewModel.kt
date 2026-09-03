package com.bhplusplus.yaya.ui.screens.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Message
import com.bhplusplus.yaya.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Modelo para representar el resumen de un chat en la lista.
 */
data class ChatSummary(
    val contact: UserProfile,
    val lastMessage: String? = null,
    val unreadCount: Int = 0,
    val lastMessageTime: String? = null,
    val displaySubtitle: String = "",
    val isModeration: Boolean = false
)

/**
 * VIEWMODEL PARA EL LISTADO DE CHATS
 * Recupera todos los perfiles con los que el usuario ha tenido contacto y gestiona badges de no leídos.
 */
class ChatListViewModel : ViewModel() {

    var chatSummaries by mutableStateOf<List<ChatSummary>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadChats()
    }

    fun loadChats() {
        val currentUser = SupabaseManager.client.auth.currentUserOrNull() ?: return
        
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Buscamos todos los mensajes donde participe el usuario
                val messages = SupabaseManager.client.postgrest["messages"]
                    .select {
                        filter {
                            or {
                                eq("sender_id", currentUser.id)
                                eq("receiver_id", currentUser.id)
                            }
                        }
                    }
                    .decodeList<Message>()

                // 2. Agrupamos por contacto y calculamos resúmenes
                processMessagesToSummaries(messages, currentUser.id)

                // 3. Activar suscripción Realtime para detectar nuevos mensajes/chats
                subscribeToNewChats()
            } catch (e: Exception) {
                Log.e("ChatListViewModel", "Error al cargar chats: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun processMessagesToSummaries(messages: List<Message>, currentUserId: String) {
        // Obtenemos nuestro propio rol para decidir si enmascaramos
        val myProfile = try {
            SupabaseManager.client.postgrest["profiles"]
                .select { filter { eq("id", currentUserId) } }
                .decodeSingle<UserProfile>()
        } catch (e: Exception) { null }

        // Extraemos los IDs de los contactos únicos
        val contactIds = messages.flatMap { listOf(it.sender_id, it.receiver_id) }
            .filter { it != currentUserId }
            .distinct()

        if (contactIds.isEmpty()) {
            chatSummaries = emptyList()
            return
        }

        // Recuperamos los perfiles de esos contactos
        val profiles = SupabaseManager.client.postgrest["profiles"]
            .select {
                filter {
                    isIn("id", contactIds)
                }
            }
            .decodeList<UserProfile>()

        // Construimos el resumen por cada contacto
        chatSummaries = profiles.map { profile ->
            val contactMessages = messages.filter { 
                it.sender_id.equals(profile.id, ignoreCase = true) || 
                it.receiver_id.equals(profile.id, ignoreCase = true)
            }.sortedByDescending { it.sent_at ?: "" }

            val lastMsg = contactMessages.firstOrNull()
            val unread = contactMessages.count { 
                it.receiver_id.equals(currentUserId, ignoreCase = true) && !it.is_read 
            }

            val subtitle = if (!lastMsg?.content.isNullOrBlank()) {
                lastMsg!!.content
            } else {
                if (profile.role == "provider") "Prestador" else "Cliente"
            }

            // Anonimato: Se enmascara si el contacto es Admin y nosotros NO
            val isModeration = profile.role == "admin" && myProfile?.role != "admin"
            val displayName = if (isModeration) "Equipo de Moderación" else profile.full_name

            ChatSummary(
                contact = profile.copy(full_name = displayName),
                lastMessage = lastMsg?.content,
                unreadCount = unread,
                lastMessageTime = lastMsg?.sent_at?.take(16)?.replace("T", " "),
                displaySubtitle = subtitle,
                isModeration = isModeration
            )
        }.sortedByDescending { it.lastMessageTime ?: "" }
    }

    /**
     * Se suscribe a cambios en la tabla 'messages' para refrescar la lista de contactos y contadores.
     */
    private fun subscribeToNewChats() {
        val channel = SupabaseManager.client.channel("chat_list_realtime")
        if (channel.status.value != RealtimeChannel.Status.UNSUBSCRIBED) return

        try {
            channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "messages"
            }.onEach {
                // Refrescamos la lista de chats ante cualquier mensaje nuevo o cambio de estado (is_read)
                loadChatsSilently()
            }.launchIn(viewModelScope)

            viewModelScope.launch {
                try {
                    channel.subscribe()
                } catch (e: Exception) {
                    Log.e("ChatListVM", "Error subscribing: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("ChatListVM", "Error setting up postgresChangeFlow: ${e.message}")
        }
    }

    /**
     * Refresca los contactos sin activar isLoading.
     */
    private fun loadChatsSilently() {
        val currentUser = SupabaseManager.client.auth.currentUserOrNull() ?: return
        viewModelScope.launch {
            try {
                val messages = SupabaseManager.client.postgrest["messages"]
                    .select {
                        filter {
                            or {
                                eq("sender_id", currentUser.id)
                                eq("receiver_id", currentUser.id)
                            }
                        }
                    }
                    .decodeList<Message>()

                processMessagesToSummaries(messages, currentUser.id)
            } catch (e: Exception) {
                Log.e("ChatListVM", "Error silent load: ${e.message}")
            }
        }
    }
}
