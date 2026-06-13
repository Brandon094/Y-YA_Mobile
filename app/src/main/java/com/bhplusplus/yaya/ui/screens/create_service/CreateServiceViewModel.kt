package com.bhplusplus.yaya.ui.screens.create_service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Category
import com.bhplusplus.yaya.data.models.Service
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * VIEWMODEL PARA LA CREACIÓN DE SERVICIOS
 * Sincronizado con el esquema de base de datos SQL de YÁYA.
 */
class CreateServiceViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Lista de categorías obtenidas de la base de datos
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    init {
        fetchCategories()
    }

    /**
     * Carga las categorías disponibles desde la tabla 'categories'.
     */
    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                categories = SupabaseManager.client.postgrest["categories"]
                    .select()
                    .decodeList<Category>()
            } catch (e: Exception) {
                _errorMessage.value = "No se pudieron cargar las categorías"
            }
        }
    }

    /**
     * Crea un nuevo servicio vinculado al usuario actual y a una categoría.
     */
    fun createService(
        title: String, 
        description: String, 
        price: String, 
        categoryId: String?, 
        estimatedTime: String,
        materialsIncluded: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        // Validaciones previas
        if (title.isBlank() || description.isBlank() || price.isBlank() || categoryId == null) {
            _errorMessage.value = "Por favor, completa todos los campos y selecciona una categoría"
            onResult(false)
            return
        }

        val priceDouble = price.toDoubleOrNull()
        if (priceDouble == null || priceDouble <= 0) {
            _errorMessage.value = "El precio debe ser un número válido"
            onResult(false)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Obtenemos el ID del usuario autenticado (el Prestador)
                val currentUserId = SupabaseManager.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("Debes estar autenticado para publicar un servicio")

                // 2. Creamos el objeto Service según el esquema SQL
                val newService = Service(
                    provider_id = currentUserId,
                    category_id = categoryId,
                    title = title,
                    description = description,
                    price = priceDouble,
                    estimated_time = estimatedTime,
                    materials_included = materialsIncluded,
                    status = "active"
                )

                // 3. Insertamos en Supabase
                SupabaseManager.client.postgrest["services"].insert(newService)

                _isLoading.value = false
                onResult(true)
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al guardar: ${e.localizedMessage}"
                onResult(false)
            }
        }
    }
}
