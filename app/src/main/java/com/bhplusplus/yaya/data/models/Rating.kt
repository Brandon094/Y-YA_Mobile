package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: RATING
 * Representa una calificación otorgada por un cliente tras finalizar un servicio.
 * Sincronizado con la tabla 'public.ratings'.
 */
@Serializable
data class Rating(
    val id: String? = null,
    val request_id: String = "",
    val client_id: String = "",
    val provider_id: String = "",
    val score: Int = 0, // 1 a 5
    val comment: String? = null,
    val created_at: String? = null
)
