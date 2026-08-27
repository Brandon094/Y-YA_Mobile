# Documentación Técnica - YÁYA

## 1. Introducción
YÁYA es una aplicación móvil desarrollada para conectar prestadores de servicios locales con clientes. Esta documentación detalla la arquitectura, decisiones técnicas y estándares utilizados en el proyecto.

## 2. Arquitectura de Software
El proyecto sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** recomendado por Google, garantizando una separación clara de responsabilidades y facilitando las pruebas unitarias.

### 2.1. Capas del Proyecto
- **UI (Capa de Presentación):** Implementada con Jetpack Compose. Utiliza el patrón de "Vistas Pasivas" (Passive Views) donde el estado es inyectado desde el ViewModel mediante modelos de **UiState**. La interfaz es elástica, soportando fuentes al 200% y respetando las insets del sistema mediante `navigationBarsPadding`.
- **Data (Capa de Datos):** Gestiona la obtención de datos desde Supabase. Implementa lógica de **Shimmering** (Skeleton Screens) para estados de carga, igualando la estructura visual de los componentes finales para una transición de estados suave y profesional.
- **Navigation & Startup:** Centraliza la lógica de navegación utilizando Type-Safety. Incluye un motor de arranque coordinado entre `MainActivity` y `MainViewModel` para un paso directo desde el Splash Screen nativo a la ruta correspondiente.

## 3. Stack Tecnológico
- **Lenguaje:** Kotlin 2.2.10 con Coroutines y Flow para programación reactiva.
- **UI Framework:** Jetpack Compose (Material 3).
- **Inyección de Dependencias:** Gestión manual mediante ViewModels y `SupabaseManager` (Singleton).
- **Backend:** Supabase 3.6.0 (PostgreSQL, Auth, Realtime, Storage).
- **Networking:** Ktor Client 3.0.3 para peticiones REST.
- **Image Loading:** Coil 3.1.0 para renderizado y caché de multimedia.
- **Formatter Engine:** Punto único de verdad (`FormatterUtils.kt`) para la transformación de datos (Moneda compacta $k/M, Formato de tiempo), garantizando consistencia DRY en toda la aplicación.
- **Serialización:** Kotlinx Serialization 1.7.3.
- **Persistencia de Sesión:** Multiplatform Settings 1.2.0.

## 4. Estándares de Código
- **Naming Conventions:** CamelCase para clases, camelCase para funciones y variables.
- **Modularización:** Actualmente monolítico con paquetes bien definidos.
- **Clean Code:** Funciones pequeñas, principios SOLID y DRY.

## 5. Gestión de Estado
Se utiliza `StateFlow` y estados mutables de Compose dentro de los ViewModels para exponer el estado a la UI de forma segura frente al ciclo de vida.

## 6. Modelos de Datos Críticos
- **Service Availability:** Sistema híbrido que utiliza `working_days` (array) y rangos horarios en la tabla `services`. Se complementa con el Horario Maestro en `public.availability`, validado proactivamente en el flujo de contratación.
- **ServiceRequest:** Modelo evolutivo con soporte para `final_price` y estados de ciclo tripartito: `pending` -> `accepted` -> `in_progress` -> `completed`.
- **Rating:** Modelo para la gestión de reputación. El `MyOrdersViewModel` recupera registros existentes para bloquear re-calificaciones y mostrar el historial inmutable al usuario.
- **Report (Hito 5):** Estructura relacional para la gestión de denuncias por mal comportamiento.
- **ServiceImage (Hito 3):** Tabla dedicada para el almacenamiento de múltiples URLs de portafolio vinculadas a un `service_id`, permitiendo galerías dinámicas.

## 7. Módulo Administrativo
El sistema cuenta con un Dashboard centralizado que permite la moderación proactiva mediante auditoría de servicios y supervisión de usuarios.

## 8. Sistema de Mensajería y Notificaciones
- **Realtime:** Los mensajes de chat se sincronizan mediante suscripciones a canales de Supabase Realtime.
- **Handshake Logic:** El ciclo transaccional requiere confirmación explícita del cliente (`in_progress`) antes de permitir la finalización por el prestador, mitigando disputas comerciales.
- **Push Engine:** Utiliza **Supabase Edge Functions** escritas en TypeScript/Deno para el envío de alertas automáticas vía FCM V1.

## 9. Cumplimiento y Legal
El proyecto incluye un mecanismo de **Account Purge** (Borrado de cuenta) integrado en el perfil del usuario para cumplimiento con las normativas de Google Play y tratamiento de datos personales.

---
*Última actualización: Junio 2026*
