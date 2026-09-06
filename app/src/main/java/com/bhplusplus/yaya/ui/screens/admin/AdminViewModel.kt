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
import com.bhplusplus.yaya.data.models.Message
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    var suspendedUserIds by mutableStateOf<Set<String>>(emptySet())
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

                // Cargar servicios para detectar usuarios suspendidos
                try {
                    val allServices = SupabaseManager.client.postgrest["services"]
                        .select()
                        .decodeList<Service>()

                    val servicesByProvider = allServices.groupBy { it.provider_id }
                    val detectedSuspended = servicesByProvider.filter { (_, services) ->
                        services.isNotEmpty() && services.all { it.status == "inactive" }
                    }.keys.filterNotNull().toSet()

                    suspendedUserIds = suspendedUserIds + detectedSuspended
                } catch (e: Exception) {
                    Log.w("AdminVM", "Info al detectar suspendidos: ${e.message}")
                }

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
     * Acciones de sanción y rehabilitación para usuarios.
     */
    fun suspendUser(userId: String) {
        // 1. Actualización optimista en memoria para CERO recarga/parpadeo de la lista
        allProfiles = allProfiles.map { profile ->
            if (profile.id == userId) profile.copy(is_suspended = true) else profile
        }

        viewModelScope.launch {
            try {
                // 2. Persistir suspensión en public.profiles
                SupabaseManager.client.postgrest["profiles"].update({
                    set("is_suspended", true)
                }) {
                    filter { eq("id", userId) }
                }

                // 3. Desactivar sus servicios
                SupabaseManager.client.postgrest["services"].update({
                    set("status", "inactive")
                }) {
                    filter { eq("provider_id", userId) }
                }

                suspendedUserIds = suspendedUserIds + userId
                Log.i("AdminVM", "Usuario $userId suspendido en BD exitosamente.")
            } catch (e: Exception) {
                Log.e("AdminVM", "Error al suspender en BD: ${e.message}")
            }
        }
    }

    fun reactivateUser(userId: String) {
        // 1. Actualización optimista en memoria para CERO recarga/parpadeo de la lista
        allProfiles = allProfiles.map { profile ->
            if (profile.id == userId) profile.copy(is_suspended = false) else profile
        }

        viewModelScope.launch {
            try {
                // 2. Persistir reactivación en public.profiles
                SupabaseManager.client.postgrest["profiles"].update({
                    set("is_suspended", false)
                }) {
                    filter { eq("id", userId) }
                }

                // 3. Reactivar sus servicios
                SupabaseManager.client.postgrest["services"].update({
                    set("status", "active")
                }) {
                    filter { eq("provider_id", userId) }
                }

                suspendedUserIds = suspendedUserIds - userId
                Log.i("AdminVM", "Usuario $userId reactivado en BD exitosamente.")
            } catch (e: Exception) {
                Log.e("AdminVM", "Error al reactivar en BD: ${e.message}")
            }
        }
    }

    fun deleteUserAccount(userId: String) {
        viewModelScope.launch {
            try {
                // INTENTO 1: Invocación a función RPC atómica almacenada en Postgres (SECURITY DEFINER)
                SupabaseManager.client.postgrest.rpc(
                    function = "admin_delete_user_account",
                    parameters = buildJsonObject {
                        put("target_user_id", userId)
                    }
                )
                Log.i("AdminVM", "Usuario $userId eliminado exitosamente vía RPC en Postgres.")
                loadAdminData()
            } catch (rpcError: Exception) {
                Log.w("AdminVM", "RPC no disponible, ejecutando borrado secuencial: ${rpcError.message}")
                deleteUserAccountSequential(userId)
            }
        }
    }

    private suspend fun deleteUserAccountSequential(userId: String) {
        try {
            // 1. Ratings
            try {
                SupabaseManager.client.postgrest["ratings"].delete { filter { eq("client_id", userId) } }
                SupabaseManager.client.postgrest["ratings"].delete { filter { eq("provider_id", userId) } }
            } catch (e: Exception) { Log.w("AdminVM", "Ratings: ${e.message}") }

            // 2. Requests
            try {
                val userServices = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("provider_id", userId) } }
                    .decodeList<Service>()

                userServices.forEach { s ->
                    s.id?.let { sId ->
                        SupabaseManager.client.postgrest["requests"].delete { filter { eq("service_id", sId) } }
                    }
                }
                SupabaseManager.client.postgrest["requests"].delete { filter { eq("client_id", userId) } }
            } catch (e: Exception) { Log.w("AdminVM", "Requests: ${e.message}") }

            // 3. Messages
            try {
                SupabaseManager.client.postgrest["messages"].delete { filter { eq("sender_id", userId) } }
                SupabaseManager.client.postgrest["messages"].delete { filter { eq("receiver_id", userId) } }
            } catch (e: Exception) { Log.w("AdminVM", "Messages: ${e.message}") }

            // 4. Reports
            try {
                SupabaseManager.client.postgrest["reports"].delete { filter { eq("reporter_id", userId) } }
                SupabaseManager.client.postgrest["reports"].delete { filter { eq("reported_user_id", userId) } }
            } catch (e: Exception) { Log.w("AdminVM", "Reports: ${e.message}") }

            // 5. Services & Images
            try {
                val userServices = SupabaseManager.client.postgrest["services"]
                    .select { filter { eq("provider_id", userId) } }
                    .decodeList<Service>()

                userServices.forEach { s ->
                    s.id?.let { sId ->
                        SupabaseManager.client.postgrest["service_images"].delete { filter { eq("service_id", sId) } }
                    }
                }
                SupabaseManager.client.postgrest["services"].delete { filter { eq("provider_id", userId) } }
            } catch (e: Exception) { Log.w("AdminVM", "Services: ${e.message}") }

            // 6. Availability
            try {
                SupabaseManager.client.postgrest["availability"].delete { filter { eq("provider_id", userId) } }
            } catch (e: Exception) { Log.w("AdminVM", "Availability: ${e.message}") }

            // 7. Profile
            SupabaseManager.client.postgrest["profiles"].delete { filter { eq("id", userId) } }

            Log.i("AdminVM", "Usuario $userId y registros asociados eliminados secuencialmente.")
            loadAdminData()
        } catch (e: Exception) {
            Log.e("AdminVM", "Error fatal al eliminar cuenta: ${e.message}", e)
            errorMessage = "No se pudo eliminar el usuario: ${e.localizedMessage}"
        }
    }

    /**
     * Envía un mensaje automático de advertencia al usuario.
     */
    fun warnUser(userId: String, reportsCount: Int) {
        viewModelScope.launch {
            try {
                val adminId = SupabaseManager.client.auth.currentUserOrNull()?.id ?: return@launch
                val message = Message(
                    sender_id = adminId,
                    receiver_id = userId,
                    content = """
                        🚩 NOTIFICACIÓN OFICIAL DE MODERACIÓN
                        
                        Hola. Hemos detectado que tu perfil ha acumulado $reportsCount reportes por parte de la comunidad. 
                        
                        Este es un llamado de atención preventivo. Te invitamos a revisar nuestras normas de convivencia y asegurar que tus servicios cumplan con la calidad y respeto que YÁYA exige.
                        
                        ⚠️ IMPORTANTE: La reincidencia en comportamientos reportables resultará en la SUSPENSIÓN de tus servicios o la ELIMINACIÓN PERMANENTE de tu cuenta.
                        
                        Atentamente,
                        Equipo de Moderación YÁYA.
                    """.trimIndent()
                )
                
                SupabaseManager.client.postgrest["messages"].insert(message)
                Log.i("AdminVM", "Llamado de atención enviado a $userId")
                
                // Opcional: Podrías marcar el reporte como 'atendido' si tuvieras esa columna
            } catch (e: Exception) {
                Log.e("AdminVM", "Error al enviar advertencia: ${e.message}")
            }
        }
    }
}
