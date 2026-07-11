# Roadmap del Proyecto - YÁYA

Este documento describe la hoja de ruta estratégica para el desarrollo de YÁYA, dividida en hitos (milestones) que llevan el proyecto desde su estado actual hasta el lanzamiento del MVP (Producto Mínimo Viable) y futuras expansiones.

## Estado Actual: 🟢 MVP+ Finalizado (v0.1.0-alpha)
- **Cuenta Universal:** Sin doble fricción entre clientes y prestadores.
- **Negociación Activa:** Flujo de contraofertas funcional.
- **Gestión Dual:** Historial de pedidos y panel de solicitudes recibidas.
- **Publicación:** Módulo de creación y edición de servicios para talentos.
- **Identidad:** Branding y manuales operativos completados.

---

## Hito 1: Refinamiento Operativo y Lógica de Negocio
**Objetivo:** Asegurar que el flujo transaccional sea robusto y preciso.
- [x] **Sincronización de Disponibilidad:** Validación en tiempo real del selector de fecha/hora contra la tabla `public.availability`.
- [x] **Evolución del Modelo Económico:** Implementación del campo `final_price` en solicitudes para reflejar acuerdos tras negociaciones.
- [ ] **Optimización de UI/UX:** Refinamiento de transiciones y estados de carga (Shimmer effect).

## Hito 2: Comunicación y Sistema de Reputación
**Objetivo:** Generar confianza entre cliente y prestador dentro de la plataforma.
- [x] **Chat en Tiempo Real:** Implementación de mensajería bidireccional utilizando `Supabase Realtime`.
- [x] **Sistema de Calificaciones (Ratings):** 
    - Envío de reseñas y puntuación (1-5 estrellas) al completar un servicio.
    - Visualización de reputación real en el catálogo (Cierre de ciclo).

## Hito 3: Multimedia y Gestión de Activos (Storage)
**Objetivo:** Enriquecer el contenido visual de la plataforma.
- [ ] **Gestión de Avatares:** Integración con Supabase Storage para fotos de perfil dinámicas.
- [ ] **Portafolio de Servicios:** Galería de imágenes para que los prestadores muestren trabajos anteriores en sus anuncios.

## Hito 4: Escalabilidad, Notificaciones y Legal
**Objetivo:** Preparar la aplicación para el lanzamiento comercial y cumplimiento normativo.
- [ ] **Sistema de Notificaciones Push:** Alertas en tiempo real sobre cambios de estado en pedidos y nuevos mensajes.
- [ ] **Modelo SaaS (Suscripciones):** Implementación de planes para prestadores con restricciones de visibilidad y funcionalidades.
- [ ] **Cumplimiento Legal:** Integración de pantallas de Términos y Condiciones, y Políticas de Privacidad.
- [ ] **Pasarela de Pagos (Post-MVP):** Integración con proveedores locales para pagos in-app.

---

## Hito 5: Panel Administrativo y Moderación (Calidad del Sistema)
**Objetivo:** Garantizar la integridad de la plataforma y evitar servicios "basura".
- [x] **Dashboard Admin:** Interfaz exclusiva para el rol `admin` con métricas de uso.
- [x] **Módulo de Moderación:** Herramientas para validar, pausar o eliminar servicios sospechosos.
- [x] **Reportes de Comportamiento:** Implementación de tabla `public.reports` y sistema de denuncias visuales.
- [x] **Auditoría de Servicios:** Flujo de aprobación para nuevos servicios de prestadores no verificados.

## Hito 6: Finalización de Manuales y Lanzamiento
- [ ] **Manual Técnico Completo:** Documentación de la API de Supabase y esquemas de seguridad.
- [ ] **Refinamiento de UX de Estados:** Ajuste de paleta de colores funcional para máximo contraste.

---
*Roadmap mantenido por el equipo de ingeniería de BH++*
*Última revisión: Junio 2026*
