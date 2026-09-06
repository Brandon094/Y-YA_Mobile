# Requisitos del Proyecto - YÁYA (v1.2.0)

## 1. Requisitos Funcionales (RF)

### Gestión de Usuarios
- **RF-01:** El sistema permitirá el registro de usuarios mediante correo y contraseña.
- **RF-02:** El sistema permitirá el inicio de sesión seguro con persistencia de sesión.
- **RF-03:** **Cuenta Universal:** El usuario tendrá un perfil único que le permite actuar como Cliente o Prestador de Servicios sin cambiar de cuenta.
- **RF-04:** El usuario podrá editar su perfil detallado (Cédula, Dirección, Fecha de Nacimiento, Foto).
- **RF-20:** El sistema permitirá al usuario solicitar la eliminación de su cuenta y datos personales directamente desde la aplicación (Cumplimiento Google Play).

### Catálogo de Servicios
- **RF-05:** El sistema mostrará una lista dinámica de categorías con iconografía específica (Mascotas, Hogar, Tecnología, etc.).
- **RF-06:** El sistema permitirá filtrar servicios por categoría y búsqueda de texto en tiempo real.
- **RF-07:** El usuario podrá ver el detalle técnico de un servicio, incluyendo días de atención, horarios y costos adicionales.

### Contratación y Negociación (Core)
- **RF-08:** El cliente podrá enviar una solicitud de servicio especificando dirección, fecha y hora.
- **RF-09:** **Validación de Disponibilidad:** El sistema bloqueará intentos de contratación fuera de los días y horas definidos por el prestador para ese servicio específico.
- **RF-10:** **Módulo de Negociación:** El prestador podrá aceptar, rechazar o enviar una contraoferta de precio; el cliente podrá responder a dicha oferta para cerrar el trato.

### Comunicación y Reputación
- **RF-11:** **Chat en Tiempo Real:** Comunicación instantánea bidireccional entre las partes interesadas mediante Supabase Realtime.
- **RF-12:** **Centro de Mensajes:** Lista centralizada de conversaciones activas accesible desde el Home.
- **RF-13:** **Sistema de Visto:** Los mensajes deben marcarse como leídos automáticamente al abrir la conversación.
- **RF-14:** El cliente podrá calificar (1-5 estrellas) y reseñar al prestador tras finalizar un servicio.

### Multimedia y Portafolio (Hito 3)
- **RF-15:** **Fotos de Perfil:** Los usuarios podrán subir y actualizar su avatar desde la pantalla de edición de perfil.
- **RF-16:** **Portafolio de Servicios:** Los prestadores podrán subir múltiples imágenes (galería) que demuestren la calidad de sus trabajos anteriores al crear o editar un servicio.
- **RF-17:** **Visualización Avanzada:** El sistema permitirá la navegación gestual (Swipe) y zoom táctil en un visor a pantalla completa con indicadores de paginación para las imágenes del portafolio.
- **RF-18:** **Visualización Dinámica (Performance):** El sistema cargará imágenes de forma eficiente (Lazy Loading) mediante Coil 3 para optimizar el consumo de datos y memoria.

### Notificaciones y Reactividad
- **RF-18:** El sistema enviará notificaciones push automáticas (FCM) ante nuevos mensajes o solicitudes.
- **RF-19:** **Badges Dinámicos:** La interfaz mostrará contadores en tiempo real sobre los iconos de notificaciones y mensajes en la barra superior.

### Moderación y Administración (Hito 5)
- **RF-17:** El sistema contará con un Dashboard Administrativo para métricas y control de usuarios.
- **RF-18:** **Flujo de Aprobación:** Los nuevos servicios entran en estado `pending_approval` y requieren validación humana para ser públicos.
- **RF-19:** **Sistema de Sanciones Progresivas:** El sistema agrupará reportes por infractor y recomendará sanciones (Llamado de atención, Suspensión, Eliminación) basadas en el volumen de denuncias (Semáforo de Severidad).
- **RF-21:** Los administradores podrán suspender servicios o eliminar cuentas de infractores directamente desde el panel de reportes.
- **RF-22 (Tutoriales Spotlight):** El sistema incluirá una guía visual interactiva (*ShowOnce*) que ilumina componentes mediante recortes y Anillo de Luz.
- **RF-23 (Verificación Previa):** El sistema realizará un *Pre-flight Check* para bloquear duplicados de cédula o correo antes de proceder al registro en Auth.
- **RF-24 (Restricción de Edad):** El sistema exigirá un mínimo de 15 años cumplidos para el registro, inhabilitando fechas no permitidas en el calendario.
- **RF-25 (Wizard de Servicios):** La creación de servicios se dividirá en 2 etapas claras con una barra de progreso y duración estructurada.

## 2. Requisitos No Funcionales (RNF)

### Rendimiento y Reactividad
- **RNF-01:** La aplicación debe cargar la pantalla principal en menos de 2 segundos.
- **RNF-02:** **UI Reactiva:** Las listas de servicios y estados de pedidos deben actualizarse automáticamente ante cambios en la base de datos sin recargar la pantalla.
- **RNF-07:** **Gestión de Desconexión:** El sistema debe detectar en tiempo real la pérdida de conectividad a internet y notificar visualmente al usuario para evitar errores de red en transacciones críticas.

### Seguridad
- **RNF-03:** Toda la comunicación con el backend debe realizarse vía HTTPS.
- **RNF-04:** **Seguridad a nivel de fila (RLS):** Los usuarios solo pueden leer o editar datos que les pertenecen o sobre los que tienen permiso explícito de negociación.

### Usabilidad y Legal
- **RNF-05:** La interfaz debe seguir las guías de Material Design 3 con soporte para modo claro y oscuro.
- **RNF-06:** **Cumplimiento Legal:** El usuario debe tener acceso a los Términos, Condiciones y Políticas de Privacidad desde la documentación oficial de la app.

---
*Analista de Software: BH++ Team*
