package com.bhplusplus.yaya.data

import com.bhplusplus.yaya.data.models.Service

/**
 * REPOSITORIO DE SERVICIOS (ESTÁTICO)
 * Contiene datos predefinidos útiles para previsualizaciones (Previews) y pruebas iniciales.
 * En la versión final, estos datos se reemplazan por las llamadas dinámicas a Supabase.
 */
object ServiceRepository {
    // Lista de servicios de ejemplo con títulos y descripciones realistas
    val services = listOf(
        Service(id = "1", title = "Aseo general del hogar", description = "Limpieza básica del hogar", price = 35000.0, working_days = listOf(1, 2, 3, 4, 5)),
        Service(id = "2", title = "Limpieza profunda", description = "Limpieza detallada de espacios", price = 70000.0, working_days = listOf(6)),
        Service(id = "3", title = "Lavado y planchado de ropa", description = "Cuidado de prendas", price = 45000.0, working_days = listOf(1, 2, 3, 4, 5, 6, 7)),
        Service(id = "4", title = "Cuidado de niños", description = "Atención y supervisión", price = 50000.0, working_days = listOf(1, 2, 3, 4, 5)),
        Service(id = "5", title = "Cuidado de adultos mayores", description = "Asistencia personalizada", price = 60000.0, working_days = listOf(1, 2, 3, 4, 5, 6, 7)),
        Service(id = "6", title = "Preparación de alimentos", description = "Cocina en casa", price = 40000.0, working_days = listOf(6, 7)),
        Service(id = "7", title = "Limpieza de baños", description = "Higiene profunda", price = 30000.0, working_days = listOf(1, 2, 3)),
        Service(id = "8", title = "Cuidado de mascotas", description = "Atención y paseo", price = 25000.0, working_days = listOf(1, 2, 3, 4, 5))
    )

    fun findById(id: String): Service {
        return services.find { it.id == id } ?: services[0]
    }
}
