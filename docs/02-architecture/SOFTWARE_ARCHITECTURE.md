# Arquitectura de Software - YÁYA

Este documento detalla los patrones de diseño, la estructura de módulos y las decisiones arquitectónicas que rigen el desarrollo de YÁYA.

## 1. Patrón Arquitectónico: MVVM (Model-View-ViewModel)
YÁYA implementa **MVVM** para separar la lógica de negocio de la interfaz de usuario, facilitando la mantenibilidad y testeabilidad.

### 1.1. Capa de Vista (UI)
- Construida íntegramente con **Jetpack Compose**.
- Las funciones Composable son "**stateless**" y "**tontas**" (Dumb Components), limitándose a renderizar datos procesados y banderas booleanas entregadas por el ViewModel mediante modelos de **UiState** dedicados (ej. `MyOrderUiState`, `MessageUiState`, `ContratacionUiState`).
- **Inyección de Estados:** El ViewModel mapea los modelos de dominio (Supabase) a estados de UI simplificados, encargándose del formateo de monedas (estilo compacto **$ 50k**), fechas y lógica de visibilidad condicional mediante el principio **DRY** (utilizando `FormatterUtils`).
- Uso de Material 3 para el sistema de diseño, con soporte estricto para temas Claro y Oscuro.
- **Flujo de Onboarding:** Implementación de un carrusel interactivo mediante `HorizontalPager` para comunicar la propuesta de valor.
- **Pull-to-Refresh:** Implementación del patrón de refresco manual mediante `PullToRefreshBox` en todas las listas principales.
- **Feedback de Carga (Shimmers):** Uso de **Skeleton Screens** (Shimmer Effects) personalizados que replican la estructura exacta de cada pantalla (Home, Detalles, Pedidos) para mejorar la percepción de fluidez.
- **Accesibilidad y Adaptabilidad:** 
    - Uso estricto de unidades `sp` para tipografía y `dp` para layouts.
    - Implementación de contenedores flexibles con `FlowRow`, `weights` dinámicos y `sizeIn` para soportar fuentes de hasta el **200%** sin desbordes.
- **Gestión de Teclado (IME):** Uso de `Modifier.imePadding()` y `contentWindowInsets` en Scaffolds para evitar solapamientos.
- **Edge-to-Edge:** Aplicación de `navigationBarsPadding()` en todos los elementos anclados al borde inferior para compatibilidad con botones de navegación del sistema.

### 1.2. Capa de ViewModel
- Actúa como el **Cerebro de Negocio** de cada pantalla.
- Responsable del parsing de datos, formateo de fechas, cálculo de estados de flujo y mapeo a objetos de **UI State**.
- Gestiona el estado de la pantalla mediante `StateFlow` o estados mutables de Compose (`mutableStateOf`) y suscripciones reactivas.
- **MainViewModel:** Controla el ciclo de vida del arranque, validando la sesión antes de liberar el Splash Screen nativo.
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

## 3. Navegación Type-Safe y Flujo de Arranque
Utilizamos el sistema de navegación basado en tipos de Kotlin (Navigation Compose 2.8+):
- **Motor de Arranque Directo (Zero-Flicker):** La aplicación utiliza la API oficial de Splash Screen sincronizada con `MainViewModel`. La navegación no se inicializa hasta que se confirma la ruta destino (`Home`, `Admin` o `Welcome`), eliminando parpadeos o pantallas intermedias.
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
- **Acceso Híbrido Admin:** El administrador aterriza en el `HomeRoute` como un usuario estándar, permitiéndole interactuar con el ecosistema (contratar, chatear). El acceso al `AdminDashboardRoute` se realiza mediante una sección protegida en el perfil del usuario.
- La UI se adapta dinámicamente consultando el campo `role` en `public.profiles`.
- Un usuario con rol `provider` puede contratar servicios de otros prestadores sin necesidad de un perfil secundario.

## 6. Lógica de Negocio y Reactividad
El sistema implementa validaciones críticas y procesos reactivos:
- **Reactividad en Tiempo Real (Realtime):** Uso de `Supabase Realtime` para sincronizar estados de pedidos, contraofertas y mensajes de chat de forma instantánea.
- **Triple Validación de Disponibilidad:** El flujo de contratación bloquea solicitudes mediante un cruce triple:
    1. **Horario Maestro:** Validación contra `public.availability` (agenda del prestador).
    2. **Días del Servicio:** Validación contra `working_days` en `services`.
    3. **Rango Horario:** Validación contra las horas de inicio/fin del servicio específico.
- **Flujo de Negociación "Handshake" (Doble Confirmación):** Implementación de un ciclo transaccional seguro y explícito:
    - `Solicitud/Contraoferta` -> `Acuerdo de Precio` (Estado: `accepted`) -> `Confirmar Inicio` (Handshake del cliente - Estado: `in_progress`) -> `Trabajo en Curso` -> `Finalización` (por el prestador - Estado: `completed`).
    - Este mecanismo garantiza que el trabajo solo pueda finalizarse si ambas partes confirmaron el inicio bajo el precio acordado, mitigando disputas comerciales.
- **Moderación y Sanciones Progresivas:** El sistema de reportes implementa una lógica de agrupamiento por infractor (`ReportedUserSummary`). El Dashboard administrativo calcula en tiempo real el nivel de severidad (Llamado de atención, Suspensión, Eliminación) basado en umbrales de reincidencia (3 y 5 reportes), permitiendo acciones correctivas masivas.
- **Chat Avanzado:** Mensajería con `reverseLayout`, previsualización de último mensaje y gestión de no leídos procesada 100% en el ViewModel.

## 7. Sistema de Notificaciones (Push Architecture)
YÁYA utiliza una arquitectura híbrida para alertas en tiempo real mediante un **Motor Unificado de Notificaciones**:
1. **Registro:** El cliente Android genera un token FCM y lo sincroniza con `public.profiles`.
2. **Eventos de Disparo (Webhooks):**
    - **Tabla `requests` (INSERT/UPDATE):** Nuevas solicitudes, cambios de precio (Contraofertas) o cambios en el estado (`accepted`, `cancelled`).
    - **Tabla `messages` (INSERT):** Mensajes de chat entrantes.
3. **Procesamiento Server-Side:** 
    - Una **Edge Function unificada** en Supabase recibe el payload.
    - Identifica dinámicamente al destinatario (`provider_id`, `client_id` o `receiver_id`).
    - Personaliza el mensaje (ej. incluye el nombre del remitente en el chat).
4. **Entrega:** La función se autentica con la API V1 de Firebase y entrega la notificación de alta prioridad al dispositivo destino.

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
