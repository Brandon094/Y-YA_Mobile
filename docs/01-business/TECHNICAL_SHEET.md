# Ficha Técnica - YÁYA

## Información General
- **Nombre del Proyecto:** YÁYA
- **Versión Actual:** 1.0.1 (versionCode 5 - Play Store Ready)
- **Desarrollador:** BH++
- **Estado:** Producción / Play Store Ready
- **Plataforma:** Android

## Especificaciones de Software
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 37 (Android 16 API 37)
- **Lenguaje:** Kotlin 2.4.10 (K2 / JVM Target 17)
- **Base de Datos:** PostgreSQL (vía Supabase)
- **Autenticación:** Supabase Auth
- **Notificaciones:** Firebase Cloud Messaging (FCM V1)

## Funcionalidades Clave (v1.0.1)
1. **Cuenta Universal (No Doble Fricción):** Un mismo usuario puede actuar como Cliente y Prestador.
2. **Chat en Tiempo Real:** Comunicación instantánea bidireccional vía Supabase Realtime.
3. **Sistema de Reputación:** Calificaciones y reseñas dinámicas post-servicio.
4. **Notificaciones Push:** Alertas automáticas para nuevas solicitudes y mensajes.
5. **Dashboard Administrativo:** Panel de control para moderación de servicios y usuarios con sanciones progresivas.
6. **Catálogo y Negociación:** Exploración de talentos y flujo de contraofertas ("Handshake" digital).

## Entorno de Desarrollo y CI/CD
- **IDE:** Android Studio Ladybug | 2024.2.1+
- **Gradle Version:** 8.11+ / Gradle 9.5 Ready
- **Kotlin Version:** 2.4.10 (K2)
- **CI/CD:** GitHub Actions (CodeQL Analysis con Java 17 + Secret Injections)
- **Supabase SDK:** 3.6.0

---
*Documento de Propiedad Exclusiva de BH++*
