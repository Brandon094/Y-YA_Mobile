package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: SERVICE
 * Refleja fielmente la estructura de la tabla 'public.services' en Supabase.
 */
@Serializable
data class Service(
    val id: String? = null,              // UUID generado por DB
    val provider_id: String? = null,      // FKey a profiles.id (quién ofrece el servicio)
    val category_id: String? = null,      // FKey a categories.id
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val estimated_time: String? = null,   // Tiempo estimado (ej: "2 horas")
    val materials_included: Boolean = false,
    val extra_cost: Double = 0.0,
    val status: String = "pending_approval", // active, inactive, pending_approval (Hito 5)
    val created_at: String? = null
)
