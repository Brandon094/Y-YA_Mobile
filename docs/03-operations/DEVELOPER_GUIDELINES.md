# 🤖 Agente de Desarrollo YÁYA - Manual de Instrucciones

Este documento define los estándares técnicos, visuales y de proceso para el desarrollo de la aplicación YÁYA por parte de **BH++**. Cualquier agente de IA o desarrollador debe seguir estas directrices estrictamente.

## 1. Identidad y Propósito
El objetivo es construir una plataforma de servicios líder, utilizando las tecnologías más avanzadas de Android para ofrecer una experiencia segura y fluida.

## 2. Parámetros de Marca (Branding)
- **Marca Desarrolladora:** BH++
- **Nombre de la App:** YÁYA (Siempre con tilde en la primera A).
- **Package Name:** `com.bhplusplus.yaya`
- **Identidad Visual:**
  - `logo_yaya_full`: Logo con personaje (Uso: Splash Screen).
  - `logo_yaya_typographic`: Solo texto azul/coral (Uso: Login y Headers).
  - `ic_logo`: Isotipo entrelazado (Uso: Icono de App y elementos minimalistas).

## 3. Stack Tecnológico Obligatorio
- **Lenguaje:** Kotlin 2.1.0+
- **UI:** Jetpack Compose con Material 3.
- **Navegación:** Jetpack Navigation con **Type-Safety** (Rutas basadas en objetos `@Serializable`).
- **Backend:** Supabase (Auth para usuarios, Postgrest para datos).
- **Persistencia:** Multiplatform Settings (para persistencia de sesión automática).
- **Arquitectura:** MVVM (Model-View-ViewModel).

## 4. Reglas de Oro del Código

### A. Internacionalización (i18n)
- **Cero Hardcoding:** No se permiten textos directamente en las funciones Composable.
- **Recursos:** Todos los textos deben residir en `app/src/main/res/values/strings.xml`.
- **Referencia:** Usar siempre `stringResource(R.string.mi_texto)`.

### B. UI/UX & Gestión de Entrada
- **Validación Proactiva:** Los formularios deben validar el formato de email y longitud de contraseña en tiempo real.
- **Estado del Botón:** El botón de acción principal debe estar deshabilitado (`enabled = false`) mientras el formulario no cumpla las validaciones.
- **Navegación por Teclado:** Implementar `ImeAction.Next` para saltar campos y `ImeAction.Done` para ejecutar la acción principal.
- **Feedback:** Mostrar siempre un estado de carga claro. Priorizar el uso de `shimmerEffect` (Skeletons) para listas y `CircularProgressIndicator` únicamente para acciones de envío (botones).
- **Accesibilidad (Universal Design):**
    - Todos los layouts deben ser probados con escala de fuente al 200%.
    - Usar `Modifier.verticalScroll` en pantallas con múltiples campos de entrada.
    - Evitar alturas fijas (`.height`) en componentes que contengan texto; preferir `heightIn` o `wrapContentHeight`.

### C. Seguridad y Roles
- **Control de Acceso:** La interfaz debe ocultar/mostrar elementos según el campo `role` del perfil del usuario (`client`, `provider`, `admin`).
- **Persistencia:** La sesión debe inicializarse en `MainActivity` mediante `SupabaseManager.initialize(context)`.

### D. Documentación Técnica
- **Código Auto-explicativo:** Las variables y funciones deben tener nombres claros en inglés o español (según el estándar del archivo).
- **Comentarios de Cabecera:** Cada clase, ViewModel y función Composable compleja debe tener un comentario KDoc (/** ... */) que explique su propósito y parámetros.
- **Mantenibilidad:** Documentar decisiones técnicas críticas o lógica de negocio compleja para facilitar el trabajo de futuros agentes o desarrolladores.

## 5. Gestión del Proyecto (Git)
Se debe seguir el estándar de **Conventional Commits**:
- `feat:` Nuevas funcionalidades.
- `fix:` Corrección de errores.
- `refactor:` Mejoras de estructura (ej. renombrado de paquetes).
- `docs:` Documentación (README, LICENSE, Guidelines).
- `chore:` Tareas de mantenimiento o dependencias.

## 6. Sincronización con Base de Datos (SQL)
Cualquier nuevo modelo de datos debe respetar el esquema de `public.services`, `public.profiles` y `public.categories`. Al navegar a detalles, se debe pasar únicamente el `ID` (String) para mantener la eficiencia de las rutas.

## 7. Estado Actual del Proyecto (Contexto para el Agente)
Al iniciar sesión, el agente debe saber que ya se han implementado:
- **Autenticación Avanzada:** Flujo de Login, Registro con respaldo de Metadata (nombre/rol) para autoreparación de perfiles y recuperación de clave.
- **Persistencia de Sesión:** Implementada mediante `multiplatform-settings` (la sesión no se cierra al salir).
- **Catálogo Dinámico (Home):** Listado de servicios reales con barra de búsqueda funcional y filtrado por categorías desde SQL.
- **Detalle de Servicio:** Visualización completa de información técnica, costos extra, materiales y datos del prestador real.
- **Flujo de Contratación (Core):** Sistema de reserva dinámico con selectores de fecha y hora (Reloj/Calendario) sincronizado con la tabla `public.requests`.
- **Ticket de Solicitud:** Pantalla de "Solicitud Enviada" con resumen real y gestión de expectativas.
- **Negociación Premium (Bidireccional):** Sistema de subasta con botones `+/-`, regla de precio mínimo y sincronización de historial en "Mis Pedidos" y "Solicitudes Recibidas".
- **Infraestructura de Notificaciones:** Sistema completo de Small Icons monocromáticos y Edge Functions para automatización de alertas.
- **Motor de Arranque (Zero-Flicker):** Implementación de la Splash Screen API oficial con navegación diferida hasta validación de sesión (paso directo Splash -> Home).
- **Reactividad Total (Issue #8):** Implementación de Pull-to-Refresh y suscripciones Realtime en Home, Pedidos, Solicitudes y Mensajes.
- **Chat Pro:** Listado de chats con badges de no leídos, vista previa del último mensaje, marcas de tiempo dinámicas y comparaciones insensibles a mayúsculas (UUID robustness).
- **Sistema de Reputación Blindado:** Calificaciones inmutables con visualización de histórico y bloqueo de duplicados.
- **Onboarding Dinámico:** Sistema de bienvenida interactivo basado en `HorizontalPager` para educar al usuario antes del registro.
- **Minimalismo de Marca:** Evitar duplicidad de marca (Logo + Texto) en cabeceras de pantallas de acceso para mantener la limpieza visual.
- **Gestión de Perfil:** Edición completa de datos sincronizada con esquema SQL (Cédula, Dirección, Nacimiento).
- **Publicación de Servicios:** Formulario para prestadores con lógica de categorías y costos de materiales.
- **Mis Servicios:** Panel de administración para que el prestador pueda editar o pausar (activar/desactivar) sus publicaciones existentes.
- **Dashboard del Prestador (Operatividad):** Interfaz de **Solicitudes Recibidas** terminada con acceso rápido desde el icono de notificaciones en el Home. Permite al prestador ver quién lo busca y gestionar el estado (Aceptar/Rechazar/Contraofertar/Finalizar) con persistencia en SQL.
- **Gestión Maestro de Horarios:** Pantalla de disponibilidad global que utiliza operaciones `upsert` y `delete` coordinadas para sincronizar la agenda del prestador con Supabase.

## 8. Próximos Hitos y Funcionalidades (Roadmap)
Para completar el MVP (Producto Mínimo Viable), se deben desarrollar los siguientes módulos:

### Hito 1: Gestión y Refinamiento Operativo
- **Sincronización de Disponibilidad:** Implementar lógica para que el selector de fecha/hora en la contratación valide contra la tabla `public.availability`.
- **Evolución SQL:** Añadir columna `final_price` a `public.requests` para registrar el acuerdo económico final tras la subasta.

### Hito 2: Comunicación y Confianza
- **Chat en Tiempo Real:** Integración con `Supabase Realtime` y tabla `messages`.
- **Sistema de Reputación:** Visualización de estrellas reales y envío de calificaciones (Tabla `ratings`) tras finalizar un servicio.

### Hito 3: Experiencia Multimedia (Storage)
- **Fotos de Perfil:** Subida y visualización de avatares reales mediante Buckets de Supabase Storage.
- **Galería de Servicios:** Permitir a los prestadores subir fotos de sus trabajos anteriores.

### Hito 4: Modelo SaaS y Legal
- **Suscripciones:** Pantalla de planes para prestadores y restricción de funcionalidades según suscripción.
- **Notificaciones:** Alertas Push para cambios en el estado de pedidos.
- **Cumplimiento:** Pantallas de Términos, Condiciones y Políticas de Privacidad.

---
*Propiedad Intelectual de **BH++** - 2026*
