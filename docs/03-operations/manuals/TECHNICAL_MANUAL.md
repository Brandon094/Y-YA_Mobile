# Documentación Técnica Senior - YÁYA

## 1. Introducción
YÁYA es una infraestructura móvil de alta gama diseñada para la intermediación de servicios locales. Este manual técnico detalla el ecosistema de componentes, patrones y estándares de ingeniería aplicados.

## 2. Arquitectura de Software: Clean MVVM + Atomic Design
El proyecto implementa una arquitectura híbrida que combina la robustez de **MVVM** con la flexibilidad de **Atomic Design**.

### 2.1. Descomposición de la Interfaz (UI Stack)
La capa de presentación reside en `ui/` y se rige por la jerarquía atómica:
- **Átomos:** Componentes elementales de Material 3 personalizados (Buttons, TextFields, Branding).
- **Moléculas:** Componentes reactivos como el `PriceNegotiator` y `NegotiationHistoryBox`.
- **Organismos:** Módulos maestros como `ServiceCard`, `AdminServiceCard` y `ReportSummaryCard`.
- **Skeletons:** Infraestructura de `Shimmer.kt` que garantiza transiciones de estado suaves (alpha 0.25).

### 2.2. Capa de Negocio (ViewModel Layer)
Los ViewModels actúan como controladores de estado puro:
- **State Injections:** Inyección de modelos `UiState` que encapsulan la verdad de la pantalla.
- **Formatter Engine:** Consumo centralizado de `FormatterUtils.kt` para garantizar el cumplimiento del principio DRY en la transformación de datos crudos de Supabase.

## 3. Stack Tecnológico de Vanguardia
- **Kotlin 2.2.10:** Aprovechando las últimas optimizaciones del compilador.
- **Jetpack Compose:** Sistema declarativo para una UI elástica y accesible.
- **Supabase Enterprise Stack:**
    - **PostgreSQL:** Almacenamiento relacional robusto.
    - **Realtime Engine:** Sincronización bidireccional mediante WebSockets.
    - **Edge Functions:** Lógica server-side en TypeScript para automatización de notificaciones FCM V1.
- **Connectivity Flow:** Sistema de monitoreo global de red basado en Kotlin Flow (`ConnectivityObserver`). Informa al usuario de desconexiones en tiempo real mediante el `YayaOfflineBanner`.
- **Coil 3.1.0:** Motor de renderizado de imágenes asíncrono con caché inteligente.

## 4. Estándares de Codificación BH++
- **Arquitectura Stateless:** Minimización de efectos secundarios en la UI.
- **Accessibility First:** Blindaje nativo para escalas de fuente al 200%.
- **Handshake Logic:** Ciclo transaccional de seguridad tripartito para blindar acuerdos comerciales.

## 5. Gobernanza y Seguridad
- **Account Purge:** Proceso de borrado de cuenta integrado en el cliente para cumplimiento con normativas de privacidad (Google/Apple).
- **RLS (Row Level Security):** Políticas de base de datos que garantizan que un usuario solo pueda editar su propia información.
- **Admin Warning Logic:** Implementación de la función `warnUser` en `AdminViewModel` para inyectar advertencias automatizadas en la tabla `messages`, reduciendo la carga operativa del equipo de moderación.

## 6. Sistema de Notificaciones y Conectividad
- **Push Multi-Admin:** La Edge Function `notify-yaya-updates` permite el envío masivo de notificaciones a todos los administradores ante nuevos servicios por auditar.
- **Connectivity Observer:** Implementación de `NetworkConnectivityObserver.kt` que emite estados de red mediante Flows. Integrado con `YayaOfflineBanner` para feedback preventivo.

---
*Manual de Ingeniería de BH++ - 2026*
