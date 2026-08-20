# Changelog - YÁYA

Todas las modificaciones notables en este proyecto serán documentadas en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0-alpha] - 2025-03-05
### Añadido (MVP+ Milestone)
- **Refinamiento UI/UX Transversal:**
    - Implementación de Avatares dinámicos en el Dashboard Administrativo (Listado de usuarios, reportes y aprobación de servicios).
    - Integración de foto de perfil del usuario actual en la barra de navegación del Home Screen.
    - Optimización de consultas relacionales (Postgrest Joins) para la recuperación eficiente de metadatos de perfiles.
- **Hito 3: Multimedia y Gestión de Activos:**
    - Integración de Supabase Storage con soporte para buckets de 'avatars' y 'portfolios'.
    - Implementación de carga dinámica de imágenes mediante Coil 3.
    - Nuevo modelo de datos `ServiceImage` y tabla `service_images` para portafolios múltiples.
    - Funcionalidad de selección y subida de foto de perfil (Avatar) en tiempo real.
    - Soporte para subida múltiple de imágenes de trabajos realizados en la creación de servicios.
    - Desarrollo de Carrusel Visual de portafolio en la pantalla de detalle del servicio.
    - Implementación de visor de imágenes a pantalla completa con soporte para navegación gestual (Swipe) mediante HorizontalPager.
    - Sistema de indicadores de posición (Pagination) para galerías extensas.
- **Hito 4: Infraestructura de Notificaciones (In Progress):**
    - Integración de Firebase Cloud Messaging (FCM) para recepción de alertas push (Lado cliente).
    - Implementación de Badges (numerito) dinámicos en la campana de notificaciones y chat del Home (Realtime).
    - Lógica de sincronización de tokens de dispositivo con el perfil de Supabase.
    - *Nota: Pendiente activación de Edge Functions y Webhooks para disparo automático desde el servidor.*
- **Documentación de Calidad y Auditoría:**
    - Implementación de matriz de calidad basada en el estándar ISO/IEC 25010.
    - Definición de historias de usuario bajo metodología BDD (Given-When-Then) para flujos críticos.
    - Creación de presentaciones teóricas sobre fundamentos de calidad de software (Fase 1 Taller).
- **Hito 2: Chat en Tiempo Real:** 
    - Implementación de mensajería instantánea bidireccional.
    - Nuevo Componente "ChatListScreen": Centro de mensajes centralizado para acceder a todas las conversaciones activas.
    - Lógica de "Visto" (Double Check): Marcación automática de mensajes como leídos en la base de datos al abrir la conversación.
    - Integración con `Supabase Realtime` para actualizaciones sin recarga.
    - Puntos de contacto en Detalle de Servicio, Mis Pedidos y Solicitudes Recibidas.
- **Hito 2: Sistema de Reputación:** 
    - Implementación de flujo de calificaciones (1-5 estrellas) y reseñas.
    - Nuevo modelo de datos `Rating` vinculado a las solicitudes completadas.
- **Hito 5: Dashboard Administrativo:** 
    - Implementación de panel de control para moderación de servicios.
    - Sistema de reportes de comportamiento con consultas relacionales.
    - Lógica de redirección por rol inmediata tras el inicio de sesión exitoso.
- **Ecosistema de Agentes Especializados:** Definición de roles (Senior, UI, Datos, Negocio, Docs) y Orquestador Maestro para la gobernanza del proyecto.
- **Hito 1: Refinamiento Operativo:** 
    - Reactividad Total: Implementación de suscripciones Realtime para listas de servicios, historial propio y contadores de la barra superior (Badges).
    - Badge de Mensajes: Indicador visual en tiempo real de mensajes no leídos en el Home.
    - Implementación de disponibilidad estructurada por servicio: Migración de campo de texto a `working_days` (array), `start_time` y `end_time` (time) en la tabla `services`.
    - Nuevo Componente UI "Day Picker": Selección interactiva de días de la semana (L-D) con feedback visual en la creación de servicios.
    - Lógica de Validación Triple: Cruce proactivo en tiempo real de días del servicio, rango horario del servicio y horario general del prestador en el flujo de contratación.
    - Implementación de lógica de validación de disponibilidad en tiempo real contra `public.availability`.
    - Evolución del modelo económico con soporte para `final_price` en el flujo de negociación.
    - Optimización de UX en Home con estados vacíos (`EmptyServicesView`) para búsquedas y filtros.
- **Sistema de Cuenta Universal:** Implementación de acceso multi-rol sin doble fricción.
- **Módulo de Negociación:** Lógica de contraofertas entre Clientes y Prestadores.
- **Gestión Operativa:** Panel de "Solicitudes Recibidas" (Prestador) y "Mis Pedidos" (Cliente).
- **CRUD de Servicios:** Los prestadores pueden crear, editar y pausar sus servicios.
- **Perfil Transaccional:** Registro, login persistente y edición de perfil vinculada a SQL.
- **Documentación Senior:** Creación de manuales, diagramas ER y modelos de seguridad.

### Cambiado
- N/A

### Eliminado
- N/A

---
*BH++ - Senior Software Engineering*
