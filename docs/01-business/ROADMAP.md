# Roadmap del Proyecto - YÁYA

Este documento describe la hoja de ruta estratégica para el desarrollo de YÁYA, dividida en hitos (milestones) que llevan el proyecto desde su estado actual hasta el lanzamiento del MVP (Producto Mínimo Viable) y futuras expansiones.

## Estado Actual: 🟢 Producción / Play Store Ready (v1.2.0 - versionCode 8)
- **Cuenta Universal:** Sin doble fricción entre clientes y prestadores.
- **Negociación Activa ("Handshake"):** Flujo de contraofertas con doble confirmación funcional.
- **Gestión Dual & Admin:** Panel administrativo con sanciones progresivas y anonimato de moderación.
- **Publicación & Play Store:** Preparación técnica para Google Play con permiso `AD_ID`, símbolos NDK FULL y `versionCode = 5`.
- **CI/CD & Calidad:** CodeQL Advanced Analysis con Java 17 y secret injections heredoc.

---

## Hito 1: Refinamiento Operativo y Lógica de Negocio
**Objetivo:** Asegurar que el flujo transaccional sea robusto y preciso.
- [x] **Disponibilidad Granular:** Implementación de selección de días (`working_days`) y rangos horarios por servicio individual.
- [x] **Sincronización de Disponibilidad:** Gestión de horario maestro mediante la tabla `public.availability` y validación en tiempo real en el flujo de contratación.
- [x] **Evolución del Modelo Económico:** Implementación del campo `final_price` en solicitudes para reflejar acuerdos tras negociaciones.
- [x] **Tema Oscuro Premium:** Implementación de paleta Deep Midnight optimizada para legibilidad y ahorro de batería.
- [x] **Splash Screen Adaptativa:** Migración a la API oficial con soporte para temas de sistema.
- [x] **Onboarding Interactivo:** Sistema de carrusel dinámico para comunicar la propuesta de valor.
- [x] **Optimización de UI/UX:** Refactorización integral bajo **Atomic Design** y estandarización de componentes (v0.1.3).
- [x] **Gestión de Conectividad:** Implementación de observador de red global con feedback visual preventivo (No-Internet Banner).

---

## Hito 2: Comunicación y Sistema de Reputación
**Objetivo:** Generar confianza entre cliente y prestador dentro de la plataforma.
- [x] **Chat en Tiempo Real:** Implementación de mensajería bidireccional utilizando `Supabase Realtime`.
- [x] **Sistema de Calificaciones (Ratings):** 
    - Envío de reseñas y puntuación (1-5 estrellas) al completar un servicio.
    - Visualización de reputación real en el catálogo (Cierre de ciclo).

---

## Hito 3: Multimedia y Gestión de Activos (Storage) - 🟢 COMPLETO
**Objetivo:** Enriquecer el contenido visual de la plataforma.
- [x] **Gestión de Avatares:** Integración con Supabase Storage para fotos de perfil dinámicas.
- [x] **Portafolio de Servicios:** Galería de imágenes para que los prestadores muestren trabajos anteriores en sus anuncios.
- [x] **Visor Premium:** Navegación por gestos (Swipe) en pantalla completa para portafolios.

## Hito 4: Escalabilidad, Notificaciones y Legal
**Objetivo:** Preparar la aplicación para el lanzamiento comercial y cumplimiento normativo.
- [x] **Sistema de Notificaciones Push:** 
    - [x] Integración de Firebase Cloud Messaging (FCM) en Android.
    - [x] Gestión de tokens de dispositivo en la base de datos.
    - [x] Lógica de Badges en tiempo real (Realtime).
    - [x] **Small Icon Monocromático:** Configuración oficial para cumplimiento de guías de Android.
    - [x] **Fase de Pruebas:** Depuración de Edge Functions y Webhooks de disparo (Operatividad básica completada).
- [x] **Automatización de Avisos:** Implementación de Edge Functions en Supabase para disparo automático de notificaciones en negociación y estados.
- [x] **Modelo SaaS (Suscripciones):** Pendiente implementación de planes (Hito futuro).
- [x] **Cumplimiento Legal:** Integración de borradores oficiales de Términos y Condiciones, y Políticas de Privacidad (v0.2.0).
- [ ] **Pasarela de Pagos (Post-MVP):** Integración con proveedores locales para pagos in-app.

---

## Hito 5: Panel Administrativo y Moderación (Calidad del Sistema)
**Objetivo:** Garantizar la integridad de la plataforma y evitar servicios "basura".
- [x] **Dashboard Admin:** Interfaz exclusiva para el rol `admin` con métricas de uso.
- [x] **Módulo de Moderación:** Herramientas para validar, pausar o eliminar servicios sospechosos.
- [x] **Reportes de Comportamiento:** Implementación de tabla `public.reports` y sistema de denuncias visuales.
- [x] **Auditoría de Servicios:** Flujo de aprobación para nuevos servicios de prestadores no verificados.

## Hito 6: Evolución UI/UX y Seguridad Robusta (v1.2.0) - 🟢 COMPLETO
- [x] **Tutoriales Spotlight:** Implementación del sistema *ShowOnce* con 8 pantallas, recortes transparentes y Anillo de Luz.
- [x] **Wizard de Servicios 2.0:** Flujo de creación en 2 pasos con barra de progreso y duración estructurada.
- [x] **Pre-flight Checks:** Validación preventiva de cédula y correo antes del registro en Auth.
- [x] **Restricción de Edad:** Validación de 15 años mínimos con inhabilitación de fechas en calendario.
- [x] **Borrado RPC:** Implementación de borrado atómico en cascada mediante procedimientos almacenados en Postgres.
- [x] **Consolidación Atómica:** Refactorización total de UI/UX (Atoms, Molecules, Organisms).
- [x] **Rediseño ProfileScreen 2.0:** Hero Header 2.0 con botón flotante de edición y Quick Action Cards.
- [x] **Auditoría Admin:** Sistema de moderación y sanciones progresivas funcional.
- [x] **Manual Técnico Completo:** Documentación de arquitectura Senior y estándares de codificación.

## Hito 7: Lanzamiento y Carga de Datos Real
- [x] **Ajuste Legal Final & Google Play:** Cumplimiento de políticas con visor legal, link de privacidad y permiso `AD_ID`.
- [x] **Estabilidad de Layouts & CI/CD:** Fix de `IllegalStateException` en scrolleables Compose, símbolos NDK FULL y CodeQL CI/CD con Java 17.
- [ ] **Carga de Datos Real:** Fase de pruebas con usuarios reales (Mauro, Harold, Brandon).

## Futuro (Post-MVP & Expansión Comercial)
### Inteligencia de Ubicación
- [ ] **Geolocalización en Tiempo Real:** Integración de mapas para visualizar talento local por proximidad.
- [ ] **Filtro de Radio:** Búsqueda de servicios basada en distancia (ej. 5km a la redonda).

### Monetización y Pagos
- [ ] **Pasarela de Pagos Integrada:** Escrow system (depósito en garantía) con Wompi/MercadoPago.
- [ ] **Modelo de Suscripciones:** Planes Premium para prestadores con visibilidad destacada.

### Confianza Bancaria
- [ ] **Verificación Biométrica:** Validación de identidad mediante reconocimiento facial y OCR de documentos.

---
*Roadmap mantenido por el equipo de ingeniería de BH++*
*Última revisión: Septiembre 2026*
