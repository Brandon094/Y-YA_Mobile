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
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA EL LISTADO DE CHATS
 * Recupera todos los perfiles con los que el usuario ha tenido contacto.
 */
class ChatListViewModel : ViewModel() {

    var chatContacts by mutableStateOf<List<UserProfile>>(emptyList())
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

                // 2. Extraemos los IDs de los otros usuarios (contactos)
                val contactIds = messages.flatMap { listOf(it.sender_id, it.receiver_id) }
                    .filter { it != currentUser.id }
                    .distinct()

                if (contactIds.isNotEmpty()) {
                    // 3. Recuperamos los perfiles de esos contactos
                    chatContacts = SupabaseManager.client.postgrest["profiles"]
                        .select {
                            filter {
                                isIn("id", contactIds)
                            }
                        }
                        .decodeList<UserProfile>()
                }
            } catch (e: Exception) {
                Log.e("ChatListViewModel", "Error al cargar chats: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
