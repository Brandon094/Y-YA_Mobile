# Guía de Configuración y Despliegue (RTC)

Este documento detalla los pasos necesarios para configurar el entorno de desarrollo y desplegar la aplicación YÁYA.

## 1. Prerrequisitos
- JDK 17 o superior.
- Android Studio Ladybug (2024.2.1) o superior.
- Cuenta en Supabase.

## 2. Configuración del Backend (Supabase)
1. Crear un nuevo proyecto en [Supabase Console](https://app.supabase.com/).
2. Configurar las tablas iniciales en la base de datos (ver `schema.sql` si está disponible).
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

---
*Mantenimiento: Equipo BH++*
