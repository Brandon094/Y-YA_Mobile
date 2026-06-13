package com.bhplusplus.yaya.data.models

import kotlinx.serialization.Serializable

/**
 * MODELO DE DATOS: CATEGORY
 * Representa las categorías de servicios disponibles en YÁYA.
 */
@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon_name: String? = null
)
