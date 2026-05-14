package com.example.myapplication.data

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val title: String,
    val description: String
)
