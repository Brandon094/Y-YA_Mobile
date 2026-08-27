# Changelog - YÁYA

Todas las modificaciones notables en este proyecto serán documentadas en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.2-alpha] - 2026-08-27
### Añadido
- **Onboarding Interactivo:**
    - Transformación de la pantalla de bienvenida en un carrusel dinámico mediante `HorizontalPager`.
    - 3 pasos narrativos: Descubre, Negocia y Confía, con indicadores de página (dots) dinámicos.
- **Refinamiento Visual del Login:**
    - Rediseño de cabecera con **Isotipo Circular Premium** (140dp con fondo sutil).
    - Eliminación de texto redundante "YÁYA" para un look más limpio y profesional.
    - Implementación de scroll interno para evitar problemas de visualización en dispositivos con pantallas pequeñas.

### Cambiado
- **Ciclo de Vida de Servicios (Cierre):**
    - Implementación del flujo completo: Solicitud -> Negociación -> Aceptación -> **Finalización** (por el prestador) -> **Calificación** (por el cliente).
    - El prestador ahora dispone del botón "Finalizar Trabajo" (color verde éxito) una vez que el servicio ha sido aceptado.
- **Sistema de Reputación Blindado:**
    - Las calificaciones ahora son **inmutables**. Una vez enviada la reseña, el botón desaparece y se muestra el **Historial de Calificación** con estrellas y comentario original.
    - Sincronización automática de promedios en el Home y Detalle de Servicio tras cada calificación.

## [0.1.1-alpha] - 2026-08-26
### Añadido
- **Infraestructura de Notificaciones Pro:**
    - Configuración de **Small Icon monocromático** (`ic_noti.png`) para cumplimiento de guías de diseño de Android.
    - Implementación de canal de notificaciones y lógica de recepción en primer plano (`YayaMessagingService`).
    - Despliegue de **Edge Function unificada** en Supabase para notificaciones push automáticas que cubren:
        - **Chat:** Alerta inmediata al recibir un mensaje nuevo.
        - **Negociación:** Notificaciones de cambios de precio (contraofertas).
        - **Estados:** Avisos automáticos al aceptar o cancelar solicitudes.
- **Negociación Premium UX:**
    - Nuevo **Selector de Oferta dinámico** con botones `+/-` e incrementos automáticos de $5.000.
    - Implementación de **Regla de Negocio de Precio Mínimo**: El cliente no puede ofertar menos del precio base del servicio.
    - Rediseño de diálogos de contraoferta tanto para Prestador como para Cliente con iconografía clara (`🚩`, `🔹`).
    - Sincronización de etiquetas de historial de negociación para reconocimiento automático de respuestas.
- **Refinamiento UI/UX de Contratación:**
    - Rediseño integral de la pantalla de contratación con tarjetas informativas para datos del sistema (Título, Precio Base).
    - Agrupación lógica de campos de entrada (Ubicación, Fecha, Hora) para una mejor jerarquía visual.
- **Rediseño de "Mis Pedidos" (Cliente):**
    - Tarjetas de pedido de alto impacto visual con iconografía vectorial detallada (Ubicación, Precio, Fecha).
    - Indicadores visuales de "Acción Requerida" con Action Badges para nuevas ofertas recibidas.
    - Historial de negociación estilizado y botones de acción Premium (Chat, Aceptar, Negociar).
- **Ajuste de Flujo de Reserva:**
    - Cambio semántico de la pantalla de confirmación a **"¡Solicitud enviada!"** para una gestión transparente de las expectativas del usuario.
- **Sistema de Disponibilidad Global:**
    - Implementación de la tabla `availability` para que los prestadores definan su horario maestro de trabajo.
    - Nueva pantalla **"Mi Horario de Trabajo"** en el perfil del prestador, permitiendo activar/desactivar días y ajustar horas.
    - Validación proactiva en el flujo de contratación: El sistema ahora cruza la fecha seleccionada por el cliente con el horario global del prestador y el horario específico del servicio.
- **Cumplimiento Legal (Google Play):**
    - Implementación de la opción **"Eliminar mi cuenta"** en la pantalla de perfil.
    - Flujo de borrado con doble confirmación para evitar eliminaciones accidentales.
    - Lógica de desconexión inmediata y limpieza de sesión tras el borrado del perfil en Supabase.
- **Refactor Arquitectónico Clean MVVM:**
    - Transformación de todas las pantallas principales en "Dumb Components" (Vistas tontas).
    - Implementación de modelos de **UiState** dedicados (`MessageUiState`, `ServiceUiState`, `IncomingRequestUiState`, `MyOrderUiState`, `MyServiceUiState`) para centralizar el formateo y la lógica de negocio en los ViewModels.
    - Eliminación de procesamiento de strings, fechas y comparaciones de IDs dentro de las funciones Composable.
    - Mejora de la reactividad del Chat con `reverseLayout` y scroll automático inteligente.
- **Experiencia de Usuario Premium (Issue #UX):**
    - Implementación de **Shimmer Effect (Skeletons)** en todas las listas principales (Home, Pedidos, Solicitudes, Mensajes y Disponibilidad) para una carga visual fluida.
    - Optimizaciones de **Accesibilidad**: Ajuste de layouts para soportar fuentes de hasta el 200%, incrementando límites de líneas y asegurando scroll en campos descriptivos.
- **Rediseño de Splash Screen Premium:**
    - Implementación de la **Splash Screen API oficial** de Android, eliminando el doble splash heredado.
    - Nuevo diseño mediante **Icono Adaptativo**: Isotipo coral centrado con fondo blanco circular nativo, garantizando una forma perfecta en todos los dispositivos.
    - Optimización de escala: Uso de `inset` del 30% para un aspecto elegante y minimalista.
    - Sello de marca corporativa: Traslado del **"Powered by BH++"** como firma visual en el pie de las pantallas de Bienvenida y Login para una mejor integración con la UI.
    - **Motor de Arranque Directo (Zero-Flicker):** Refactor de `MainActivity` para diferir la construcción de la UI hasta que `MainViewModel` confirme la sesión, permitiendo un paso instantáneo desde el Splash a la pantalla destino (Home/Admin).
    - Soporte completo para Modo Oscuro con fondo *Deep Midnight*.
- **Chat Pro Avanzado:**
    - Listado de chats con badges de no leídos, previsualización del último mensaje y marcas de tiempo dinámicas.
    - Rediseño de la pantalla de chat con burbujas Premium, hora de mensaje e indicadores de lectura (Double Check).
    - Optimización de UX: Implementación de **Gestión de Teclado (IME)** mediante `imePadding` para evitar el solapamiento del campo de texto al escribir.
    - Robustez de datos: Comparación de UUIDs case-insensitive para asegurar la vinculación de mensajes en todos los roles.
- **Ciclo de Vida de Solicitudes Completo:**
    - Se ha implementado el estado final de los servicios: ahora el prestador puede marcar un trabajo como **"Completado"** tras haber sido aceptado.
    - Nuevo botón dinámico **"Finalizar Trabajo"** en el panel de Solicitudes Recibidas (Vista Prestador).
    - El flujo ahora es: Solicitud -> Negociación -> Aceptación -> Finalización -> Calificación.
- **Sistema de Reputación Blindado:**
    - Implementación completa de la lógica de **Calificaciones y Reseñas** vinculada a la tabla `ratings` de Supabase.
    - Visualización de **promedio de estrellas y conteo de reseñas** en las tarjetas del catálogo (Home).
    - Nueva sección de **Reseñas de Clientes** en el detalle del servicio, permitiendo leer comentarios previos sobre el prestador.
    - Lógica de control en "Mis Pedidos": El sistema detecta automáticamente si un servicio ya fue calificado, bloqueando el re-envío y mostrando el **Resumen Histórico de Calificación** (estrellas y comentario del usuario).

### Corregido
- **DatePicker:** Corrección de desfase de zona horaria (-1 día) al convertir milisegundos UTC a hora local (Colombia UTC-5).
- **Notificaciones:** Eliminación de recuadro blanco en barra de estado mediante ícono monocromático configurado en Manifest.

## [0.1.0-alpha] - 2025-03-05
### Añadido (MVP+ Milestone)
- **Personalización y Branding:**
    - Implementación de **Dark Theme Premium** basado en tonos Deep Midnight (Slate) para reducir la fatiga visual.
    - Integración de la **Splash Screen API** oficial de Android, con soporte dinámico para Modo Claro (Blanco) y Modo Oscuro (Deep Midnight).
    - Sincronización automática de barras del sistema (Status Bar) con el tema dinámico.
    - Adopción completa de tokens de diseño Material 3 (SurfaceVariant, Outline, Tertiary).
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
