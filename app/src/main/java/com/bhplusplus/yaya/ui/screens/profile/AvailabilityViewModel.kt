package com.bhplusplus.yaya.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Availability
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * Estado de UI para un día de la semana.
 */
data class DayAvailabilityState(
    val dayOfWeek: Int,
    val isWorking: Boolean = false,
    val startTime: String = "08:00:00",
    val endTime: String = "17:00:00",
    val originalId: String? = null
)

/**
 * VIEWMODEL PARA LA GESTIÓN DE DISPONIBILIDAD GLOBAL DEL PRESTADOR
 */
class AvailabilityViewModel : ViewModel() {

    var daysState by mutableStateOf<List<DayAvailabilityState>>(
        (1..7).map { DayAvailabilityState(it) }
    )
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    init {
        loadAvailability()
    }

    fun loadAvailability() {
        val userId = SupabaseManager.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val result = SupabaseManager.client.postgrest["availability"]
                    .select { filter { eq("provider_id", userId) } }
                    .decodeList<Availability>()

                val newState = (1..7).map { day ->
                    val found = result.find { it.day_of_week == day }
                    if (found != null) {
                        DayAvailabilityState(
                            dayOfWeek = day,
                            isWorking = true,
                            startTime = found.start_time,
                            endTime = found.end_time,
                            originalId = found.id
                        )
                    } else {
                        DayAvailabilityState(day)
                    }
                }
                daysState = newState
            } catch (e: Exception) {
                Log.e("AvailabilityVM", "Error al cargar disponibilidad: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleDay(dayOfWeek: Int) {
        daysState = daysState.map {
            if (it.dayOfWeek == dayOfWeek) it.copy(isWorking = !it.isWorking) else it
        }
    }

    fun updateTimes(dayOfWeek: Int, start: String, end: String) {
        daysState = daysState.map {
            if (it.dayOfWeek == dayOfWeek) it.copy(startTime = start, endTime = end) else it
        }
    }

    fun saveAvailability(onSuccess: () -> Unit) {
        val userId = SupabaseManager.client.auth.currentUserOrNull()?.id ?: return
        viewModelScope.launch {
            isSaving = true
            try {
                // 1. Identificar qué días guardar
                val itemsToSave = daysState.filter { it.isWorking }.map {
                    Availability(
                        id = it.originalId,
                        provider_id = userId,
                        day_of_week = it.dayOfWeek,
                        start_time = it.startTime,
                        end_time = it.endTime
                    )
                }

                // 2. Identificar qué días borrar (los que estaban marcados como working y ya no)
                val idsToDelete = daysState.filter { !it.isWorking && it.originalId != null }
                    .map { it.originalId!! }

                // Operación en Supabase: Borramos los inactivos
                if (idsToDelete.isNotEmpty()) {
                    SupabaseManager.client.postgrest["availability"].delete {
                        filter { isIn("id", idsToDelete) }
                    }
                }

                // Upsert de los activos
                if (itemsToSave.isNotEmpty()) {
                    SupabaseManager.client.postgrest["availability"].upsert(itemsToSave)
                }

                message = "Horario guardado correctamente."
                onSuccess()
            } catch (e: Exception) {
                Log.e("AvailabilityVM", "Error al guardar: ${e.message}")
                message = "Error al guardar los cambios."
            } finally {
                isSaving = false
            }
        }
    }
}
