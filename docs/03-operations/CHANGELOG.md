# Changelog - YÁYA

Todas las modificaciones notables en este proyecto serán documentadas en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-09-03
### Añadido
- **Motor Centralizado de Validaciones de Datos (`ValidationUtils.kt`):**
    - Implementación del componente `ValidationUtils.kt` aplicando el principio DRY y la arquitectura MVVM para validación integral de entrada de usuarios en toda la app.
    - Soporte para nombres alfabéticos (sin números ni caracteres especiales no permitidos), documento de identidad DNI/CC (6 a 12 dígitos), teléfono móvil de exactamente 10 dígitos, correo electrónico en formato RFC/Patterns y contraseña segura (mín. 8 caracteres combinando mayúscula, minúscula y número o símbolo).
    - Validación de fechas de nacimiento no futuras con restricción en UI (`SelectableDates`), agendamiento de citas con dirección válida (mín. 5 caracteres), restricción de días pasados, fecha no transcurrida y hora no pasada para citas del mismo día.
    - Extensión del átomo `YayaTextField` con soporte para mensajes de error contextuales (`errorMessage`) en `RegisterUserScreen`, `EditProfileScreen`, `PantallaContratacion`, `LoginScreen` y `ResetPasswordScreen`.
- **Estrategia de Filtrado Geográfico por Municipio/Zona (`municipality`):**
    - Definición de la lista inmutable y estandarizada de municipios del departamento del Huila en `ValidationUtils.HUILA_MUNICIPALITIES` (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante).
    - Reemplazo de campos de texto libre por controles desplegables inmutables `ExposedDropdownMenuBox` en `RegisterUserScreen`, `EditProfileScreen` y `CreateServiceScreen`, garantizando la captura limpia de la ubicación de atención y cobertura sin errores de tipeo.
    - Sincronización del diálogo modal de filtro geográfico en `HomeScreen` consumiendo la fuente de datos unificada de `ValidationUtils.HUILA_MUNICIPALITIES`.
    - Incorporación del campo opcional `municipality: String?` ("La Plata" por defecto) en los modelos de datos de dominio `UserProfile` y `Service`.
    - Chip de Selección de Municipio interactivo en `HomeTopBar` y lógica de filtrado dinámico en `HomeViewModel.applyFilters()` (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante, Todos).
- **Módulo de Visualización de Reputación y Reseñas en Perfil del Prestador (`ProfileViewModel`, `ProfileHeroHeader`, `ProfileScreen`, `ProfileOptionItem`):**
    - Consulta y cálculo dinámico de puntuación promedio (`averageRating`), total de evaluaciones (`totalRatings`) y lista ordenada de opiniones (`providerRatings`) mediante Postgrest sobre la tabla `public.ratings` para usuarios con rol `provider` o `admin`.
    - Incorporación del indicador gráfico `RatingIndicator` en la cabecera Hero del perfil (`ProfileHeroHeader`) directamente alineado con el badge de rol.
    - Integración de la opción *"Mi Reputación y Reseñas"* en la sección *"MI TALENTO"* del perfil con soporte para badges textuales con resumen de estrellas (`badgeText`, ej: `⭐ 4.9 (18)` o `"Sin opiniones"`) en `ProfileOptionItem`.
    - Despliegue de modal de hoja inferior (`ModalBottomSheet`) en `ProfileScreen` para examinar el listado completo de reseñas recibidas mediante componentes atómicos `YayaRatingItem`, con feedback para estado sin calificaciones.
- **Redirección Automática de Onboarding para Prestadores (Post-Registro):**
    - Redirección automática de usuarios recién registrados con rol `provider` hacia la pantalla de configuración de Jornada Maestra (`AvailabilityScreen`), garantizando la parametrización de rangos de disponibilidad base (`public.availability`) antes de la creación y oferta de servicios.
- **Carga Inteligente de Disponibilidad y Detector de Cruces (`CreateServiceScreen` & `CreateServiceViewModel`):**
    - Action button *"Cargar mi jornada maestra"* que puebla de forma instantánea el listado de días laborables (`working_days`) utilizando la configuración general almacenada en `public.availability`.
    - Algoritmo de detección de ocupación en `CreateServiceViewModel.loadProviderAvailabilityAndServices()` que consulta servicios previos del prestador e identifica la distribución de días ya asignados a otros servicios (`occupiedDaysByOtherServices`).
    - Destacado visual en `CreateServiceScreen` con círculos de días ocupados resaltados en tono de advertencia (`errorContainer`) y notificación informativa contextual que explicita los días, nombres de los servicios asignados y sus rangos horarios activos (ej: `Desarrollo de aplicaciones móviles (08:00 - 18:00)`) para visibilidad de horas libres y prevención de solapamientos y traslapes de agenda.
    - Validaciones estrictas de horarios y conformidad con Jornada Maestra: verificación de secuencia horaria (`startTime < endTime`), conformidad con la disponibilidad maestra (`masterStart` - `masterEnd`) y evaluación de solapamiento de horarios entre servicios del mismo prestador (`ValidationUtils.isTimeRangeOverlapping`).
    - Deshabilitación visual (`alpha = 0.3f`) y desacoplamiento de clics en el selector de días (`FlowRow`) para días fuera de la jornada maestra (`masterWorkingDays`), con evaluación reactiva `currentValidationError` y deshabilitación dinámica del botón de guardado ante inconsistencias.
- **Migración de Base de Datos Supabase (DDL):**
    - Definición de scripts SQL para adicionar la columna `municipality` a las tablas `public.profiles` y `public.services` con valor predeterminado `'La Plata'`.
- **Visor del Manual de Uso Integrado con Segregación Estricta por Rol y Estilo Formal Sin Emojis (`ManualConstants.kt`, `AppNavigation.kt`, `LegalViewerScreen`, `USER_MANUAL.md`, `ADMIN_MANUAL.md`):**
    - Implementación de la función `ManualConstants.getManualContentForRole(role)` en `ManualConstants.kt` que retorna dinámicamente el manual personalizado según el rol del usuario autenticado (`CLIENT_ROLE_MANUAL_CONTENT`, `PROVIDER_ROLE_MANUAL_CONTENT`, `ADMIN_ROLE_MANUAL_CONTENT`).
    - Lógica de enrutamiento dinámico por rol en `UserManualRoute` de `AppNavigation.kt`: consulta asíncrona de `activeRole` desde el perfil Postgrest (`profiles.role`) con fallback a `userMetadata["role"]`, pasando a `LegalViewerScreen` títulos adaptativos ("Manual para Clientes", "Manual para Prestadores", "Manual Maestro de YÁYA") y el contenido correspondiente.
    - Adopción de estilo formal, ejecutivo y técnico (estilo legal Markdown) con erradicación total de emojis en todos los textos de manuales (`ManualConstants.kt`, `USER_MANUAL.md` y `ADMIN_MANUAL.md`) para cumplir con los estándares de documentación formal de BH++ Team.
    - Reescritura integral de los manuales oficiales físicos en `docs/03-operations/manuals/USER_MANUAL.md` y `ADMIN_MANUAL.md` detallando formalmente facultades ("Lo que PUEDE hacer") y restricciones ("Lo que NO PUEDE hacer") para Cliente, Prestador y Administrador.
- **Rediseño Limpio, Modular y Estructurado (ProfileScreen 2.0):**
    - **Hero Header 2.0:** Integración de un botón flotante de lápiz/editar (`IconButton Icons.Default.Edit`) en la esquina superior derecha del encabezado, eliminando la necesidad de un botón largo en la lista.
    - **Tarjetas de Acceso Rápido (Quick Action Cards):** Fila de 3 tarjetas compactas en grid para prestadores y administradores (*Mis Servicios*, *Solicitudes* con badge flotante en rojo de pendientes, y *Reputación* con calificación ⭐ 4.9).
    - **Navegación por Pestañas Segmentadas (`TabRow`):** Organización modular de las 14 opciones en 2 pestañas limpias:
        - *Pestaña 1 ("💼 Mi Operación"):* Operatividad diaria (Horario de trabajo, servicios publicados, solicitudes, mis pedidos, mensajes y panel admin).
        - *Pestaña 2 ("⚙️ Ajustes y Ayuda"):* Seguridad y soporte (Cambio de clave, Manual de uso, Términos y Condiciones, Política de Privacidad, Borrado de cuenta y Cerrar sesión).
- **Confirmación Atómica de Cierre de Sesión en Barra Inferior (`HomeScreen` & `YayaConfirmationDialog`):**
    - Integración del diálogo modal atómico de confirmación `YayaConfirmationDialog` en el botón de cerrar sesión de la barra de navegación inferior de `HomeScreen`.
    - Prevención del cierre de sesión accidental solicitando la confirmación activa e intencional del usuario antes de desautenticar la sesión.
- **Modernización y Reestructuración Interactiva del Portal Web de Manuales (`portal_web/manuales.html`):**
    - Implementación del selector interactivo de pestañas por rol (`switchRoleTab`) en JavaScript para consultar de forma segregada los manuales de Cliente, Prestador y Administrador.
    - Incorporación de matrices atómicas de permisos con bloques visuales destacados en verde ("LO QUE PUEDE HACER EL USUARIO") y rojo ("LO QUE NO PUEDE HACER EL USUARIO") para los tres roles del sistema.
    - Adopción de estilo formal técnico sin emojis, con soporte de Tema Dual (Modo Claro / Modo Oscuro) e Interfaz Elástica Responsive, plenamente sincronizada con la versión v1.1.0 de la App (cobertura por municipio, jornada maestra, detección inteligente de traslapes horarios, reputación de talentos y semáforo de moderación disciplinario).
- **Optimización de Firebase Hosting (`firebase.json`) y Despliegue en Producción del Portal Web (v1.1.0):**
    - Configuración de `cleanUrls: true` con `public: "portal_web"` en `firebase.json` sin rewrites de SPA, garantizando soporte nativo para arquitectura multi-página estática y URLs limpias (acceso directo a `/manuales` a partir de `portal_web/manuales.html`).
    - Confirmación del despliegue en producción de 13 archivos estáticos en la infraestructura de Firebase Hosting (Google Cloud Infrastructure).
    - Despliegue verificado en producción en el Dominio Principal ([https://y-ya-d5929.web.app](https://y-ya-d5929.web.app)) y en el Portal Web de Manuales ([https://y-ya-d5929.web.app/manuales](https://y-ya-d5929.web.app/manuales)).

### Cambiado
- **Armonización y Unificación Total del Tema Oscuro en el Portal Web (`#0F172A` / `#1E293B`):**
    - Estandarización de la paleta nocturna Deep Midnight `darkBg: '#0F172A'` (Slate 900) y `darkSurface: '#1E293B'` (Slate 800) en el 100% de las páginas del portal web (`index.html`, `tecnica.html`, `manuales.html`, `terminos.html`, `privacidad.html`, `eliminar-cuenta.html` y `js/components.js`), reemplazando el fondo negro genérico (`#121212` / `#1E1E1E`).
    - Garantía de 100% de coherencia visual e identidad de marca entre la App móvil Android v1.1.0 y el Portal Web en producción.
    - Actualización de la insignia de versión en el footer unificado en `js/components.js` a `v1.1.0 Stable (versionCode 5)`.
- **Rediseño del Selector de Rol (`RegisterUserScreen`):**
    - Reemplazo de los RadioButton sueltos por Tarjetas Atómicas de Selección (`Surface` / `OutlinedCard`) con borde en color primario (`MaterialTheme.colorScheme.primary`) al ser seleccionadas, mejorando el área táctil y la jerarquía visual del selector de rol.
- **Alineación Semántica de Calificaciones en `ServiceCard` (`RatingIndicator`):**
    - Reubicación visual de la estrella e indicador promedio de calificación (`RatingIndicator`) hacia la cabecera del componente `ServiceCard`, situándolo directamente debajo del nombre del prestador (`state.domain.provider?.full_name`).
    - Alineación semántica directa con la tabla SQL `public.ratings` (que califica al prestador / `provider_id` y su reputación general de talento), eliminando cualquier confusión previa donde la estrella aparentaba ser una calificación del servicio individual en lugar del prestador.
    - Reorganización visual de la categoría del servicio y la disponibilidad de días en el pie de página de la tarjeta (`ServiceCard`) para un layout limpio e intuitivo.
- **Incremento de Versión y Etiquetado Oficial Git:**
    - Actualización de versión oficial a `versionName = "1.1.0"` y `versionCode = 5` en `app/build.gradle.kts` para despliegue en Google Play Store.
    - Etiquetado oficial del Release Tag `v1.1.0` en Git para GitHub con el mensaje `"Lanzamiento Oficial YÁYA v1.1.0 (versionCode 5)"`, consolidando el Hito de Versión Oficial en Producción / Play Store Ready (`versionCode = 5`, `versionName = "1.1.0"`).
- **Unificación y Fusión Oficial a Producción (`desarrollo` ➔ `produccion`):**
    - Fusión y sincronización exitosa de la rama `desarrollo` hacia la rama principal de producción `produccion` (`origin/produccion`) en Git/GitHub, consolidando oficialmente todo el trabajo acumulado de la versión `v1.1.0` (`versionCode = 5`).
    - Impacto total del release consolidado: 63 archivos actualizados con +3101 inserciones y -770 eliminaciones en el repositorio oficial.

### Corregido
- **Resolución de Error "Malformed root json" en CI/CD (`codeql.yml`):**
    - Corrección de fallos en el análisis CodeQL de GitHub Actions causados cuando el secreto `secrets.GOOGLE_SERVICES_JSON` estaba vacío, malformado o codificado en Base64.
    - Implementación de un algoritmo de autodetección y decodificación automática para formatos Base64 y JSON plano.
    - Inyección automatizada de un archivo `app/google-services.json` de respaldo (*fallback*) con paquete `com.bhplusplus.yaya` para asegurar que `./gradlew assembleDebug` ejecute sin fallos en builds de CI/CD.
    - Integración de validación previa mediante `jq empty` para garantizar 100% de integridad y conformidad sintáctica JSON antes de compilar.
- **Optimización de Legibilidad y Contraste en Tema Oscuro (`LoginScreen` & `RegisterUserScreen`):**
    - Corrección de la visibilidad del enlace *"¿Olvidaste tu contraseña?"* en `LoginScreen` mediante la sustitución del color estático `secondary` (`NavyBlue` `#1E2A38`) por `MaterialTheme.colorScheme.primary` (`RedPrimary` con `FontWeight.Bold`), eliminando el contraste deficiente sobre fondos oscuros (Deep Midnight).
    - Ajuste de contraste en `RegisterUserScreen` aplicando explícitamente `color = MaterialTheme.colorScheme.onBackground` en las etiquetas de selección de rol (*"Quiero contratar un servicio"*, *"Quiero ofrecer un talento"*) y en la fila de consentimiento legal (`LegalConsentRow`), garantizando la adaptabilidad dinámica del texto (blanco/slate en Tema Oscuro y oscuro en Tema Claro).
- **Solución a Condición de Carrera en Inicialización de Municipio (`HomeViewModel.loadData`):**
    - Corrección de condición de carrera en la secuencia de carga inicial de `HomeViewModel.loadData()`, donde `applyFilters()` se ejecutaba previamente a la recuperación del perfil de usuario (`userProfile`), provocando que la vista inicial se filtrara por la ubicación por defecto ("La Plata").
    - Reorganización de la ejecución asíncrona para asegurar la descarga del perfil del usuario e inicializar `selectedMunicipality` con su municipio real **antes** de invocar `applyFilters()`, garantizando la presentación inmediata de los servicios filtrados por la ubicación real del usuario sin requerir refresco manual.
- **Corrección de Error de Columna `id` y `day_of_week` Nulos (`Availability.kt` & `AvailabilityViewModel`):**
    - Corrección del error `null value in column "id" violates not-null constraint` al guardar la disponibilidad en Supabase PostgreSQL, mediante la generación proactiva de UUIDs de cliente (`java.util.UUID.randomUUID()`) previa a la operación de `upsert`.
    - Eliminación de valores por defecto en las propiedades de `Availability.kt` (`day_of_week`, `provider_id`, `start_time`, `end_time`) para evitar que `kotlinx.serialization` omita propiedades en la codificación JSON cuando coinciden con su valor por defecto, garantizando la transmisión explícita de `day_of_week` y eliminando el error `null value in column "day_of_week" violates not-null constraint`.
    - Normalización de las cadenas de horario al formato de 8 caracteres (`"HH:mm:ss"`) para compatibilidad estricta con el tipo de dato SQL `time without time zone`, e incorporación de contenedor visual de error en `AvailabilityScreen`.
- **Prevención de Traslapes y Solapamientos Horarios entre Servicios:**
    - Verificación estricta mediante `ValidationUtils.isTimeRangeOverlapping` para bloquear la creación o edición de servicios cuyos rangos horarios colisionen con otros servicios activos del mismo prestador en días compartidos.
- **Desplazamiento Suave en Selector Modal de Municipios (`HomeScreen`):**
    - Corrección del diálogo `AlertDialog` de selección de municipio en `HomeScreen`, sustituyendo el contenedor rígido `Column` por `LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp))`.
    - Solución a la imposibilidad de seleccionar municipios ubicados al final de la lista (ej. Neiva, Pitalito, Todos los municipios) en pantallas de dimensiones reducidas debido al desbordamiento sin scroll, garantizando desplazamiento vertical suave y acceso al 100% de las opciones de cobertura.
- **Manejo Defensivo de Existencia de Perfil Previo a Reservas (`requests_client_id_fkey` en `ContratacionViewModel`):**
    - Corrección del fallo de clave foránea PostgreSQL `Code: 23503` (`Details: Key is not present in table "profiles"`) al intentar crear una solicitud de servicio (`insert` en tabla `requests`).
    - Implementación de la función atómica `ensureProfileExists(user)` dentro de `ContratacionViewModel.contratar()` que verifica y crea automáticamente el registro del cliente en `public.profiles` con su metadata de Auth (`full_name`, `role`, `phone`, `address`, `municipality`) antes de procesar el agendamiento, garantizando un flujo de contratación 100% resiliente y libre de errores 23503.

## [1.0.1] - 2026-09-02
### Corregido
- **Deserialización Segura de Datos Supabase (KotlinX Serialization):**
    - Corrección de `MissingFieldException` al deserializar respuestas JSON de consultas con proyecciones relacionales parciales (`Columns.raw("id, services!inner(provider_id)")`) en `ProfileViewModel` y `HomeViewModel`.
    - Asignación de valores por defecto en los modelos de datos (`ServiceRequest`, `UserProfile`, `Message`, `Rating`, `Report`, `ServiceImage`, `Availability`, `Category`) asegurando tolerancia ante campos omitidos o nulables en las consultas de Supabase Postgrest.
- **Estabilidad de Canales Supabase Realtime:**
    - Corrección de `IllegalStateException: You cannot call postgresChangeFlow after joining the channel` al reinicializar la vista de chat o refrescar datos en ViewModels.
    - Validación proactiva del estado `RealtimeChannel.Status.UNSUBSCRIBED` antes de registrar escuchas `postgresChangeFlow` en `ChatViewModel`, `ChatListViewModel`, `HomeViewModel`, `IncomingRequestsViewModel`, `MyOrdersViewModel`, `MyServicesViewModel` y `ProfileViewModel`.
- **Estabilidad de UI/Layout (Jetpack Compose):**
    - Corrección de `IllegalStateException` producida por componentes scrolleables medidos con restricciones de altura infinita (conflictos de medición entre `LazyColumn` y `Column` con `verticalScroll`).
    - Ajustes y refactorización estructural en `NegotiationHistoryBox.kt`, `ConfirmacionScreen.kt` y `MyServicesScreen.kt`.

### Añadido
- **Motor Centralizado de Validaciones de Datos (ValidationUtils):**
    - Implementación del componente `ValidationUtils.kt` aplicando el principio DRY y la arquitectura MVVM para validación integral de entrada de usuarios en toda la app.
    - **Nombres y Apellidos:** Validación alfabética estricta sin números ni caracteres especiales no permitidos.
    - **Documento de Identidad:** Formato exclusivamente numérico de 6 a 12 dígitos.
    - **Teléfono Móvil:** Validación numérica exacta de 10 dígitos.
    - **Correo Electrónico:** Validación de estructura RFC/Patterns.
    - **Contraseña Segura:** Mínimo 8 caracteres con combinación obligatoria de mayúsculas, minúsculas y números o símbolos.
    - **Fecha de Nacimiento:** Bloqueo de días futuros en el calendario UI (`SelectableDates`) y validación de fecha cronológica no futura (`isValidBirthDate`).
    - **Agendamiento de Citas (Contratación):** Validación de dirección de atención (mín. 5 caracteres), bloqueo UI de días pasados (`SelectableDates`), verificación de fecha no pasada (`isValidFutureDate`) y validación de hora no transcurrida para citas del mismo día (`isValidScheduleTime`).
    - Extensión del átomo `YayaTextField` con soporte para mensajes de error contextuales (`errorMessage`) en `RegisterUserScreen`, `EditProfileScreen`, `PantallaContratacion`, `LoginScreen` y `ResetPasswordScreen`.
- **Cumplimiento y Publicación en Google Play Store:**
    - Declaración del permiso `com.google.android.gms.permission.AD_ID` en `AndroidManifest.xml` para cumplimiento de las políticas de identificador de publicidad y analítica de Google Play.
    - Inclusión de símbolos de depuración nativos en nivel `FULL` (`ndk { debugSymbolLevel = 'FULL' }`) en `app/build.gradle.kts` para análisis de crashes nativos y desofuscación en Play Console.
    - Configuración de versión `versionCode 5` (`versionName "1.1.0"`) en `app/build.gradle.kts`.

### Cambiado
- **Infraestructura CI/CD y Automatización de Calidad:**
    - Implementación de **Advanced CodeQL Analysis** en GitHub Actions (`.github/workflows/codeql.yml`) con soporte para Java 17 y Kotlin (`java-kotlin`) y reglas `security-extended,security-and-quality`.
    - Inyección segura de `google-services.json` mediante secretos de GitHub Actions y formato heredoc en los pipelines de integración continua.

## [1.0.0] - 2026-08-28
### Añadido
- **Versión de Lanzamiento Oficial (SENA Gold Edition):**
    - Consolidación de todo el ecosistema digital: App Android + Portal Web + Infraestructura Cloud.
    - **Security & Stability Patch:** Actualización masiva de dependencias a sus versiones estables más recientes (Patch 2026).
    - Mitigación masiva de vulnerabilidades reportadas por Dependabot (v0.1.3 -> v1.0.0), incluyendo parches críticos para:
        - **Netty Stack (v4.1.138.Final):** DoS, SslHandler Crash, ByteBuf Leak, MadeYouReset DDoS, IPv6 Filter Bypass, Bzip2 Infinite Loop, Decompression Bomb, Request Smuggling, OOM en HttpPostRequestDecoder y WebSocket validation.
        - **Apache HttpClient (v4.5.14):** CVE-2020-13956 (Authority Component misinterpretation).
        - **Bouncy Castle (v1.85.2):** Reuso de keystream (CVE-2024-34447), algoritmos riesgosos e inyección LDAP.
        - **Guava (v33.7.1-jre):** Uso inseguro de directorio temporal e información divulgada.
        - **Apache Commons Lang (v3.20.0):** Recursión incontrolada.
        - **jose4j (v0.9.6):** DoS via JWE.
    - Soporte para **Android API 37** (Next-Gen Readiness), **Java 17** y **Gradle 9.5**.
    - **Ecosistema de Calidad y CI/CD:**
        - Implementación de **Advanced CodeQL Analysis** mediante GitHub Actions para monitoreo continuo de seguridad y calidad.
        - Configuración de **Secret Injections** para inyectar `google-services.json` en tiempo de ejecución, manteniendo la seguridad de las API Keys.
    - **Fix de Compilación:** Actualización de las librerías `androidx.fragment` and `androidx.activity` a versiones superiores a 1.3.0 para asegurar la compatibilidad con `registerForActivityResult`.
- **Evolución del Ciclo de Vida Admin:**
    - Los administradores ahora aterrizan en la pantalla de Inicio (`Home`) igual que cualquier usuario, permitiéndoles interactuar con la app (contratar, chatear, etc.).
    - Se implementó un acceso exclusivo al **Panel Administrativo** desde el perfil del usuario, visible únicamente para el rol `admin`.
    - Habilitación de la **Navegación de Retorno (Back)** en el Dashboard Admin para una integración fluida con el resto de la aplicación.
- **Sistema de Sanciones Progresivas:**
    - Rediseño de la gestión de reportes mediante agrupamiento por infractor (`ReportedUserSummary`).
    - Implementación de **Semáforo de Severidad**: Amarillo (1-2), Naranja (3-4) y Rojo (5+ reportes) con etiquetas dinámicas de recomendación de sanción.
    - **Llamado de Atención Automático:** Nueva función para enviar mensajes de advertencia pre-diseñados a los infractores desde el Panel Admin, promoviendo la convivencia sin escalar a suspensiones inmediatas.
    - Nuevas acciones masivas: Suspender (desactivación inmediata de servicios del prestador) y Eliminación de cuenta directa desde el resumen de reportes.
- **Flujo de Negociación "Handshake" (Doble Confirmación):**
    - Implementación de un ciclo de seguridad tripartito: Negociación -> Acuerdo (`accepted`) -> Confirmación de Inicio (`in_progress`) -> Finalización (`completed`).
    - El cliente ahora tiene el poder de dar el visto bueno final al precio antes de iniciar el servicio.
    - Se corrigió la visibilidad de acciones en el estado `in_progress`, garantizando la continuidad del flujo hasta la calificación.
- **Rediseño Maestro de Home Screen:**
    - Nueva barra superior con saludo dinámico e identidad visual Premium.
    - Barra de búsqueda integrada y selector de categorías con diseño moderno.
    - **Tarjetas de Servicio 2.0:** Avatares con badges de categoría superpuestos, precio en formato Pill y visualización de disponibilidad elástica.
- **Infraestructura de Shimmers Pro:**
    - Implementación de **Skeleton Screens** en todas las vistas críticas: Home, Pedidos, Solicitudes, Mensajes, Disponibilidad, Contratación y **Detalle del Servicio**.
    - Optimización de intensidad visual (alpha 0.25) para un feedback de carga más claro y elegante.
    - El Shimmer de detalles replica exactamente la estructura de la galería, card de prestador y secciones de valor, eliminando saltos visuales.
    - Soporte completo de Shimmers para el Dashboard Administrativo (Aprobaciones, Usuarios y Reportes).
- **Onboarding Interactivo:** Carrusel de bienvenida de 3 pasos con animaciones y dots dinámicos.
- **Utilidades Globales (Formatter Engine):**
    - Implementación de `FormatterUtils.kt` con soporte para moneda colombiana compacta (ej: **$ 50k**, **$ 1.2M**).
    - Normalización de parsing de fechas ISO y formatos de tiempo.
- **Optimización de Accesibilidad (Universal Design):**
    - Layouts adaptativos para fuentes al **200%** mediante `FlowRow`, pesos dinámicos y `sizeIn`.
    - Blindaje de círculos de disponibilidad y textos de contacto en el Chat.
- **Estandarización Atómica (Atomic Design):**
    - Refactorización masiva de la interfaz de usuario bajo la metodología **Atomic Design**, creando librerías de componentes reutilizables en `ui/components/atoms`, `ui/components/molecules` y `ui/components/organisms`.
    - **Átomos:** `YayaButton`, `YayaTextField`, `YayaAvatar`, `YayaStatusBadge`, `YayaSectionHeader`, `YayaBranding` y `YayaLogo`.
    - **Moléculas:** `RatingIndicator`, `DayIndicator`, `CategorySelector`, `DetailRow`, `ChatContactItem`, `ProfileOptionItem` (con soporte de badges), `UserListItem`, `EmptyStateView`, `TimeSelectorPill`, `YayaTimePickerDialog`, `NegotiationHistoryBox`, `NegotiationActionPill`, `AvatarSelector`, `ChatBubble`, `ChatInputBar`, `StatusBadgeDetail`, `PriceNegotiator`, `YayaReportDialog`, `YayaRatingDialog`, `YayaRatingItem`, `YayaSelectorButton`, `YayaNegotiationDialog` y `YayaOfflineBanner`.
    - **Organismos:** `ServiceCard`, `HomeTopBar`, `AdminTopBar`, `AdminServiceCard`, `ReportSummaryCard`, `MyServiceCard`, `IncomingRequestCard`, `MyOrderCard`, `SearchBarIntegrated`, `OnboardingCarousel`, `ProfileHeroHeader`, `ProfileSectionCard`, `AvailabilityDayCard`, `WelcomeActions`, `ChatHeader`, `ServiceDetailGallery`, `ProviderCard`, `ServiceRequestHero`, `ConfirmationTicketCard` y `SuccessHeroBanner`.
    - Esta reestructuración garantiza consistencia visual absoluta y facilita el mantenimiento global de la marca BH++.
- **Feedback de Notificaciones Expandido:**
    - Implementación de badges de notificación en tiempo real en la pantalla de Perfil.
    - Los prestadores ahora ven el conteo de solicitudes pendientes directamente en su menú de perfil.
    - Los administradores visualizan el número de servicios pendientes de aprobación en el acceso al Dashboard Admin.
    - Integración de conteo de mensajes no leídos en la sección "Mis Actividad" del perfil.
- **Auditoría Admin Automatizada (Edge Functions):**
    - Evolución de la Edge Function unificada para soportar envío masivo de notificaciones a múltiples administradores simultáneamente.
    - Implementación de notificaciones instantáneas para el equipo admin ante la creación de nuevos servicios ("🛡️ Nuevo Servicio por Auditar").
    - Cierre del ciclo de feedback al prestador: notificaciones automáticas cuando su servicio es aprobado o pausado por la administración.
- **Sistema de Conectividad Proactivo:**
    - Implementación de `NetworkConnectivityObserver` para monitorear el estado de internet en tiempo real mediante Coroutines Flow.
    - Integración de banner de alerta global (`YayaOfflineBanner`) que notifica al usuario cuando se pierde la conexión, mejorando la fiabilidad de la App.
- **Anonimato Administrativo (Seguridad Admin):**
    - Implementación de capa de enmascaramiento de identidad para el equipo de moderación.
    - Cuando un administrador interactúa con un usuario, su nombre se muestra como "Equipo de Moderación" y su avatar es reemplazado por el isotipo oficial de YÁYA.
    - Esta protección se aplica de forma inteligente: los administradores conservan su identidad real cuando chatean entre sí.
    - Actualización de la Edge Function para garantizar anonimato en las notificaciones push ("🛡️ Equipo de Moderación YÁYA").
- **Ecosistema Legal y Cumplimiento (Play Store Ready):**
    - Implementación de aceptación obligatoria de Términos y Condiciones y Política de Privacidad en el flujo de registro.
    - Desarrollo de un **Visor Legal Premium** con motor de renderizado Markdown-lite, aplicando jerarquía tipográfica, iconos institucionales y degradados inmersivos.
    - Acceso permanente a documentos legales desde la configuración del perfil, garantizando transparencia total con el usuario.
- **Infraestructura de Observabilidad (Firebase Pro):**
    - Integración de **Firebase Crashlytics** para el monitoreo automático de errores y crashes en tiempo real.
    - Implementación de **Firebase Analytics** para la medición de eventos de usuario y métricas de retención.
    - Implementación de `CrashReporter.kt` como motor centralizado de logging y excepciones para la App.
- **Ecosistema de Manuales Finalizado (Presentación SENA):**
    - Redacción exahustiva del Manual de Usuario Final, cubriendo procesos de registro, flujos de negociación y el innovador protocolo Handshake Digital.
    - Consolidación del Manual Técnico Maestro con diagramas de arquitectura, seguridad RLS y guías de despliegue.
    - Estructura alineada con los requisitos de entrega del programa ADSO - SENA.
- **Portal Web y Marketing (Producción):**
    - Despliegue oficial de la Landing Page interactiva en **Firebase Hosting** ([y-ya-d5929.web.app](https://y-ya-d5929.web.app)).
    - Optimización **Responsive** total con menú de hamburguesa y layouts elásticos para dispositivos móviles.
    - Integración de **Modo Oscuro** nativo con persistencia en el portal web.
    - Conexión de botones de acción (CTA) con la ficha oficial de **Google Play Store**.
    - Integración de identidad visual oficial (Logos, Isotipos y Mockups) en toda la web.
    - **Performance & SEO Booster:** Implementación de dimensiones explícitas, pre-carga de imágenes críticas (Preload/FetchPriority) y estrategia SEO completa (Meta tags, JSON-LD, Sitemap/Robots).
    - **Accesibilidad Senior:** Cumplimiento de estándares con etiquetas ARIA, jerarquía de encabezados corregida y optimización de contrastes para legibilidad universal.
- **Cumplimiento y Seguridad:** Opción de borrado de cuenta integrado en el perfil para cumplimiento con Google Play.

### Cambiado
- **Refactor Arquitectónico Senior:** Migración total a "Vistas Pasivas" (Stateless) y ViewModels con inyección de `UiState`.
- **Navegación Robusta:** Soporte transversal para `navigationBarsPadding` en toda la aplicación.

## [0.1.2-alpha] - 2026-08-27
### Añadido
- **Refinamiento Visual del Login:** Isotipo Circular Premium y eliminación de redundancia de marca.

## [0.1.1-alpha] - 2026-08-26
### Añadido
- **Infraestructura de Notificaciones Pro:** Edge Function unificada y Small Icons monocromáticos.
- **Negociación Premium UX:** Selector de oferta dinámico con incrementos de $5.000.

### Corregido
- **DatePicker:** Ajuste de zona horaria (Colombia UTC-5).

---
*BH++ - Senior Software Engineering*
