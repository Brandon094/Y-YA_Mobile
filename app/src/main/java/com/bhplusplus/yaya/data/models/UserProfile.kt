package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: USERPROFILE
 * Mapea directamente con la tabla 'public.profiles' de Supabase.
 * Los campos opcionales son marcados como nulables para evitar errores de parseo.
 */
@Serializable
data class UserProfile(
    val id: String,
    val full_name: String,
    val role: String,
    val phone: String? = null,
    val document_id: String? = null,
    val birth_date: String? = null,
    val address: String? = null,
    val avatar_url: String? = null
)
