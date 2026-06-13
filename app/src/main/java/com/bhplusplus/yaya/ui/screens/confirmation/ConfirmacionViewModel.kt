package com.bhplusplus.yaya.ui.screens.confirmation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.ServiceRequest
import com.bhplusplus.yaya.data.models.UserProfile
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ConfirmacionViewModel : ViewModel() {
    var servicio by mutableStateOf("Cargando...")
    var prestador by mutableStateOf("Cargando...")
    var fecha by mutableStateOf("")
    var ubicacion by mutableStateOf("")
    var precio by mutableStateOf("")
    var tiempo by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    /**
     * Carga los datos reales de la solicitud y el servicio desde Supabase.
     */
    fun loadRequestData(requestId: String, serviceId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Cargar la Solicitud (Request) para ver dirección y fecha programada
                val request = SupabaseManager.client.postgrest["requests"]
                    .select { filter { eq("id", requestId) } }
                    .decodeSingle<ServiceRequest>()
                
                ubicacion = request.service_address
                
                // Formatear fecha
                request.scheduled_date?.let { 
                    try {
                        val zdt = ZonedDateTime.parse(it)
                        val formatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale("es", "CO"))
                        fecha = zdt.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    } catch (e: Exception) {
                        fecha = it.take(10)
                    }
                }

                // 2. Cargar el Servicio para ver el título y precio base
                val serviceResult = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("id", serviceId) } }
                    .decodeSingle<Service>()
                
                servicio = serviceResult.title
                precio = "$ ${serviceResult.price}"
                tiempo = serviceResult.estimated_time ?: "N/A"

                // 3. Cargar el Prestador para ver su nombre
                serviceResult.provider_id?.let { pId ->
                    val providerResult = SupabaseManager.client.postgrest["profiles"]
                        .select { filter { eq("id", pId) } }
                        .decodeSingle<UserProfile>()
                    prestador = providerResult.full_name
                }

            } catch (e: Exception) {
                Log.e("ConfirmacionVM", "Error al cargar datos reales: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
