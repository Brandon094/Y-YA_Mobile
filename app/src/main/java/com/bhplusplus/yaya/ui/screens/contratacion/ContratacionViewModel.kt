package com.bhplusplus.yaya.ui.screens.contratacion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.ServiceRequest
import com.bhplusplus.yaya.data.models.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

/**
 * VIEWMODEL PARA LA PANTALLA DE CONTRATACIÓN
 */
class ContratacionViewModel : ViewModel() {
    
    // Datos cargados desde Supabase
    var service by mutableStateOf<Service?>(null)
        private set
    var providerProfile by mutableStateOf<UserProfile?>(null)
        private set

    // Estados del formulario
    var direccion by mutableStateOf("")
    var fecha by mutableStateOf("") // Formato YYYY-MM-DD
    var hora by mutableStateOf("")  // Formato HH:mm
    var oferta by mutableStateOf("")

    // Estados de control
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Carga toda la información necesaria desde Supabase usando el ID real.
     */
    fun loadData(serviceId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Descargar el Servicio Real
                val serviceResult = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("id", serviceId) } }
                    .decodeSingle<Service>()
                
                service = serviceResult
                if (oferta.isEmpty()) oferta = serviceResult.price.toString()

                // 2. Descargar el Perfil del Prestador Real
                serviceResult.provider_id?.let { pId ->
                    val providerResult = SupabaseManager.client.postgrest["profiles"]
                        .select { filter { eq("id", pId) } }
                        .decodeSingle<UserProfile>()
                    providerProfile = providerResult
                }
            } catch (e: Exception) {
                Log.e("ContratacionVM", "Error al cargar datos: ${e.message}")
                errorMessage = "No se pudo obtener la información del servicio."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Crea la reserva en Supabase.
     */
    fun contratar(onResult: (Boolean, String?) -> Unit) {
        val currentService = service ?: return

        if (direccion.isBlank() || fecha.isBlank() || hora.isBlank()) {
            errorMessage = "Completa dirección, fecha y hora."
            onResult(false, null)
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val clientId = SupabaseManager.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("Sesión inválida")

                // Combinar fecha y hora para crear un LocalDateTime
                val date = LocalDate.parse(fecha)
                val time = LocalTime.parse(hora)
                val combinedDateTime = LocalDateTime.of(date, time)
                
                // Formatear para Supabase (Timestamptz)
                val scheduledDate = combinedDateTime.format(DateTimeFormatter.ISO_DATE_TIME)

                val request = ServiceRequest(
                    client_id = clientId,
                    service_id = currentService.id!!,
                    request_description = "Oferta: $oferta. Programado para: $fecha a las $hora",
                    service_address = direccion,
                    scheduled_date = scheduledDate,
                    status = "pending"
                )

                // Insertamos y recuperamos el ID generado
                val response = SupabaseManager.client.postgrest["requests"].insert(request) {
                    select()
                }.decodeSingle<ServiceRequest>()

                onResult(true, response.id)
            } catch (e: Exception) {
                Log.e("ContratacionVM", "Error al crear: ${e.message}")
                errorMessage = "Error al procesar la reserva. Verifica los datos."
                onResult(false, null)
            } finally {
                isLoading = false
            }
        }
    }
}
