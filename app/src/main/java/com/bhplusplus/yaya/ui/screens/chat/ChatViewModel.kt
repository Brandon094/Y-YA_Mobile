package com.bhplusplus.yaya.ui.screens.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Message
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA EL CHAT EN TIEMPO REAL (Hito 2)
 * Gestiona el historial de mensajes y la suscripción Realtime.
 */
class ChatViewModel : ViewModel() {

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val currentUser = SupabaseManager.client.auth.currentUserOrNull()

    /**
     * Inicializa el chat cargando el historial y activando Realtime.
     */
    fun initializeChat(receiverId: String) {
        if (currentUser == null) return
        
        fetchMessages(receiverId)
        subscribeToMessages(receiverId)
    }

    /**
     * Carga los mensajes existentes entre ambos usuarios.
     */
    private fun fetchMessages(receiverId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val currentUserId = currentUser?.id ?: return@launch
                
                // Consultamos mensajes donde participen ambos usuarios
                val result = SupabaseManager.client.postgrest["messages"]
                    .select {
                        filter {
                            or {
                                and {
                                    eq("sender_id", currentUserId)
                                    eq("receiver_id", receiverId)
                                }
                                and {
                                    eq("sender_id", receiverId)
                                    eq("receiver_id", currentUserId)
                                }
                            }
                        }
                    }
                    .decodeList<Message>()
                
                messages = result.sortedBy { it.sent_at }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error al cargar mensajes: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Se suscribe a cambios en tiempo real en la tabla 'messages'.
     */
    private fun subscribeToMessages(receiverId: String) {
        val currentUserId = currentUser?.id ?: return
        
        val channel = SupabaseManager.client.channel("chat_channel")
        
        // Escuchamos inserciones en la tabla messages
        channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "messages"
        }.onEach { action ->
            val newMessage = action.decodeRecord<Message>()
            
            // Solo añadimos el mensaje si pertenece a esta conversación específica
            if ((newMessage.sender_id == currentUserId && newMessage.receiver_id == receiverId) ||
                (newMessage.sender_id == receiverId && newMessage.receiver_id == currentUserId)) {
                
                // Evitamos duplicados si el mensaje ya fue añadido por la carga inicial o envío local
                if (messages.none { it.id == newMessage.id }) {
                    messages = messages + newMessage
                }
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            channel.subscribe()
        }
    }

    /**
     * Envía un nuevo mensaje a la base de datos.
     */
    fun sendMessage(receiverId: String, content: String) {
        if (content.isBlank() || currentUser == null) return
        
        viewModelScope.launch {
            try {
                val newMessage = Message(
                    sender_id = currentUser.id,
                    receiver_id = receiverId,
                    content = content
                )
                
                // Insertamos en Supabase. El suscriptor Realtime se encargará de mostrarlo.
                SupabaseManager.client.postgrest["messages"].insert(newMessage)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error al enviar mensaje: ${e.message}")
            }
        }
    }
}
