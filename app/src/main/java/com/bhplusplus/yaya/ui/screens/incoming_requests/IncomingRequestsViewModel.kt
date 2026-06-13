package com.bhplusplus.yaya.ui.screens.incoming_requests

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.ServiceRequest
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA SOLICITUDES ENTRANTES (VISTA PRESTADOR)
 * Filtra las solicitudes de la tabla 'requests' que pertenecen a los servicios del prestador actual.
 */
class IncomingRequestsViewModel : ViewModel() {

    var requests by mutableStateOf<List<ServiceRequest>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchIncomingRequests()
    }

    /**
     * Obtiene las solicitudes uniendo con 'services' para filtrar por el prestador actual
     * y con 'profiles' para obtener los datos del cliente.
     */
    fun fetchIncomingRequests() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    // Consulta compleja con Joins:
                    // 1. Traemos todo de 'requests' (*)
                    // 2. Unimos con 'services' (!inner asegura el filtro)
                    // 3. Unimos con 'profiles' (renombrado a client por client_id)
                    val result = SupabaseManager.client.postgrest["requests"]
                        .select(Columns.raw("*, services!inner(*), profiles:client_id(*)")) {
                            filter {
                                eq("services.provider_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                    
                    requests = result.sortedByDescending { it.created_at }
                }
            } catch (e: Exception) {
                Log.e("IncomingReqVM", "Error fetching requests: ${e.message}")
                errorMessage = "No se pudieron cargar las solicitudes."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Actualiza el estado de una solicitud (Aceptar/Rechazar).
     */
    fun updateRequestStatus(requestId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["requests"].update({
                    set("status", newStatus)
                }) {
                    filter { eq("id", requestId) }
                }
                // Refrescamos la lista localmente
                fetchIncomingRequests()
            } catch (e: Exception) {
                Log.e("IncomingReqVM", "Error updating status: ${e.message}")
                errorMessage = "No se pudo actualizar el estado."
            }
        }
    }
}
