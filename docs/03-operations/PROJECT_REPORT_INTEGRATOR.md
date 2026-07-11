# Informe Técnico: Taller Integrador de Codificación
**Proyecto:** YÁYA (v0.1.0-alpha)  
**Desarrollador:** Brandon Daza  
**Organización:** BH++ Team  
**Fecha:** 17 de Junio de 2026

---

## Actividad 1. Diagnóstico del Proyecto Actual

### 1. Información General
 Campo | Detalle |
 :--- | :--- |
 **Nombre del Proyecto** | YÁYA (Conecta. Confía. Contrata.) |
 **Problema que resuelve** | Informalidad y falta de seguridad en la contratación de servicios locales independientes. |
 **Objetivo General** | Desarrollar un ecosistema móvil multi-rol para la gestión y negociación de servicios. |

### 2. Especificaciones Técnicas
- **Lenguaje:** Kotlin 2.2.10 (K2 Compiler)
- **UI Framework:** Jetpack Compose (Material 3)
- **Backend:** Supabase (PostgreSQL, Auth, Realtime)
- **Arquitectura:** MVVM con Flujo de Datos Unidireccional (UDF)
- **Navegación:** Jetpack Navigation con Type-Safety (Seguridad de tipos)

**Estructura de Base de Datos (ERD):**

![Diagrama Entidad Relación](../assets/DiagramER.png)
### 3. Estado de Módulos
- **Desarrollados:** Auth (Login/Registro), Perfil Universal, Catálogo Dinámico, Módulo de Negociación (Contraofertas), Gestión de Servicios (CRUD), Validación de Disponibilidad, Dashboard Administrativo, Sistema de Reportes de Comportamiento y Sistema de Calificaciones (Hito 2/5).
- **Pendientes:** Chat en tiempo real (Hito 2), Multimedia/Storage (Hito 3).

---

## Actividad 2. Análisis de Código Fuente
**Módulo Seleccionado:** Gestión de Solicitudes y Negociación (`IncomingRequests`)

### Definición Técnica
- **Función:** Administrar el ciclo de vida transaccional de una solicitud de servicio.
- **ViewModel (Controlador):** `IncomingRequestsViewModel.kt`. Gestiona estados reactivos mediante `mutableStateOf`.
- **Modelos:** `ServiceRequest.kt`, `UserProfile.kt`.
- **Tablas:** `public.requests`, `public.services`, `public.profiles`.

### Consulta SQL Avanzada (Postgrest Join)
Para optimizar el rendimiento, se utiliza una sola consulta relacional en lugar de múltiples llamadas asíncronas, reduciendo la latencia de red:
```kotlin
val result = SupabaseManager.client.postgrest["requests"]
    .select(Columns.raw("*, services!inner(*), profiles:client_id(*)")) {
        filter { eq("services.provider_id", userId) }
    }
```

**Evidencia de Implementación (Join Complejo):**
![Join Supabase](../assets/fecthIncommingRequest.png)

### Validaciones e Interfaz Reactiva
1. **Seguridad de Roles:** Control de acceso basado en el perfil del usuario.
2. **Componentes UI Dinámicos:** Uso de `StatusBadge` para representar visualmente el ciclo de vida de la solicitud.

**Evidencia de Interfaz (StatusBadge):**
![Status Badge UI](../assets/StatusBadge.png)

---

## Actividad 3. Desarrollo de un CRUD Completo
**Módulo:** Gestión de Servicios Publicados (`MyServices`)

Este módulo implementa el ciclo de vida completo de los datos (Create, Read, Update, Delete) vinculados al perfil del prestador:

**Evidencia de Lógica CRUD (Create/Update):**
![Lógica de Persistencia](../assets/SaveService.png)

**Evidencia de Interfaz (Read/LazyList):**
![LazyColumn Servicios](../assets/LazyColumn.png)

---

## Actividad 4. Corrección de Errores (Bitácora)

| # | Error Identificado | Solución Técnica |
| :--- | :--- | :--- |
| 1 | Pérdida de Sesión | Implementación de `SettingsSessionManager` con persistencia local. |
| 2 | Disponibilidad Ciega | Validación proactiva contra tabla `availability`. |
| 3 | Fricción de Roles | Arquitectura de Perfil Único Multi-rol en PostgreSQL. |
| 4 | Metadata Inexistente | Lógica de recuperación de perfil desde metadatos de Auth. |
| 5 | Navegación Insegura | Migración de String routes a objetos `@Serializable`. |
| 6 | Redirección Retrasada | Centralización de lógica de roles para navegación inmediata tras Login. |
| 7 | UX Silenciosa | Implementación de `EmptyServicesView` para feedback en búsquedas vacías. |

**Evidencia de Corrección (Persistencia de Sesión):**
![Persistencia Supabase](../assets/SupaBase.png)

**Evidencia de Lógica de Negocio (Disponibilidad):**
![Lógica Disponibilidad](../assets/checkAvailability.png)

---

## Actividad 5. Optimización del Código

### 1. Navegación Type-Safe
Migración completa de rutas basadas en Strings (inseguras) a objetos serializables, garantizando errores en tiempo de compilación en lugar de ejecución.

**Evidencia de Rutas Seguras:**
![Definición de Rutas](../assets/DefinicionRutas.png)

**Evidencia de Implementación NavHost:**
![NavHost Implementation](../assets/NavHost.png)

---

## Actividad 6. Defensa Técnica Individual

- **Gestión de Sesiones:** Se utiliza un patrón Singleton (`SupabaseManager`) con persistencia en `SharedPreferences` para garantizar un inicio de sesión continuo.
- **Seguridad:** Los datos están protegidos por políticas **RLS (Row Level Security)** en el motor de base de datos PostgreSQL.
- **Arquitectura:** El uso de MVVM permite que la lógica de negocio sea independiente de la UI, facilitando el mantenimiento y la escalabilidad del proyecto YÁYA.

---
Documento generado bajo estándares de ingeniería de software por **BH++ Team**
