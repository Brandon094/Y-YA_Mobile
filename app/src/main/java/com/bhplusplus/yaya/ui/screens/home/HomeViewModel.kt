package com.bhplusplus.yaya.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.models.Category
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.serialization.json.jsonPrimitive

/**
 * LÓGICA DE NEGOCIO PARA LA PANTALLA PRINCIPAL
 * Gestiona el listado, la búsqueda y el filtrado por categorías.
 */
class HomeViewModel : ViewModel() {

    // Lista original (sin filtrar) traída de Supabase
    private var allServices = emptyList<Service>()

    // Lista que se muestra en la UI (ya filtrada)
    var filteredServices by mutableStateOf<List<Service>>(emptyList())
        private set

    // Lista de categorías para el selector horizontal
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    // Estado del filtro actual
    var searchQuery by mutableStateOf("")
    var selectedCategoryId by mutableStateOf<String?>(null)

    var userRole by mutableStateOf<String?>(null)
    var notificationCount by mutableStateOf(0)
    var isLoading by mutableStateOf(false)
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Obtener categorías reales de la DB
                categories = SupabaseManager.client.postgrest["categories"]
                    .select()
                    .decodeList<Category>()

                // 2. Obtener todos los servicios (Filtrando por estado activo para usuarios normales)
                allServices = SupabaseManager.client.postgrest["services"]
                    .select {
                        filter {
                            eq("status", "active") // Solo servicios aprobados
                        }
                    }
                    .decodeList<Service>()
                
                applyFilters() // Inicializamos la lista filtrada

                // 3. Obtener rol del usuario y contar notificaciones
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    try {
                        val profile = SupabaseManager.client.postgrest["profiles"]
                            .select { filter { eq("id", user.id) } }
                            .decodeSingle<UserProfile>()
                        userRole = profile.role
                        
                        // Contar notificaciones según el rol (Hito 4)
                        fetchNotificationCount(user.id, profile.role)
                    } catch (e: Exception) {
                        Log.w("HomeViewModel", "Perfil no encontrado en DB, usando metadata.")
                        // Recuperamos el rol desde los metadatos de Auth para no bloquear la UI
                        userRole = user.userMetadata?.get("role")?.jsonPrimitive?.content ?: "client"
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "ERROR: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Cuenta solicitudes pendientes según el rol para mostrar en el badge (Hito 4).
     */
    private fun fetchNotificationCount(userId: String, role: String) {
        viewModelScope.launch {
            try {
                if (role == "provider" || role == "admin") {
                    // Si es prestador, cuenta solicitudes recibidas con estado 'pending'
                    val pendingCount = SupabaseManager.client.postgrest["requests"]
                        .select {
                            filter {
                                // Necesitamos filtrar por los servicios que pertenecen a este prestador
                                // Para simplificar en esta fase, buscamos solicitudes con estado pending
                                // En una fase avanzada usaríamos un join o una RPC
                                eq("status", "pending")
                            }
                        }
                        .decodeList<Service>() // Usamos Service solo para contar, no importa el tipo exacto
                        .size
                    notificationCount = pendingCount
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al contar notificaciones: ${e.message}")
            }
        }
    }

    /**
     * Filtra la lista localmente para que sea instantáneo para el usuario.
     */
    fun applyFilters() {
        filteredServices = allServices.filter { service ->
            val matchesSearch = service.title.contains(searchQuery, ignoreCase = true) ||
                               service.description.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategoryId == null || service.category_id == selectedCategoryId
            
            matchesSearch && matchesCategory
        }
    }

    /**
     * Al cambiar el texto de búsqueda
     */
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        applyFilters()
    }

    /**
     * Al seleccionar o deseleccionar una categoría
     */
    fun onCategorySelect(categoryId: String?) {
        selectedCategoryId = if (selectedCategoryId == categoryId) null else categoryId
        applyFilters()
    }
}
