# Changelog - YÁYA

Todas las modificaciones notables en este proyecto serán documentadas en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.3-alpha] - 2026-08-27
### Añadido
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
