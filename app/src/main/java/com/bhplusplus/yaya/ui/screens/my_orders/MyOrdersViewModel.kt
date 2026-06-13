package com.bhplusplus.yaya.ui.screens.my_orders

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
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA LA PANTALLA DE MIS PEDIDOS
 * Recupera el historial de solicitudes del usuario actual desde Supabase.
 */
class MyOrdersViewModel : ViewModel() {

    // Lista de pedidos filtrada para el usuario actual
    var orders by mutableStateOf<List<ServiceRequest>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchMyOrders()
    }

    /**
     * Consulta la tabla 'requests' filtrando por client_id.
     */
    fun fetchMyOrders() {
        viewModelScope.launch {
            isLoading = true
            try {
                val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    // Consultamos las solicitudes donde el usuario es el cliente
                    val result = SupabaseManager.client.postgrest["requests"]
                        .select {
                            filter {
                                eq("client_id", userId)
                            }
                        }
                        .decodeList<ServiceRequest>()
                    
                    // Ordenamos por fecha de creación (más recientes primero)
                    orders = result.sortedByDescending { it.created_at }
                }
            } catch (e: Exception) {
                Log.e("MyOrdersVM", "Error al cargar pedidos: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
