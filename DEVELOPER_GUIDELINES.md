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
- **Feedback:** Mostrar siempre un `CircularProgressIndicator` durante las llamadas a Supabase.

### C. Seguridad y Roles
- **Control de Acceso:** La interfaz debe ocultar/mostrar elementos según el campo `role` del perfil del usuario (`client`, `provider`, `admin`).
- **Persistencia:** La sesión debe inicializarse en `MainActivity` mediante `SupabaseManager.initialize(context)`.

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
- **Autenticación:** Flujo completo de Login, Registro (con validaciones de email/password) y Recuperación de clave.
- **Persistencia:** Sesión persistente mediante `multiplatform-settings`.
- **Navegación:** Sistema centralizado en `AppNavigation.kt` usando Type-Safety.
- **Pantallas Core:** 
    - `Splash/Loading`: Con logo completo y fondo elegante.
    - `Home`: Con barra de búsqueda, filtro por categorías dinámico y control de acceso (el botón '+' solo aparece para prestadores/admin).
    - `Profile/EditProfile`: Lectura y edición de datos del perfil.
    - `CreateService`: Formulario vinculado a la tabla `services` y `categories` de Supabase.

## 8. Próximos Hitos y Funcionalidades (Roadmap)
Para completar el MVP (Producto Mínimo Viable), se deben desarrollar los siguientes módulos:

### Hito 1: Gestión y Seguimiento (Dashboards)
- **Mis Pedidos:** Historial de servicios contratados por el cliente y su estado.
- **Solicitudes Entrantes:** Panel para que el prestador acepte/rechace trabajos.
- **Mis Servicios:** Interfaz para que el prestador gestione sus publicaciones existentes.

### Hito 2: Comunicación y Confianza
- **Chat en Tiempo Real:** Comunicación directa cliente-prestador (Tabla `messages`).
- **Sistema de Reputación:** Visualización y envío de calificaciones (Tabla `ratings`).

### Hito 3: Monetización y Legal
- **Suscripciones (SaaS):** Pantalla de selección de planes para prestadores.
- **Cumplimiento:** Pantalla de Términos, Condiciones y Políticas de Privacidad.

### Hito 4: Experiencia Pro
- **Multimedia:** Subida de fotos de perfil y servicios a Supabase Storage.
- **Notificaciones:** Sistema de alertas para actualizaciones de pedidos y mensajes.

---
*Propiedad Intelectual de **BH++** - 2026*
