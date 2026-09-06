package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: USERPROFILE
 * Mapea directamente con la tabla 'public.profiles' de Supabase.
 * Los campos principales (id, full_name, role) no llevan valores por defecto
 * para obligar a kotlinx.serialization a serializarlos e incluirlos siempre
 * en las peticiones JSON de Supabase Postgrest (evitando errores not-null Code 23502).
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
    val municipality: String? = "La Plata",
    val avatar_url: String? = null,
    val fcm_token: String? = null,
    val is_suspended: Boolean = false
)
