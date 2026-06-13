package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: REQUEST
 * Representa una solicitud de servicio (contratación) en la plataforma YÁYA.
 * Mapea con la tabla 'public.requests' de Supabase.
 */
@Serializable
data class ServiceRequest(
    val id: String? = null,
    val client_id: String,
    val service_id: String,
    val request_description: String? = null,
    val service_address: String,
    val scheduled_date: String? = null, // Formato ISO 8601
    val status: String = "pending",
    val created_at: String? = null
)
