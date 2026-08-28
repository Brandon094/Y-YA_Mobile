# Modelo de Seguridad - YÁYA

Este documento describe las medidas de seguridad implementadas en YÁYA para proteger la integridad de los datos y la privacidad de los usuarios.

## 1. Autenticación y Sesiones
- **Proveedor:** Utilizamos **Supabase Auth** para la gestión de usuarios.
- **Persistencia:** La sesión del usuario se guarda localmente mediante `SharedPreferencesSettings`, permitiendo que el usuario no deba re-autenticarse al cerrar y abrir la aplicación.
- **Recuperación:** Se implementa un flujo de restablecimiento de contraseña mediante correos electrónicos gestionados por Supabase.

## 2. Seguridad en Base de Datos (RLS)
El sistema utiliza **Row Level Security (RLS)** de PostgreSQL para asegurar que los datos no sean accedidos de forma no autorizada:
- **Profiles:** Un usuario solo puede editar su propio perfil (donde `auth.uid() = id`).
- **Services:** 
    - **Lectura:** Usuarios autenticados ven servicios con `status = 'active'`. Dueños ven sus servicios en cualquier estado.
    - **Inserción:** Usuarios autenticados pueden crear servicios vinculados a su propio `auth.uid()`.
    - **Actualización:** Solo el dueño (`provider_id`) puede editar sus servicios.
    - **Administración:** Los perfiles con rol `admin` tienen bypass de RLS para moderación total.
- **Requests:** Solo el cliente que solicita (`client_id`) y el prestador que recibe el servicio (`provider_id` vía join) pueden ver y actualizar el estado o precio de una solicitud (Negociación).
- **Messages:** 
    - **Lectura:** Usuarios solo ven mensajes donde son remitentes o destinatarios.
    - **Inserción:** Usuarios solo pueden enviar mensajes bajo su propio ID.
    - **Actualización:** Solo el destinatario puede marcar un mensaje como leído (`is_read = true`).
- **Availability:** Lectura pública para usuarios autenticados; edición restringida exclusivamente al dueño del perfil de prestador.
- **Ratings:** Lectura pública; inserción restringida al cliente que realizó la solicitud del servicio.
- **Reports:** Cualquier usuario puede reportar; visualización restringida al autor del reporte y a los administradores.
- **Storage (Buckets):**
    - **avatars:** Lectura pública; escritura/eliminación restringida al dueño del perfil (`auth.uid()`).
    - **portfolios:** Lectura pública; escritura/eliminación restringida al prestador dueño del servicio.

## 3. Protección de API Keys
- **Nivel de Acceso:** Se utiliza la `anon_key` de Supabase, la cual es pública por diseño pero está limitada por las políticas de RLS mencionadas anteriormente.
- **Entorno:** Aunque actualmente las llaves residen en el código (`SupabaseManager.kt`), la recomendación para producción es utilizar técnicas de ofuscación o inyección mediante archivos de propiedades no versionados.

## 4. Comunicación Segura
- Todas las peticiones al backend se realizan mediante **HTTPS**, asegurando el cifrado de los datos en tránsito (SSL/TLS).

## 5. Validación de Datos
- **Frontend:** Implementamos validaciones en tiempo real para correos, contraseñas y campos obligatorios antes de enviar cualquier petición al servidor.
- **Backend:** PostgreSQL aplica restricciones de integridad (Checks, Not Null, Unique) para evitar datos corruptos.

## 6. Privacidad y Anonimato Admin
- **Enmascaramiento de Moderadores:** Para prevenir represalias o contacto no deseado fuera de la plataforma, el sistema enmascara la identidad de los administradores. En el chat con usuarios, el nombre se muestra como "Equipo de Moderación" y el avatar es reemplazado por el isotipo institucional.
- **Auditoría Multi-Admin:** Las notificaciones de auditoría se distribuyen a todos los administradores simultáneamente sin revelar qué administrador específico tomó una acción correctiva.

---
*Seguridad diseñada por el equipo BH++*
