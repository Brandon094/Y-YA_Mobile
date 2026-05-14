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

class HomeViewModel : ViewModel() {

    var services by mutableStateOf<List<Service>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchServices()
    }

    fun fetchServices() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Traemos todos los servicios de la tabla 'services'
                val result = SupabaseManager.client.postgrest["services"]
                    .select()
                    .decodeList<Service>()
                
                services = result
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error cargando servicios", e)
            } finally {
                isLoading = false
            }
        }
    }
}
