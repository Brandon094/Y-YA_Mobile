package com.example.myapplication.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: SERVICE
 * Representa un servicio ofrecido en la plataforma YYA.
 * La anotación @Serializable permite que Supabase convierta automáticamente el JSON de la BD a esta clase.
 */
@Serializable
data class Service(
    val title: String = "",       // Título descriptivo del servicio
    val description: String = "", // Detalle de lo que incluye el servicio
    val id: String? = null,       // Identificador único (UUID) de la base de datos
    val price: Double = 0.0,      // Precio base del servicio
    val status: String = "active" // Estado actual (activo/inactivo)
)
