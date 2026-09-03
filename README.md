# 🚀 YÁYA - Conecta. Confía. Contrata.

**YÁYA** es una plataforma móvil de vanguardia diseñada bajo estándares de ingeniería **Senior Enterprise** (Versión `1.1.0` - `versionCode 5` - *Play Store Ready*). Nuestra misión es conectar talentos independientes con usuarios mediante una experiencia **Premium, Inclusiva y Segura**, apalancada en una arquitectura de software impecable.

---

## 🏛️ Pilares de Ingeniería (La "Biblia" de YÁYA)

El proyecto se rige por cuatro principios fundamentales que garantizan su escalabilidad y calidad:

1.  **Atomic Design System**: La interfaz no es un conjunto de pantallas, sino un ecosistema de componentes jerárquicos (**Atoms**, **Molecules**, **Organisms**). Esto garantiza consistencia visual absoluta y un mantenimiento centralizado.
2.  **Clean MVVM**: Separación estricta de responsabilidades. Las vistas son **100% pasivas (Stateless)** y toda la lógica de negocio, transformación de datos y toma de decisiones reside en los ViewModels.
3.  **Filosofía DRY (Don't Repeat Yourself)**: Uso intensivo de utilidades centralizadas (`ValidationUtils`, `FormatterUtils`, `ImageUtils`) y componentes reutilizables para eliminar la redundancia de código.
4.  **Premium UX, Accesibilidad & Contraste Adaptativo**: Implementación de **Skeleton Screens (Shimmers)** para cargas fluidas, soporte nativo para **fuentes al 200%** mediante layouts elásticos, contraste dinámico en Tema Oscuro (Deep Midnight) y un flujo de negociación con **Handshake Digital** para máxima seguridad transaccional.

---

## ✨ Novedades Destacadas de la Versión v1.1.0 (versionCode 5)

- **Motor Centralizado de Validaciones (`ValidationUtils.kt`):** Control unificado DRY y MVVM para nombres alfabéticos, DNI/CC (6-12 dígitos), teléfono (10 dígitos), correo RFC, clave segura, fechas de nacimiento no futuras y citas válidas.
- **Filtrado Geográfico por Municipio (`HUILA_MUNICIPALITIES`):** Segmentación regional con desplegables inmutables `ExposedDropdownMenuBox` en registro, perfil, creación de servicios y selector modal de catálogo (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante, Todos).
- **Reputación y Reseñas ⭐ en Perfil y Tarjetas:** Integración de la tabla `public.ratings` para calcular y visualizar el promedio y total de opiniones en la cabecera del perfil, las tarjetas de servicio (`ServiceCard`) y la sección "Mi Reputación y Reseñas" con modal desplegable (`ModalBottomSheet`).
- **Rediseño UI/UX 2.0 del Perfil de Usuario (`ProfileScreen`):** Hero Header 2.0 con botón flotante de edición, Quick Action Cards (*Mis Servicios*, *Solicitudes* con badge de pendientes, y *Reputación* ⭐ 4.9) y navegación modular mediante `TabRow` (*"💼 Mi Operación"* y *"⚙️ Ajustes y Ayuda"*).
- **Visor del Manual de Uso Integrado por Roles:** Segregación dinámica por rol (`client`, `provider`, `admin`) en `LegalViewerScreen` con estilo formal Markdown sin emojis (`ManualConstants`).
- **Onboarding de Prestadores y Control de Traslapes Horarios:** Redirección automática post-registro hacia la Jornada Maestra (`AvailabilityScreen`), precarga rápida por servicio e identificación/bloqueo de solapamientos horarios.
- **Contraste Adaptativo en Tema Oscuro:** Adaptación dinâmica de textos y enlaces (`MaterialTheme.colorScheme.primary` y `onBackground`) en `LoginScreen` y `RegisterUserScreen`.

---

## 📚 Centro de Documentación Estratégica

Toda la inteligencia del proyecto está organizada para una auditoría técnica inmediata:

### 🔹 [01. Visión de Negocio & Producto](./docs/01-business/)
- **[Portal Web (Live)](https://y-ya-d5929.web.app):** Nuestra cara al mundo (Alojado en Firebase).
- **[Ficha Técnica v1.1.0](./docs/01-business/TECHNICAL_SHEET.md):** Especificaciones de plataforma y capacidades de release.
- **[Identidad de Marca (Branding Pro)](./docs/01-business/BRANDING.md):** Manual de estilo y tokens de diseño.
- **[Requisitos de Ingeniería](./docs/01-business/REQUIREMENTS.md):** RF y RNF detallados bajo norma ISO.
- **[Roadmap de Evolución](./docs/01-business/ROADMAP.md):** Hitos alcanzados y visión de escalabilidad (Estado: Producción / Play Store Ready).

### 🔹 [02. Arquitectura & Modelado de Datos](./docs/02-architecture/)
- **[Arquitectura de Software (Master Doc)](./docs/02-architecture/SOFTWARE_ARCHITECTURE.md):** Detalle de patrones, Atomic Design y flujo reactivo.
- **[Modelo de Seguridad & RLS](./docs/02-architecture/SECURITY_MODEL.md):** Protección de datos a nivel de fila y políticas Supabase.
- **[Diccionario de Datos](./docs/02-architecture/DATA_DICTIONARY.md):** La verdad técnica de nuestras tablas y relaciones.

### 🔹 [03. Excelencia Operativa & Desarrollo](./docs/03-operations/)
- **[Guía de Lanzamiento Play Store](./docs/03-operations/PLAY_STORE_CHECKLIST.md):** Lista de verificación para publicación oficial.
- **[Manual Técnico Senior](./docs/03-operations/manuals/TECHNICAL_MANUAL.md):** Biblia del stack tecnológico, motor de validaciones, filtrado geográfico y estándares de codificación.
- **[Manual del Usuario Final](./docs/03-operations/manuals/USER_MANUAL.md):** Guía formal por roles (Cliente, Prestador, Administrador) sin emojis.
- **[Manual Maestro de Administración](./docs/03-operations/manuals/ADMIN_MANUAL.md):** Protocolo de auditoría, moderación y semáforo disciplinario.
- **[Guía del Desarrollador (Instrucciones de Agente)](./docs/03-operations/DEVELOPER_GUIDELINES.md):** Reglas de oro inmutables del código.
- **[Changelog Histórico (v1.1.0)](./docs/03-operations/CHANGELOG.md):** Trazabilidad total de cada mejora e innovación implementada.
- **[Ecosistema de Agentes](./docs/agents/README.md):** Gobernanza mediante IA especializada.

### 🔹 [05. Ecosistema Web & Marketing](./docs/05-web-portal/)
- **[Arquitectura Web](./docs/05-web-portal/WEB_ARCHITECTURE.md):** Detalle del motor atómico JS y hosting Firebase.
- **[Estrategia SEO & Conversión](./docs/05-web-portal/MARKETING_STRATEGY.md):** Cómo YÁYA domina los resultados de Google.

---

## 🛠️ Stack Tecnológico de Alto Nivel

- **Core:** Kotlin 2.4.10 (Coroutines & Flow)
- **Target SDK:** Android API 37 (minSdk 26, versionCode 5, versionName "1.1.0")
- **UI Framework:** Jetpack Compose (Material 3)
- **Design System:** Atomic Design Methodology
- **Backend-as-a-Service:** Supabase (Auth, PostgreSQL, Realtime, Storage, Edge Functions)
- **Image Engine:** Coil 3.1.0
- **Navigation:** Type-Safe Jetpack Navigation

---

## 🤝 Colaboración BH++

Este proyecto es una obra de **BH++ - Senior Software Engineering**. Para contribuciones, respete estrictamente los estándares de **Atomic Design** y **Conventional Commits** definidos en nuestra guía técnica.

---
*Desarrollado con excelencia por **BH++***
