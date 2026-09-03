# Ficha Técnica - YÁYA

## Información General
- **Nombre del Proyecto:** YÁYA
- **Versión Actual:** 1.1.0 (versionCode 5 - Play Store Ready)
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

## Funcionalidades Clave (v1.1.0)
1. **Cuenta Universal (No Doble Fricción):** Un mismo usuario puede actuar como Cliente y Prestador.
2. **Motor Centralizado de Validaciones de Datos (`ValidationUtils.kt`):** Validación estricta de nombres alfabéticos, DNI/CC (6 a 12 dígitos), teléfono de 10 dígitos, correo RFC/Patterns, contraseña segura, fechas no futuras y direcciones válidas.
3. **Estrategia de Filtrado Geográfico por Municipio/Zona (`municipality`):** Segmentación dinámica y selecciones desplegables inmutables (`ExposedDropdownMenuBox` / `ValidationUtils.HUILA_MUNICIPALITIES`) en Registro, Perfil, Creación de Servicios y Catálogo (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante, Todos).
4. **Módulo de Visualización de Reputación y Reseñas en Perfil:** Consulta e integración de la reputación de prestadores (`public.ratings`) en `ProfileHeroHeader`, `ProfileScreen` y `ProfileOptionItem` con hoja desplegable modal (`ModalBottomSheet`) de reseñas recibidas.
5. **Redirección Automática de Onboarding para Prestadores:** Enrutamiento inteligente post-registro hacia la pantalla de configuración de Jornada Maestra (`AvailabilityScreen`).
6. **Carga Inteligente de Disponibilidad y Prevención de Traslapes:** Botón de carga rápida de disponibilidad maestra y algoritmo de prevención de cruces de horarios entre servicios del mismo prestador.
7. **Chat en Tiempo Real:** Comunicación instantánea bidireccional vía Supabase Realtime.
8. **Dashboard Administrativo:** Panel de control para moderación de servicios y usuarios con sanciones progresivas.
9. **Catálogo y Negociación:** Exploración de talentos y flujo de contraofertas ("Handshake" digital).

## Entorno de Desarrollo y CI/CD
- **IDE:** Android Studio Ladybug | 2024.2.1+
- **Gradle Version:** 8.11+ / Gradle 9.5 Ready
- **Kotlin Version:** 2.4.10 (K2)
- **CI/CD:** GitHub Actions (CodeQL Analysis con Java 17 + Secret Injections)
- **Supabase SDK:** 3.6.0

---
*Documento de Propiedad Exclusiva de BH++*
