# Documentación Técnica - YÁYA

## 1. Introducción
YÁYA es una aplicación móvil desarrollada para conectar prestadores de servicios locales con clientes. Esta documentación detalla la arquitectura, decisiones técnicas y estándares utilizados en el proyecto.

## 2. Arquitectura de Software
El proyecto sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** recomendado por Google, garantizando una separación clara de responsabilidades y facilitando las pruebas unitarias.

### 2.1. Capas del Proyecto
- **UI (Capa de Presentación):** Implementada con Jetpack Compose. Utiliza el patrón de "Vistas Tontas" donde el estado es inyectado desde el ViewModel. Incluye sellos de branding corporativo ("Powered by BH++") integrados al pie de las pantallas de acceso y un sistema de **Onboarding interactivo** mediante carruseles.
- **Data (Capa de Datos):** Gestiona la lógica de obtención de datos desde Supabase o almacenamiento local. Incluye Repositorios y Modelos.
- **Navigation & Startup:** Centraliza la lógica de navegación utilizando Type-Safety. Incluye un motor de arranque coordinado entre `MainActivity` y `MainViewModel` para un paso directo desde el Splash Screen nativo a la ruta correspondiente.

## 3. Stack Tecnológico
- **Lenguaje:** Kotlin 2.2.10 con Coroutines y Flow para programación reactiva.
- **UI Framework:** Jetpack Compose (Material 3) con Compose Compiler 2.2.10.
- **Inyección de Dependencias:** Gestión manual mediante ViewModels y `SupabaseManager` (Singleton).
- **Backend:** Supabase 3.6.0 (PostgreSQL, Auth, Realtime, Storage).
- **Networking:** Ktor Client 3.0.3 para peticiones REST.
- **Image Loading:** Coil 3.1.0 para renderizado y caché de multimedia.
- **Image Viewer:** Implementación de diálogos personalizados con HorizontalPager para visualización de portafolios a pantalla completa y navegación gestual.
- **Serialización:** Kotlinx Serialization 1.7.3.
- **Persistencia de Sesión:** Multiplatform Settings 1.2.0.

## 4. Estándares de Código
- **Naming Conventions:** CamelCase para clases, camelCase para funciones y variables.
- **Modularización:** Actualmente monolítico con paquetes bien definidos, con potencial de escalar a módulos Gradle por funcionalidad.
- **Clean Code:** Funciones pequeñas, principios SOLID y DRY.

## 5. Gestión de Estado
Se utiliza `StateFlow` y `SharedFlow` dentro de los ViewModels para exponer el estado a la UI de forma segura frente al ciclo de vida.

## 6. Modelos de Datos Críticos
- **Service Availability:** Sistema híbrido que utiliza `working_days` (array de enteros) y rangos horarios (`start_time`, `end_time`) directamente en la tabla `services` para una disponibilidad granular por talento. Se mantiene la tabla `availability` para horarios globales del prestador.
- **ServiceRequest:** Evolucionado para incluir `final_price`, permitiendo la persistencia del acuerdo económico tras la negociación. Soporta estados de ciclo de vida completo (`pending`, `accepted`, `completed`).
- **Rating:** Modelo para la gestión de reputación. Incluye lógica de persistencia para `ratingScore` y `ratingComment` dentro del `MyOrderUiState` para visualización persistente y bloqueo de re-calificación.
- **Report (Hito 5):** Estructura relacional para la gestión de denuncias por mal comportamiento.
- **ServiceImage (Hito 3):** Tabla dedicada para el almacenamiento de múltiples URLs de portafolio vinculadas a un `service_id`, permitiendo galerías dinámicas.

## 7. Módulo Administrativo
El sistema cuenta con un Dashboard centralizado que permite la moderación proactiva mediante auditoría de servicios y supervisión de usuarios.
- **Identidad Visual:** Integración de Avatares mediante Coil 3 en todas las vistas administrativas para facilitar el reconocimiento de perfiles.
- **Relaciones de Datos:** Uso de consultas asíncronas con embebido de perfiles (Joins) para reducir la carga de red.

## 8. Sistema de Mensajería y Notificaciones
- **Realtime:** Los mensajes de chat se sincronizan mediante suscripciones a canales de Supabase Realtime.
- **Feedback de Chat:** Implementación de `ChatSummary` para procesar historiales masivos, extrayendo el último mensaje, contadores de no leídos y marcas de tiempo, con ordenamiento automático por actividad reciente.
- **Push Engine:** Utiliza **Supabase Edge Functions** escritas en TypeScript/Deno.
    - **Disparador:** Webhooks sobre las tablas `requests` (`INSERT`/`UPDATE`) y `messages` (`INSERT`).
    - **Lógica de Negocio:** La función detecta el destinatario (ya sea por cambio de precio o por mensaje nuevo) y recupera su `fcm_token`.
    - **FCM V1:** Autenticación mediante JWT de Google Cloud para el envío de notificaciones de alta prioridad con el icono de marca.

## 9. Cumplimiento y Legal
El proyecto incluye una sección dedicada (`docs/04-legal`) con los lineamientos de privacidad y uso de la plataforma, alineados con el tratamiento de datos personales y la intermediación de servicios.

---
*Última actualización: Junio 2026*
