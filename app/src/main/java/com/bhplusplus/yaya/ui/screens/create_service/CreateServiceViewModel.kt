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
import android.util.Log

/**
 * VIEWMODEL PARA LA CREACIÓN Y EDICIÓN DE SERVICIOS
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
     * Carga los datos de un servicio existente para editar.
     */
    fun loadServiceData(serviceId: String, onLoaded: (Service) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val service = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("id", serviceId) } }
                    .decodeSingle<Service>()
                onLoaded(service)
            } catch (e: Exception) {
                Log.e("CreateServiceVM", "Error al cargar servicio: ${e.message}")
                _errorMessage.value = "Error al cargar los datos del servicio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Crea o actualiza un servicio vinculado al usuario actual.
     */
    fun saveService(
        serviceId: String? = null,
        title: String, 
        description: String, 
        price: String, 
        categoryId: String?, 
        estimatedTime: String,
        workingDays: List<Int>,
        startTime: String,
        endTime: String,
        materialsIncluded: Boolean,
        extraCost: String,
        onResult: (Boolean) -> Unit
    ) {
        if (title.isBlank() || description.isBlank() || price.isBlank() || categoryId == null || workingDays.isEmpty()) {
            _errorMessage.value = "Completa los campos y selecciona al menos un día de trabajo"
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

                val serviceData = Service(
                    id = serviceId,
                    provider_id = user.id,
                    category_id = categoryId,
                    title = title,
                    description = description,
                    price = priceVal,
                    estimated_time = estimatedTime,
                    working_days = workingDays,
                    start_time = startTime,
                    end_time = endTime,
                    materials_included = materialsIncluded,
                    extra_cost = extraCostVal,
                    // Hito 5: Requiere aprobación del admin
                )

                if (serviceId == null) {
                    // Nuevo
                    SupabaseManager.client.postgrest["services"].insert(serviceData)
                } else {
                    // Editar
                    SupabaseManager.client.postgrest["services"].update(
                        {
                            set("title", title)
                            set("description", description)
                            set("price", priceVal)
                            set("category_id", categoryId)
                            set("estimated_time", estimatedTime)
                            set("working_days", workingDays)
                            set("start_time", startTime)
                            set("end_time", endTime)
                            set("materials_included", materialsIncluded)
                            set("extra_cost", extraCostVal)
                        }
                    ) {
                        filter { eq("id", serviceId) }
                    }
                }

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
