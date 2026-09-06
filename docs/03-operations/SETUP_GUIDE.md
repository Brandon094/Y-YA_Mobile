# Guía de Configuración y Despliegue (RTC)

Este documento detalla los pasos necesarios para configurar el entorno de desarrollo y desplegar la aplicación YÁYA.

## 1. Prerrequisitos
- JDK 17 o superior.
- Android Studio Ladybug (2024.2.1) o superior.
- Cuenta en Supabase.

## 2. Configuración del Backend (Supabase)
1. Crear un nuevo proyecto en [Supabase Console](https://app.supabase.com/).
2. Ejecutar el script completo de `DATABASE_SCHEMA.md` en el SQL Editor para crear las 9 tablas y habilitar las políticas RLS.
3. Habilitar la autenticación por Email/Password.
4. Obtener la `SUPABASE_URL` y la `SUPABASE_ANON_KEY` desde Settings > API.

## 3. Configuración Local
1. Clonar el repositorio.
2. Crear un archivo `secrets.properties` en la raíz del proyecto (o configurar variables de entorno):
   ```properties
   SUPABASE_URL="https://tu-proyecto.supabase.co"
   SUPABASE_ANON_KEY="tu-anon-key"
   ```
3. Sincronizar Gradle.

## 4. Compilación y Ejecución
- Para depuración: `app:assembleDebug` y ejecutar en emulador.
- Para producción: Configurar firma de APK/Bundle y ejecutar `app:bundleRelease`.

## 5. Operatividad Administrativa
1. **Purga Atómica:** Es obligatorio crear la función RPC `admin_delete_user_account` en la base de datos (ver `SUPABASE_CONFIG_FIX.md`) para habilitar el borrado de usuarios desde el Panel Admin.

---
*Mantenimiento: Equipo BH++*
