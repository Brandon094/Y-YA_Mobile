package com.example.myapplication.data

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val title: String = "",
    val description: String = "",
    val id: String? = null,
    val price: Double = 0.0,
    val status: String = "active"
)
