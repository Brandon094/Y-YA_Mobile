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
        extraCost: String,
        onResult: (Boolean) -> Unit
    ) {
        if (title.isBlank() || description.isBlank() || price.isBlank() || categoryId == null) {
            _errorMessage.value = "Completa los campos obligatorios"
            onResult(false)
            return
        }

        val priceVal = price.toDoubleOrNull() ?: 0.0
        val extraCostVal = extraCost.toDoubleOrNull() ?: 0.0

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                    ?: throw Exception("No autenticado")

                val newService = Service(
                    provider_id = user.id,
                    category_id = categoryId,
                    title = title,
                    description = description,
                    price = priceVal,
                    estimated_time = estimatedTime,
                    materials_included = materialsIncluded,
                    extra_cost = extraCostVal,
                    status = "active"
                )

                SupabaseManager.client.postgrest["services"].insert(newService)

                _isLoading.value = false
                onResult(true)
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error: ${e.localizedMessage}"
                onResult(false)
            }
        }
    }
}
