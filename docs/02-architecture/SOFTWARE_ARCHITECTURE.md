# Arquitectura de Software - YÁYA

Este documento detalla los patrones de diseño, la estructura de módulos y las decisiones arquitectónicas que rigen el desarrollo de YÁYA.

## 1. Patrón Arquitectónico: MVVM (Model-View-ViewModel)
YÁYA implementa **MVVM** para separar la lógica de negocio de la interfaz de usuario, facilitando la mantenibilidad y testeabilidad.

### 1.1. Capa de Vista (UI)
- Construida íntegramente con **Jetpack Compose**.
- Las funciones Composable son "stateless" en la medida de lo posible, recibiendo estados del ViewModel.
- Uso de Material 3 para el sistema de diseño.

### 1.2. Capa de ViewModel
- Actúa como puente entre la Capa de Datos y la UI.
- Gestiona el estado de la pantalla mediante `StateFlow`.
- Sobrevive a cambios de configuración y maneja el ciclo de vida de las Coroutines.

### 1.3. Capa de Datos (Data Layer)
- **Repositories:** Actualmente, se cuenta con `ServiceRepository` para proporcionar datos estáticos destinados a *Previews* y pruebas rápidas.
- **DataSources (Supabase):** La lógica de persistencia dinámica reside directamente en los ViewModels, los cuales interactúan con el cliente global de Supabase (`SupabaseManager.client`) para realizar operaciones CRUD y consultas relacionales complejas mediante Postgrest.
- **Multimedia Storage:** Integración con **Supabase Storage** para la gestión de activos binarios (imágenes). Se utilizan buckets específicos (`avatars`, `portfolios`) con políticas de acceso público para lectura y restringido para escritura.

## 2. Flujo de Datos (Reactive Stream)
El flujo de información es unidireccional (UDF - Unidirectional Data Flow):
1. El usuario realiza una acción en la **UI**.
2. El **ViewModel** recibe la acción y llama al **Repository**.
3. El **Repository** interactúa con **Supabase** y devuelve un `Flow`.
4. El **ViewModel** procesa el resultado y actualiza el `StateFlow`.
5. La **UI** observa el cambio y se recompone automáticamente.

## 3. Navegación Type-Safe
Utilizamos el sistema de navegación basado en tipos de Kotlin (Navigation Compose 2.8+):
- Las rutas se definen como objetos `@Serializable`.
- Se evitan los errores de tipado manual en los strings de las rutas.
- Los argumentos se pasan de forma segura entre pantallas.

## 4. Gestión de Dependencias
Se utiliza un enfoque de **Inyección de Dependencias** (DI) para desacoplar los componentes.
- Componentes clave como `SupabaseClient` se inicializan a nivel de aplicación.
- Los ViewModels reciben sus repositorios mediante sus constructores.
- **Gestión de Imágenes:** Se integra **Coil 3** como el motor principal de carga y caché de imágenes, permitiendo la visualización eficiente de avatares y portafolios desde URLs remotas.
- **Patrones UX Multimedia:** Implementación de **HorizontalPager** de Jetpack Compose para carruseles inmersivos y visores de pantalla completa con navegación gestual.

## 5. Lógica de Roles y Cuenta Universal
YÁYA rompe la fricción tradicional de las plataformas de servicios al permitir que un mismo `id` de perfil contenga las capacidades de ambos roles. 
- La UI se adapta dinámicamente consultando el campo `role` en `public.profiles`.
- Un usuario con rol `provider` puede contratar servicios de otros prestadores sin necesidad de un perfil secundario.
- Esta arquitectura simplifica la gestión de sesiones y la integridad de los datos en Supabase.

## 6. Lógica de Negocio y Reactividad
El sistema implementa validaciones críticas y procesos reactivos:
- **Validación de Disponibilidad:** Cruce de horarios en tiempo real contra los campos `working_days`, `start_time` y `end_time` en la tabla `services` (disponibilidad granular) y la tabla `availability` (horario global del prestador).
- **Chat en Tiempo Real:** Uso de `Supabase Realtime` (Postgres Changes) para sincronizar mensajes sin latencia perceptible.
- **Automatización via Edge Functions:** Uso de funciones en el servidor (Deno) disparadas por Webhooks para tareas asíncronas como el envío de notificaciones push vía Firebase.

## 7. Sistema de Notificaciones (Push Architecture)
YÁYA utiliza una arquitectura híbrida para alertas:
1. **Registro:** El cliente Android genera un token FCM y lo sincroniza con `public.profiles`.
2. **Evento:** Un `INSERT` en la tabla `requests` activa un Webhook.
3. **Procesamiento:** Una `Edge Function` en Supabase recibe el evento, se autentica con Google Cloud y envía el mensaje a Firebase.
4. **Entrega:** Firebase entrega la notificación al dispositivo destino.

## 7. Manejo de Errores y Estados Globales
Cada pantalla implementa un modelo de estado robusto:
```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

---
*Documento de Arquitectura por BH++*
