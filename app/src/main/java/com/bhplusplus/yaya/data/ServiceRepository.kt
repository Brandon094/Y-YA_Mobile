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
        Service(id = "1", title = "Aseo general del hogar", description = "Limpieza básica del hogar"),
        Service(id = "2", title = "Limpieza profunda", description = "Limpieza detallada de espacios"),
        Service(id = "3", title = "Lavado y planchado de ropa", description = "Cuidado de prendas"),
        Service(id = "4", title = "Cuidado de niños", description = "Atención y supervisión"),
        Service(id = "5", title = "Cuidado de adultos mayores", description = "Asistencia personalizada"),
        Service(id = "6", title = "Preparación de alimentos", description = "Cocina en casa"),
        Service(id = "7", title = "Limpieza de baños", description = "Higiene profunda"),
        Service(id = "8", title = "Cuidado de mascotas", description = "Atención y paseo")
    )

    fun findById(id: String): Service {
        return services.find { it.id == id } ?: services[0]
    }
}
