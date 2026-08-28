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
import com.bhplusplus.yaya.utils.FormatterUtils
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Estado de UI para la pantalla de confirmación.
 */
data class ConfirmationUiState(
    val serviceTitle: String,
    val providerName: String,
    val formattedDate: String,
    val formattedTime: String,
    val address: String,
    val formattedBasePrice: String,
    val formattedOfferPrice: String,
    val estimatedTime: String
)

class ConfirmacionViewModel : ViewModel() {
    
    var uiState by mutableStateOf<ConfirmationUiState?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    /**
     * Carga los datos reales de la solicitud y el servicio desde Supabase.
     */
    fun loadRequestData(requestId: String, serviceId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Cargar la Solicitud
                val request = SupabaseManager.client.postgrest["requests"]
                    .select { filter { eq("id", requestId) } }
                    .decodeSingle<ServiceRequest>()
                
                // 2. Cargar el Servicio
                val serviceResult = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("id", serviceId) } }
                    .decodeSingle<Service>()
                
                // 3. Cargar el Prestador
                val providerName = serviceResult.provider_id?.let { pId ->
                    SupabaseManager.client.postgrest["profiles"]
                        .select { filter { eq("id", pId) } }
                        .decodeSingle<UserProfile>().full_name
                } ?: "Prestador Independiente"

                // Formatear Fecha y Hora
                val zdt = request.scheduled_date?.let { ZonedDateTime.parse(it) }
                val dateLabel = zdt?.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale("es", "CO")))
                    ?.replaceFirstChar { it.uppercase() } ?: "Fecha no definida"
                val timeLabel = zdt?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Hora no definida"

                uiState = ConfirmationUiState(
                    serviceTitle = serviceResult.title,
                    providerName = providerName,
                    formattedDate = dateLabel,
                    formattedTime = timeLabel,
                    address = request.service_address,
                    formattedBasePrice = FormatterUtils.formatCurrency(serviceResult.price),
                    formattedOfferPrice = FormatterUtils.formatCurrency(request.final_price),
                    estimatedTime = serviceResult.estimated_time ?: "N/A"
                )

            } catch (e: Exception) {
                Log.e("ConfirmacionVM", "Error al cargar datos: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
