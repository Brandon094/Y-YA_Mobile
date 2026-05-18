package com.example.myapplication.ui.screens.contratacion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.models.Service

class ContratacionViewModel : ViewModel() {
    var servicio by mutableStateOf("")
    var direccion by mutableStateOf("")
    var hora by mutableStateOf("")
    var oferta by mutableStateOf("")

    fun setInitialData(service: Service) {
        if (servicio.isEmpty()) {
            servicio = service.title
            oferta = service.price.toString()
        }
    }
    // Almacenar la contratacion en supabase
    fun contratar(onSuccess: () -> Unit) {
        // Aquí iría la lógica para guardar la contratación en Supabase si fuera necesario
        onSuccess()
    }
}
