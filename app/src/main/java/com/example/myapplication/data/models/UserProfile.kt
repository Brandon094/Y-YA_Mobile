package com.example.myapplication.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: USERPROFILE
 * Mapea directamente con la tabla 'public.profiles' de Supabase.
 */
@Serializable
data class UserProfile(
    val id: String,
    val full_name: String,
    val phone: String,
    val document_id: String,
    val birth_date: String,
    val address: String,
    val role: String, // Campo obligatorio
    val avatar_url: String? = null
)
