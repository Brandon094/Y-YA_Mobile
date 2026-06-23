package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: AVAILABILITY
 * Refleja la estructura de la tabla 'public.availability'.
 * Define los horarios de atención de los prestadores.
 */
@Serializable
data class Availability(
    val id: String? = null,
    val provider_id: String,
    val day_of_week: Int, // 1 (Lunes) a 7 (Domingo)
    val start_time: String, // Formato "HH:mm:ss"
    val end_time: String    // Formato "HH:mm:ss"
)
