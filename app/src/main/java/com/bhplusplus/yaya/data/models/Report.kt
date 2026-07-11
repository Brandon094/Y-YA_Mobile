package com.bhplusplus.yaya.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: REPORT
 * Representa una denuncia por mal comportamiento entre usuarios.
 * Sincronizado con la tabla 'public.reports'.
 */
@Serializable
data class Report(
    val id: String? = null,
    val reporter_id: String,
    val reported_user_id: String,
    val reason: String,
    val created_at: String? = null,

    // Datos vinculados (Joins) para mostrar nombres en el Dashboard
    @SerialName("reporter_profile")
    val reporter: UserProfile? = null,
    
    @SerialName("reported_profile")
    val reported: UserProfile? = null
)
