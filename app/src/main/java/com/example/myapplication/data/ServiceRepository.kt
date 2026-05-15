package com.example.myapplication.data

object ServiceRepository {
    // Datos hardcodeados de ejemplo
    val services = listOf(
        Service(title = "Aseo general del hogar", description = "Limpieza básica del hogar"),
        Service(title = "Limpieza profunda", description = "Limpieza detallada de espacios"),
        Service(title = "Lavado y planchado de ropa", description = "Cuidado de prendas"),
        Service(title = "Cuidado de niños", description = "Atención y supervisión"),
        Service(title = "Cuidado de adultos mayores", description = "Asistencia personalizada"),
        Service(title = "Preparación de alimentos", description = "Cocina en casa"),
        Service(title = "Limpieza de baños", description = "Higiene profunda"),
        Service(title = "Cuidado de mascotas", description = "Atención y paseo")
    )
}
