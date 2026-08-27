package com.bhplusplus.yaya.ui.screens.admin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhplusplus.yaya.data.SupabaseManager
import com.bhplusplus.yaya.data.models.Service
import com.bhplusplus.yaya.data.models.UserProfile
import com.bhplusplus.yaya.data.models.Report
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

/**
 * Modelo para resumir reportes por usuario denunciado.
 */
data class ReportedUserSummary(
    val profile: UserProfile?,
    val reports: List<Report>,
    val count: Int = reports.size
)

/**
 * VIEWMODEL PARA EL DASHBOARD ADMINISTRATIVO
 * Gestiona la moderación de servicios y la supervisión de usuarios.
 */
class AdminViewModel : ViewModel() {

    var pendingServices by mutableStateOf<List<Service>>(emptyList())
        private set

    var allProfiles by mutableStateOf<List<UserProfile>>(emptyList())
        private set

    var reports by mutableStateOf<List<Report>>(emptyList())
        private set
    
    // Lista agrupada para detectar infractores
    var reportedUsersSummaries by mutableStateOf<List<ReportedUserSummary>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadAdminData()
    }

    /**
     * Carga datos iniciales: Servicios pendientes y perfiles de usuario.
     */
    fun loadAdminData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Cargar servicios que requieren aprobación con Join del prestador
                pendingServices = SupabaseManager.client.postgrest["services"]
                    .select(Columns.raw("*, provider_profile:provider_id(*)")) {
                        filter {
                            eq("status", "pending_approval")
                        }
                    }
                    .decodeList<Service>()

                // 2. Cargar todos los perfiles para supervisión
                allProfiles = SupabaseManager.client.postgrest["profiles"]
                    .select()
                    .decodeList<UserProfile>()

                // 3. Cargar reportes con Joins para ver quién denuncia a quién
                val reportsResult = SupabaseManager.client.postgrest["reports"]
                    .select(Columns.raw("*, reporter_profile:reporter_id(*), reported_profile:reported_user_id(*)"))
                    .decodeList<Report>()
                
                reports = reportsResult

                // 4. Agrupar reportes por usuario denunciado (Detección de infractores)
                reportedUsersSummaries = reportsResult
                    .groupBy { it.reported_user_id }
                    .map { (_, userReports) ->
                        ReportedUserSummary(
                            profile = userReports.firstOrNull()?.reported,
                            reports = userReports
                        )
                    }
                    .sortedByDescending { it.count }

            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al cargar datos de administración: ${e.message}")
                errorMessage = "Error al conectar con el servidor."
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Aprueba un servicio para que sea visible en el catálogo público.
     */
    fun approveService(serviceId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["services"].update({
                    set("status", "active")
                }) {
                    filter { eq("id", serviceId) }
                }
                loadAdminData() // Refrescar lista
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al aprobar servicio: ${e.message}")
            }
        }
    }

    /**
     * Rechaza un servicio (Lo marca como inactivo).
     */
    fun rejectService(serviceId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["services"].update({
                    set("status", "inactive")
                }) {
                    filter { eq("id", serviceId) }
                }
                loadAdminData() // Refrescar lista
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al rechazar servicio: ${e.message}")
            }
        }
    }

    /**
     * Acciones de sanción para infractores.
     */
    fun suspendUser(userId: String) {
        viewModelScope.launch {
            try {
                // Desactivar sus servicios como medida de suspensión inmediata
                SupabaseManager.client.postgrest["services"].update({
                    set("status", "inactive")
                }) {
                    filter { eq("provider_id", userId) }
                }
                Log.i("AdminVM", "Usuario $userId suspendido: Servicios desactivados.")
                loadAdminData()
            } catch (e: Exception) {
                Log.e("AdminVM", "Error al suspender: ${e.message}")
            }
        }
    }

    fun deleteUserAccount(userId: String) {
        viewModelScope.launch {
            try {
                SupabaseManager.client.postgrest["profiles"].delete {
                    filter { eq("id", userId) }
                }
                loadAdminData()
            } catch (e: Exception) {
                Log.e("AdminVM", "Error al eliminar cuenta: ${e.message}")
            }
        }
    }
}
