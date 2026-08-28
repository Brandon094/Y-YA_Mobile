package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: SERVICEIMAGE
 * Mapea directamente con la tabla 'public.service_images'.
 */
@Serializable
data class ServiceImage(
    val id: String? = null,
    val service_id: String,
    val image_url: String,
    val created_at: String? = null
)
