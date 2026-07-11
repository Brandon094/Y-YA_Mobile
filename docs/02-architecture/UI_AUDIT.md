# Auditoría de Funcionalidad y UI/UX - YÁYA

Este documento sirve como lista de verificación (checklist) para el estado de implementación de cada pantalla, su lógica de negocio y su integración con el backend.

---

## 🟢 1. Flujo de Acceso (Onboarding & Auth)
Estado general para garantizar la entrada segura de usuarios.

- [x] **Welcome Screen**
    - [x] UI: Diseño minimalista con logos de marca.
    - [x] Navegación: Redirección correcta a Login/Registro.
- [x] **Login**
    - [x] UI: Campos validados y manejo de errores visuales.
    - [x] ViewModel: Integración con `Supabase Auth`.
    - [x] Persistencia: Guardado de sesión local.
- [x] **Registro**
    - [x] UI: Selector de rol (Cliente/Prestador) funcional.
    - [x] ViewModel: Creación de usuario en Auth y perfil en `public.profiles`.
- [x] **Recuperar Contraseña (Reset)**
    - [x] UI: Formulario de envío de correo.
    - [x] Lógica: Envío de enlace de recuperación vía Supabase.

---

## 🟢 2. Pantalla Principal (Exploración)
El núcleo de descubrimiento de servicios.

- [x] **Home (Catálogo)**
    - [x] UI: Listado dinámico de servicios con `ServiceItem`.
    - [x] ViewModel: Carga inicial de categorías y servicios desde SQL.
    - [x] Búsqueda: Filtrado por texto (Local Search).
    - [x] Filtros: Selección de categorías funcional.
    - [x] Roles: Visualización de FAB (+) solo para prestadores/admin.
    - [x] UX: Vista de estados vacíos (`EmptyServicesView`) para búsquedas sin resultados.

---

## 🟡 3. Ciclo de Contratación (Core Business)
Flujo transaccional entre cliente y prestador.

- [x] **Detalle de Servicio**
    - [x] UI: Visualización de descripción, precio y datos del prestador.
    - [x] Navegación: Botón de "Contratar" funcional.
- [x] **Pantalla de Contratación (Formulario)**
    - [x] UI: Selectores de fecha y hora integrados.
    - [x] ViewModel: Registro de la solicitud en `public.requests`.
    - [x] Lógica: Validación en tiempo real contra `public.availability` (Hito 1).
- [x] **Confirmación (Ticket)**
    - [x] UI: Resumen dinámico de la transacción exitosa.

---

## 🟢 4. Gestión de Usuario y Perfiles
Configuración y administración de identidad.

- [x] **Perfil (Vista)**
    - [x] UI: Visualización de datos actuales del usuario.
- [x] **Editar Perfil**
    - [x] UI: Formulario con carga de datos previos.
    - [x] ViewModel: Actualización de datos en `public.profiles`.
- [ ] **Avatar Storage** (Pendiente)
    - [ ] Lógica: Subida de imagen a Supabase Bucket.

---

## 🟢 5. Paneles Operativos (Dashboard)
Gestión de órdenes y servicios según el rol.

- [x] **Mis Pedidos (Cliente)**
    - [x] UI: Listado con badges de estado (`pending`, `accepted`, etc.).
    - [x] Lógica: Sistema de negociación de contraofertas funcional.
    - [x] Reputación: Flujo de calificación (estrellas y comentarios) para servicios completados.
- [x] **Solicitudes Recibidas (Prestador)**
    - [x] UI: Interfaz para aceptar/rechazar o contraofertar.
    - [x] ViewModel: Actualización de estados en tiempo real.
- [x] **Mis Servicios (Prestador)**
    - [x] UI: Listado de servicios propios publicados.
    - [x] Lógica: Función de Activar/Desactivar visibilidad.
- [x] **Crear Servicio**
    - [x] UI: Formulario completo con categorías y precios.
- [x] **Dashboard Administrativo (Hito 5)**
    - [x] UI: Sistema de pestañas para Pendientes, Usuarios y Reportes.
    - [x] Lógica: Moderación de servicios (Aprobar/Rechazar).
    - [x] Lógica: Visualización de reportes con Joins complejos.
    - [x] Seguridad: Redirección automática por rol `admin`.

---

## 🔴 6. Próximas Implementaciones (MVP+)
Funcionalidades críticas para el cierre de la versión 1.0.

- [x] **Chat en Tiempo Real**
    - [x] UI: Interfaz de burbujas con scroll automático.
    - [x] Lógica: Suscripción a `messages` vía Supabase Realtime.
    - [x] Navegación: Accesos desde Detalle, Mis Pedidos y Solicitudes.
- [x] **Sistema de Calificaciones (Ratings)**
- [ ] **Notificaciones Push**

---
**Leyenda:**
- [x] Implementado y Probado.
- [ ] En desarrollo o pendiente.
- 🟢/🟡/🔴 Indica el nivel de madurez del módulo.
