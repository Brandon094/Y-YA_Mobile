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

## 5. Lógica de Roles y Cuenta Universal
YÁYA rompe la fricción tradicional de las plataformas de servicios al permitir que un mismo `id` de perfil contenga las capacidades de ambos roles. 
- La UI se adapta dinámicamente consultando el campo `role` en `public.profiles`.
- Un usuario con rol `provider` puede contratar servicios de otros prestadores sin necesidad de un perfil secundario.
- Esta arquitectura simplifica la gestión de sesiones y la integridad de los datos en Supabase.

## 6. Lógica de Negocio y Validaciones (Hito 1)
El sistema implementa validaciones críticas en el lado del cliente (ViewModel) para asegurar la integridad operativa:
- **Validación de Disponibilidad:** Antes de confirmar una reserva, el sistema consulta la tabla `availability`. Cruza el día de la semana (`day_of_week`) y el rango horario (`start_time`, `end_time`) para habilitar o deshabilitar la contratación.
- **Negociación de Precios:** El campo `final_price` evoluciona dinámicamente durante el flujo de contraofertas, permitiendo que tanto el cliente como el prestador actualicen el valor económico del servicio de forma persistente.

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
