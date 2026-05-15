package com.example.myapplication.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Service
import com.example.myapplication.data.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log

/**
 * LÓGICA DE NEGOCIO PARA LA PANTALLA PRINCIPAL
 * Se encarga de traer los datos de los servicios desde la base de datos de Supabase.
 */
class HomeViewModel : ViewModel() {

    // Lista de servicios observable por la UI (Compose State)
    var services by mutableStateOf<List<Service>>(emptyList())
        private set

    // Estado para mostrar el indicador de carga en el listado
    var isLoading by mutableStateOf(false)
        private set

    // Al instanciar el ViewModel, cargamos los servicios automáticamente
    init {
        fetchServices()
    }

    /**
     * Consulta la tabla 'services' de Supabase.
     */
    fun fetchServices() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Realizamos un SELECT a la tabla 'services'
                // decodeList convierte automáticamente el JSON de la BD en una lista de objetos Service
                val result = SupabaseManager.client.postgrest["services"]
                    .select()
                    .decodeList<Service>()
                
                services = result
            } catch (e: Exception) {
                // Log de error detallado para depuración
                Log.e("HomeViewModel", "ERROR DE SUPABASE: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
