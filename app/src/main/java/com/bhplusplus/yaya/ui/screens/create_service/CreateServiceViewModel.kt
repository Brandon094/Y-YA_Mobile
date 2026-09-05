package com.bhplusplus.yaya.ui.screens.create_service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Category
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.ServiceImage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import android.util.Log

/**
 * VIEWMODEL PARA LA CREACIÓN Y EDICIÓN DE SERVICIOS
 * Sincronizado con el esquema de base de datos SQL de YÁYA.
 */
class CreateServiceViewModel : ViewModel() {

    // NAVEGACIÓN Y ESTADOS DEL FORMULARIO WIZARD (MVVM)
    var currentStep by mutableIntStateOf(1)
        private set

    var estimatedTimeNumber by mutableStateOf("2")
    var estimatedTimeUnit by mutableStateOf("Horas")
    val timeUnits = listOf("Minutos", "Horas", "Días", "Meses", "Años")

    val combinedEstimatedTime: String
        get() = "$estimatedTimeNumber $estimatedTimeUnit"

    fun goToStep(step: Int) {
        if (step in 1..2) {
            currentStep = step
        }
    }

    /**
     * Parsea la duración almacenada en Supabase (ej. "2 Horas") a número y unidad.
     */
    fun parseEstimatedTime(rawTime: String?) {
        val str = rawTime ?: ""
        if (str.isNotBlank() && str.contains(" ")) {
            estimatedTimeNumber = str.substringBefore(" ")
            estimatedTimeUnit = str.substringAfter(" ")
        } else if (str.isNotBlank()) {
            estimatedTimeNumber = str.filter { it.isDigit() }.ifBlank { "2" }
            estimatedTimeUnit = "Horas"
        }
    }

    /**
     * Valida si la información del Paso 1 está lista para avanzar al Paso 2.
     */
    fun isStep1Valid(title: String, description: String, categoryId: String?, municipality: String): Boolean {
        return title.isNotBlank() && description.isNotBlank() && categoryId != null && municipality.isNotBlank()
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Lista de categorías obtenidas de la base de datos
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    // Imágenes seleccionadas para el portafolio (ByteArray para subir)
    var selectedImages by mutableStateOf<List<ByteArray>>(emptyList())

    // Jornada maestra configurada en public.availability
    var masterWorkingDays by mutableStateOf<List<Int>>(emptyList())
        private set

    var masterAvailabilityList by mutableStateOf<List<com.bhplusplus.yaya.data.models.Availability>>(emptyList())
        private set

    // Días ocupados por OTROS servicios del mismo prestador (Map: dayNumber -> serviceTitle)
    var occupiedDaysByOtherServices by mutableStateOf<Map<Int, String>>(emptyMap())
        private set

    var existingServicesList by mutableStateOf<List<Service>>(emptyList())
        private set

    init {
        fetchCategories()
        loadProviderAvailabilityAndServices()
    }

    /**
     * Carga la jornada maestra del prestador y sus otros servicios activos
     * para advertir sobre días ocupados o sugerir su horario laboral.
     */
    fun loadProviderAvailabilityAndServices(currentEditingServiceId: String? = null) {
        viewModelScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull() ?: return@launch

                // 1. Cargar disponibilidad maestra desde public.availability
                val availabilityList = SupabaseManager.client.postgrest["availability"]
                    .select { filter { eq("provider_id", user.id) } }
                    .decodeList<com.bhplusplus.yaya.data.models.Availability>()

                masterAvailabilityList = availabilityList
                masterWorkingDays = availabilityList.map { it.day_of_week }.sorted()

                // 2. Cargar otros servicios del prestador
                val existingServices = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("provider_id", user.id) } }
                    .decodeList<Service>()

                existingServicesList = existingServices

                val occupiedMap = mutableMapOf<Int, String>()
                existingServices.forEach { existing ->
                    if (existing.id != currentEditingServiceId) {
                        val startShort = if (existing.start_time.length >= 5) existing.start_time.substring(0, 5) else existing.start_time
                        val endShort = if (existing.end_time.length >= 5) existing.end_time.substring(0, 5) else existing.end_time
                        val timeRange = if (startShort.isNotEmpty() && endShort.isNotEmpty()) " ($startShort - $endShort)" else ""
                        val serviceInfo = "${existing.title}$timeRange"

                        existing.working_days.forEach { day ->
                            val current = occupiedMap[day]
                            occupiedMap[day] = if (current != null) "$current | $serviceInfo" else serviceInfo
                        }
                    }
                }
                occupiedDaysByOtherServices = occupiedMap
            } catch (e: Exception) {
                Log.w("CreateServiceVM", "Info: No se cargó disponibilidad previa: ${e.message}")
            }
        }
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
                parseEstimatedTime(service.estimated_time)
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
     * Valida de manera reactiva todos los campos del formulario antes de guardar.
     * Retorna null si la información es válida, o un mensaje de error descriptivo si hay algún fallo.
     */
    fun validateServiceData(
        title: String,
        price: String,
        categoryId: String?,
        workingDays: List<Int>,
        startTime: String,
        endTime: String,
        serviceId: String? = null
    ): String? {
        if (title.isBlank()) return "Ingresa el título del servicio"
        if (categoryId == null) return "Selecciona una categoría de talento"
        if (price.isBlank() || (price.toDoubleOrNull() ?: 0.0) <= 0) return "Ingresa un precio base válido"
        if (workingDays.isEmpty()) return "Selecciona al menos un día de prestación"

        val parseTime = { t: String ->
            val shortStr = if (t.length >= 5) t.substring(0, 5) else t
            java.time.LocalTime.parse(shortStr)
        }
        val serviceStart = try { parseTime(startTime) } catch (_: Exception) { null }
        val serviceEnd = try { parseTime(endTime) } catch (_: Exception) { null }

        if (serviceStart == null || serviceEnd == null || !serviceStart.isBefore(serviceEnd)) {
            return "La hora de inicio debe ser anterior a la hora de fin"
        }

        val dayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

        // 1. Validar que los días y horas estén dentro de la jornada maestra
        if (masterWorkingDays.isNotEmpty()) {
            for (day in workingDays) {
                if (!masterWorkingDays.contains(day)) {
                    return "El día ${dayNames[day - 1]} no forma parte de tu jornada maestra"
                }
                val master = masterAvailabilityList.find { it.day_of_week == day }
                if (master != null) {
                    val masterStart = try { parseTime(master.start_time) } catch (_: Exception) { null }
                    val masterEnd = try { parseTime(master.end_time) } catch (_: Exception) { null }
                    if (masterStart != null && masterEnd != null) {
                        if (serviceStart.isBefore(masterStart) || serviceEnd.isAfter(masterEnd)) {
                            val mStart = master.start_time.take(5)
                            val mEnd = master.end_time.take(5)
                            return "El horario para ${dayNames[day - 1]} debe estar dentro de tu jornada maestra ($mStart - $mEnd)"
                        }
                    }
                }
            }
        }

        // 2. Validar que no se traslape con otros servicios del mismo prestador
        for (existing in existingServicesList) {
            if (existing.id != serviceId) {
                val commonDays = workingDays.intersect(existing.working_days.toSet())
                if (commonDays.isNotEmpty()) {
                    val isOverlap = com.bhplusplus.yaya.utils.ValidationUtils.isTimeRangeOverlapping(
                        startTime, endTime,
                        existing.start_time, existing.end_time
                    )
                    if (isOverlap) {
                        val firstDay = commonDays.first()
                        val exStart = existing.start_time.take(5)
                        val exEnd = existing.end_time.take(5)
                        return "Conflicto el ${dayNames[firstDay - 1]}: Ya tienes '${existing.title}' de $exStart a $exEnd"
                    }
                }
            }
        }

        return null
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
        municipality: String = "La Plata",
        onResult: (Boolean) -> Unit
    ) {
        if (title.isBlank() || description.isBlank() || price.isBlank() || categoryId == null || workingDays.isEmpty()) {
            _errorMessage.value = "Completa los campos y selecciona al menos un día de trabajo"
            onResult(false)
            return
        }

        // 1. Validar que hora inicio < hora fin
        val parseTime = { t: String ->
            val shortStr = if (t.length >= 5) t.substring(0, 5) else t
            java.time.LocalTime.parse(shortStr)
        }
        val serviceStart = try { parseTime(startTime) } catch (_: Exception) { null }
        val serviceEnd = try { parseTime(endTime) } catch (_: Exception) { null }

        if (serviceStart == null || serviceEnd == null || !serviceStart.isBefore(serviceEnd)) {
            _errorMessage.value = "La hora de inicio debe ser anterior a la hora de fin."
            onResult(false)
            return
        }

        val dayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

        // 2. Validar que esté dentro de la jornada maestra si el prestador la tiene configurada
        if (masterAvailabilityList.isNotEmpty()) {
            for (day in workingDays) {
                val master = masterAvailabilityList.find { it.day_of_week == day }
                if (master == null) {
                    _errorMessage.value = "El día ${dayNames[day - 1]} no está incluido en tu jornada maestra de trabajo."
                    onResult(false)
                    return
                } else {
                    val masterStart = try { parseTime(master.start_time) } catch (_: Exception) { null }
                    val masterEnd = try { parseTime(master.end_time) } catch (_: Exception) { null }
                    if (masterStart != null && masterEnd != null) {
                        if (serviceStart.isBefore(masterStart) || serviceEnd.isAfter(masterEnd)) {
                            val masterStartShort = master.start_time.take(5)
                            val masterEndShort = master.end_time.take(5)
                            _errorMessage.value = "El horario para el ${dayNames[day - 1]} debe estar dentro de tu jornada maestra ($masterStartShort - $masterEndShort)."
                            onResult(false)
                            return
                        }
                    }
                }
            }
        }

        // 3. Validar que no se cruce con el horario de OTROS servicios del mismo prestador
        for (existing in existingServicesList) {
            if (existing.id != serviceId) {
                val commonDays = workingDays.intersect(existing.working_days.toSet())
                if (commonDays.isNotEmpty()) {
                    val isOverlap = com.bhplusplus.yaya.utils.ValidationUtils.isTimeRangeOverlapping(
                        startTime, endTime,
                        existing.start_time, existing.end_time
                    )
                    if (isOverlap) {
                        val firstCommonDay = commonDays.first()
                        val existingStartShort = existing.start_time.take(5)
                        val existingEndShort = existing.end_time.take(5)
                        _errorMessage.value = "Conflicto de horario el ${dayNames[firstCommonDay - 1]}: Ya tienes el servicio '${existing.title}' de $existingStartShort a $existingEndShort."
                        onResult(false)
                        return
                    }
                }
            }
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
                    municipality = municipality
                )

                val finalServiceId: String

                if (serviceId == null) {
                    // Nuevo - Insertamos y recuperamos el ID generado
                    val insertedService = SupabaseManager.client.postgrest["services"]
                        .insert(serviceData) {
                            select()
                        }
                        .decodeSingle<Service>()
                    finalServiceId = insertedService.id ?: throw Exception("Error al obtener ID")
                } else {
                    // Editar
                    finalServiceId = serviceId
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
                            set("municipality", municipality)
                        }
                    ) {
                        filter { eq("id", serviceId) }
                    }
                }

                // SUBIR IMÁGENES DEL PORTAFOLIO
                if (selectedImages.isNotEmpty()) {
                    selectedImages.forEachIndexed { index, bytes ->
                        val fileName = "${finalServiceId}_img_$index.jpg"
                        val url = SupabaseManager.uploadImage("portfolios", fileName, bytes)
                        
                        // Guardar referencia en la tabla service_images
                        val serviceImage = ServiceImage(
                            service_id = finalServiceId,
                            image_url = url
                        )
                        SupabaseManager.client.postgrest["service_images"].insert(serviceImage)
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
