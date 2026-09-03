package com.bhplusplus.yaya.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: REQUEST
 * Representa una solicitud de servicio (contratación) en la plataforma YÁYA.
 * Mapea con la tabla 'public.requests' de Supabase e incluye datos unidos (joins).
 */
@Serializable
data class ServiceRequest(
    val id: String? = null,
    val client_id: String = "",
    val service_id: String = "",
    val final_price: Double = 0.0, // Precio final acordado (Hito 1)
    val request_description: String? = null,
    val service_address: String = "",
    val scheduled_date: String? = null, // Formato ISO 8601
    val status: String = "pending",
    val created_at: String? = null,
    
    // Datos de relaciones (opcionales para cuando se hace un SELECT con join)
    val services: Service? = null,
    
    @SerialName("profiles")
    val client: UserProfile? = null
)
