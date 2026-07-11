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
- **Requests:** Solo el cliente que solicita (`client_id`) y el prestador que recibe (`provider_id`) tienen acceso a ver y actualizar los detalles de una solicitud específica.

## 3. Protección de API Keys
- **Nivel de Acceso:** Se utiliza la `anon_key` de Supabase, la cual es pública por diseño pero está limitada por las políticas de RLS mencionadas anteriormente.
- **Entorno:** Aunque actualmente las llaves residen en el código (`SupabaseManager.kt`), la recomendación para producción es utilizar técnicas de ofuscación o inyección mediante archivos de propiedades no versionados.

## 4. Comunicación Segura
- Todas las peticiones al backend se realizan mediante **HTTPS**, asegurando el cifrado de los datos en tránsito (SSL/TLS).

## 5. Validación de Datos
- **Frontend:** Implementamos validaciones en tiempo real para correos, contraseñas y campos obligatorios antes de enviar cualquier petición al servidor.
- **Backend:** PostgreSQL aplica restricciones de integridad (Checks, Not Null, Unique) para evitar datos corruptos.

---
*Seguridad diseñada por el equipo BH++*
