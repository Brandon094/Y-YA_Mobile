# Changelog - YÁYA

Todas las modificaciones notables en este proyecto serán documentadas en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0-alpha] - 2025-03-05
### Añadido (MVP+ Milestone)
- **Hito 4: Infraestructura de Notificaciones:**
    - Integración de Firebase Cloud Messaging (FCM) para recepción de alertas push.
    - Implementación de Badges (numerito) dinámicos en la campana de notificaciones del Home.
    - Lógica de sincronización de tokens de dispositivo con el perfil de Supabase.
    - Soporte para permisos de notificación en Android 13+.
- **Hito 2: Chat en Tiempo Real:** 
    - Implementación de mensajería instantánea bidireccional.
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
