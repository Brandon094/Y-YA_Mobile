# Ficha Técnica - YÁYA

## Información General
- **Nombre del Proyecto:** YÁYA
- **Versión Actual:** 1.2.0 (versionCode 7)
- **Desarrollador:** BH++
- **Estado:** 🟢 Producción / Play Store Release (v1.2.0 - versionCode 7)
- **Plataforma:** Android

## Especificaciones de Software
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 37 (Android 16 API 37)
- **Lenguaje:** Kotlin 2.4.10 (K2 / JVM Target 17)
- **Base de Datos:** PostgreSQL (vía Supabase)
- **Autenticación:** Supabase Auth
- **Notificaciones:** Firebase Cloud Messaging (FCM V1)

## Funcionalidades Clave (v1.2.0)
1. **Cuenta Universal (No Doble Fricción):** Un mismo usuario puede actuar como Cliente y Prestador.
2. **Motor de Tutoriales In-App Spotlight (ShowOnce):** Guía interactiva en 8 pantallas con recorte transparente e Anillo de Luz para facilitar la curva de aprendizaje.
3. **Wizard de Creación de Servicios 2.0:** Formulario intuitivo en 2 pasos con barra de progreso y duración compuesta estructurada (Número + Unidad de tiempo).
4. **Seguridad de Registro Avanzada:** Verificación previa (*Pre-flight Check*) de cédula/correo para evitar duplicados antes del registro en Auth y restricción de edad mínima (15 años) con validación en calendario.
5. **Panel Administrativo 2.0:** Gestión directa de usuarios (Suspensión/Reactivación) y Borrado Atómico en Cascada vía RPC en Postgres para garantizar la integridad referencial.
6. **Motor Centralizado de Validaciones de Datos (`ValidationUtils.kt`):** Validación estricta de nombres alfabéticos, DNI/CC (6 a 12 dígitos), teléfono de 10 dígitos, correo RFC/Patterns, contraseña segura, fechas no futuras y direcciones válidas.
7. **Estrategia de Filtrado Geográfico por Municipio/Zona (`municipality`):** Segmentación dinámica y selecciones desplegables inmutables en Registro, Perfil, Creación de Servicios y Catálogo.
8. **Módulo de Visualización de Reputación y Reseñas en Perfil:** Consulta e integración de la reputación de prestadores en `ProfileHeroHeader` y `ProfileScreen` con hoja desplegable modal de reseñas.
9. **Redirección Automática de Onboarding para Prestadores:** Enrutamiento inteligente post-registro hacia la pantalla de configuración de disponibilidad.
10. **Chat en Tiempo Real:** Comunicación instantánea bidireccional vía Supabase Realtime.
11. **Catálogo y Negociación:** Exploración de talentos y flujo de contraofertas ("Handshake" digital).

## Entorno de Desarrollo y CI/CD
- **IDE:** Android Studio Ladybug | 2024.2.1+
- **Gradle Version:** 8.11+ / Gradle 9.5 Ready
- **Kotlin Version:** 2.4.10 (K2)
- **CI/CD:** GitHub Actions (CodeQL Analysis con Java 17 + Secret Injections)
- **Supabase SDK:** 3.6.0

---
*Documento de Propiedad Exclusiva de BH++*
