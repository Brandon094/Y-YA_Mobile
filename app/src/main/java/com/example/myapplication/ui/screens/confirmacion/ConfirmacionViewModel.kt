package com.example.myapplication.ui.screens.confirmacion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.models.Service

class ConfirmacionViewModel : ViewModel() {
    var servicio by mutableStateOf("")
    var prestador by mutableStateOf("Maria Chantre")
    var fecha by mutableStateOf("27/08/2025")
    var ubicacion by mutableStateOf("Cl 1 #5-40")
    var precio by mutableStateOf("")
    var tiempo by mutableStateOf("4h")

    fun setServiceData(service: Service) {
        servicio = service.title
        precio = "$ ${service.price}"
    }
}
