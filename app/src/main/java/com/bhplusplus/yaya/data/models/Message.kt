package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: MESSAGE
 * Representa un mensaje de chat entre dos usuarios.
 * Sincronizado con la tabla 'public.messages'.
 */
@Serializable
data class Message(
    val id: String? = null,
    val sender_id: String = "",
    val receiver_id: String = "",
    val content: String = "",
    val is_read: Boolean = false,
    val sent_at: String? = null
)
