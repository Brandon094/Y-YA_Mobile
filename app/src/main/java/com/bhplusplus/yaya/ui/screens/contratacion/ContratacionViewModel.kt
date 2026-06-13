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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

/**
 * VIEWMODEL PARA LA PANTALLA DE CONTRATACIÓN
 */
class ContratacionViewModel : ViewModel() {
    
    // Perfil del prestador real traído de Supabase
    var providerProfile by mutableStateOf<UserProfile?>(null)
        private set

    // Estados de los campos del formulario
    var direccion by mutableStateOf("")
    var hora by mutableStateOf("")
    var oferta by mutableStateOf("")

    // Estados de control
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Inicializa los datos de la pantalla con la información del servicio.
     */
    fun setInitialData(service: Service) {
        if (oferta.isEmpty()) {
            oferta = service.price.toString()
        }
    }

    /**
     * Descarga la información del prestador desde la tabla 'profiles' usando su ID.
     */
    fun loadProviderInfo(providerId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = SupabaseManager.client.postgrest["profiles"]
                    .select { filter { eq("id", providerId) } }
                    .decodeSingle<UserProfile>()
                
                providerProfile = result
            } catch (e: Exception) {
                Log.e("ContratacionVM", "Error cargando prestador: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Guarda la solicitud en la tabla 'requests'.
     */
    fun contratar(service: Service, onResult: (Boolean) -> Unit) {
        if (direccion.isBlank() || hora.isBlank()) {
            errorMessage = "Por favor, ingresa la dirección y la hora sugerida."
            onResult(false)
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val clientId = SupabaseManager.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("Sesión no válida.")

                val request = ServiceRequest(
                    client_id = clientId,
                    service_id = service.id ?: "",
                    request_description = "Oferta: $oferta. Hora: $hora",
                    service_address = direccion,
                    scheduled_date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    status = "pending"
                )

                SupabaseManager.client.postgrest["requests"].insert(request)
                onResult(true)
            } catch (e: Exception) {
                errorMessage = "Error al procesar la solicitud: ${e.localizedMessage}"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }
}
