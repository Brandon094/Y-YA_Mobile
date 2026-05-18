package com.example.myapplication.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: USERPROFILE
 * Representa el perfil detallado de un usuario en la plataforma YYA.
 * Mapea directamente con la tabla 'public.profiles' de Supabase.
 */
@Serializable
data class UserProfile(
    val id: String,                    // UUID del usuario (vinculado a auth.users)
    val full_name: String = "",        // Nombre completo del usuario
    val phone: String? = null,         // Número de contacto
    val document_id: String? = null,   // Identificación oficial (DNI, CC, etc.)
    val birth_date: String? = null,    // Fecha de nacimiento (ISO String)
    val address: String? = null,       // Dirección física
    val role: String = "client",       // Rol: 'client', 'provider' o 'admin'
    val avatar_url: String? = null     // URL de la imagen de perfil
)
