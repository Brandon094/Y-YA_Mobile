# Documentación Técnica - YÁYA

## 1. Introducción
YÁYA es una aplicación móvil desarrollada para conectar prestadores de servicios locales con clientes. Esta documentación detalla la arquitectura, decisiones técnicas y estándares utilizados en el proyecto.

## 2. Arquitectura de Software
El proyecto sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** recomendado por Google, garantizando una separación clara de responsabilidades y facilitando las pruebas unitarias.

### 2.1. Capas del Proyecto
- **UI (Capa de Presentación):** Implementada con Jetpack Compose. Contiene los `Screens` y sus respectivos `ViewModels`.
- **Data (Capa de Datos):** Gestiona la lógica de obtención de datos desde Supabase o almacenamiento local. Incluye Repositorios y Modelos.
- **Navigation:** Centraliza la lógica de navegación utilizando Type-Safety para evitar errores en tiempo de ejecución.

## 3. Stack Tecnológico
- **Lenguaje:** Kotlin 2.2.10 con Coroutines y Flow para programación reactiva.
- **UI Framework:** Jetpack Compose (Material 3) con Compose Compiler 2.2.10.
- **Inyección de Dependencias:** Gestión manual mediante ViewModels y `SupabaseManager` (Singleton).
- **Backend:** Supabase 3.6.0 (PostgreSQL, Auth, Realtime).
- **Networking:** Ktor Client 3.0.3 para peticiones REST.
- **Serialización:** Kotlinx Serialization 1.7.3.
- **Persistencia de Sesión:** Multiplatform Settings 1.2.0.

## 4. Estándares de Código
- **Naming Conventions:** CamelCase para clases, camelCase para funciones y variables.
- **Modularización:** Actualmente monolítico con paquetes bien definidos, con potencial de escalar a módulos Gradle por funcionalidad.
- **Clean Code:** Funciones pequeñas, principios SOLID y DRY.

## 5. Gestión de Estado
Se utiliza `StateFlow` y `SharedFlow` dentro de los ViewModels para exponer el estado a la UI de forma segura frente al ciclo de vida.

## 6. Modelos de Datos Críticos
- **Availability:** Mapea la disponibilidad semanal de los prestadores para validación en tiempo real.
- **ServiceRequest:** Evolucionado para incluir `final_price`, permitiendo la persistencia del acuerdo económico tras la negociación.
- **Report (Hito 5):** Estructura relacional para la gestión de denuncias por mal comportamiento.

## 7. Módulo Administrativo
El sistema cuenta con un Dashboard centralizado que permite la moderación proactiva mediante:
- **Auditoría de Servicios:** Interfaz para aprobar talento nuevo antes de su publicación en el catálogo.
- **Supervisión de Usuarios:** Acceso a la base de datos de perfiles y denuncias activas para garantizar la seguridad del ecosistema.

---
*Última actualización: Junio 2026*
