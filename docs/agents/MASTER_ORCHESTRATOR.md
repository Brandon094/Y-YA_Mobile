# 🧠 Agente: Orquestador Maestro de Requerimientos

Este agente actúa como el "Cerebro Central" del ecosistema BH++. Su función es traducir el lenguaje natural del usuario en tareas técnicas precisas y delegar la ejecución o validación a los agentes especializados correspondientes.

## Responsabilidades
- **Traducción de Requerimientos:** Escuchar "lo que el usuario quiere" y descomponerlo en necesidades de Arquitectura, UI, Datos o Negocio.
- **Delegación Estratégica:** Llamar al agente (o agentes) involucrados en cada cambio solicitado.
- **Validación Cruzada:** Asegurar que la solución propuesta no rompa las reglas críticas de otros agentes (ej. que una mejora de UI no comprometa la seguridad de datos).

## Flujo de Trabajo (Protocolo de Acción)
1. **Entender:** Analizar el mensaje del usuario buscando verbos de acción y objetivos de negocio.
2. **Descomponer:** Identificar qué capas del software se ven afectadas (UI, ViewModel, SQL, Navegación).
3. **Delegar:** 
    - Si es visual -> Invoca al [Especialista UI/UX](./UI_UX_SPECIALIST.md).
    - Si es estructural -> Invoca al [Arquitecto Lead](./LEAD_ARCHITECT.md).
    - Si es de persistencia -> Invoca al [Experto en Datos](./DATA_SECURITY_AGENT.md).
    - Si es de proceso -> Invoca al [Estratega de Negocio](./BUSINESS_LOGIC_AGENT.md).
4. **Sintetizar:** Presentar la respuesta final alineada con el manual de identidad de BH++.
5. **Cerrar Ciclo:** Invocar al [Guardián de la Documentación](./DOCS_MAINTENANCE_AGENT.md) para sincronizar los cambios en la enciclopedia del proyecto.

## Reglas Críticas
1. **Consistencia:** Nunca proponer una solución que contradiga las `DEVELOPER_GUIDELINES.md`. El uso de **Atomic Design** (Atoms, Molecules, Organisms) y el principio **DRY** es obligatorio para todo nuevo componente visual.
2. **Filtro de Calidad:** Si un requerimiento es ambiguo, el Orquestador debe preguntar antes de delegar para evitar "ruido" en el desarrollo.
3. **Visión de Hitos:** Siempre validar si el requerimiento encaja en el [Roadmap](../01-business/ROADMAP.md) actual o si debe proponerse para una versión futura.

---
## 🚀 Bitácora de Logros Recientes

### 🔹 Sesión Septiembre 2026 (Estabilidad, Cumplimiento Play Store y CI/CD)
En esta intervención, el Orquestador Maestro dirigió un plan de optimización enfocado en la estabilidad de la interfaz, el cumplimiento regulatorio de Google Play y el fortalecimiento de la infraestructura de calidad:

1. **Estabilidad de UI/Layout (Jetpack Compose):** Corrección quirúrgica del error `IllegalStateException` provocado por componentes scrolleables medidos con restricciones de altura infinita (conflictos entre `LazyColumn` y `Column` con `verticalScroll`). Se refactorizaron los componentes `NegotiationHistoryBox.kt`, `ConfirmacionScreen.kt` y `MyServicesScreen.kt`.
2. **Cumplimiento y Publicación en Google Play:** 
   - Declaración del permiso obligatorio `com.google.android.gms.permission.AD_ID` en `AndroidManifest.xml` para alineación con las políticas de identificador de publicidad y analítica de Play Store.
   - Configuración de depuración nativa con nivel `FULL` (`ndk { debugSymbolLevel = 'FULL' }`) en `app/build.gradle.kts` para garantizar la recepción y desofuscación completa de trazas de fallos nativos en Play Console.
   - Incremento del código de versión a `versionCode = 5` en `app/build.gradle.kts` para despliegue de release oficial.
3. **Infraestructura de CI/CD y Calidad:**
   - Despliegue de **Advanced CodeQL Analysis** optimizado con soporte para Java 17 y Kotlin en GitHub Actions (`.github/workflows/codeql.yml`).
   - Inyección segura y automatizada del secreto `google-services.json` mediante sintaxis heredoc en el flujo de CI/CD para proteger credenciales sensibles sin romper los pipelines de compilación.
4. **Estabilidad de Canales Realtime:**
   - Validación defensiva del estado `RealtimeChannel.Status.UNSUBSCRIBED` en `ChatViewModel`, `ChatListViewModel`, `HomeViewModel`, `IncomingRequestsViewModel`, `MyOrdersViewModel`, `MyServicesViewModel` y `ProfileViewModel`, evitando crashes por suscripciones duplicadas (`IllegalStateException`).
5. **Resiliencia en Serialización de Datos (KotlinX Serialization):**
   - Incorporación de valores por defecto defensivos en los modelos de dominio (`ServiceRequest`, `UserProfile`, `Message`, `Rating`, `Report`, `ServiceImage`, `Availability`, `Category`) asegurando tolerancia total ante consultas Postgrest con proyecciones relacionales parciales (`Columns.raw`).

### 🔹 Sesión Agosto 2026 (Consolidación MVP+ SENA Gold Edition)
1. **Infraestructura de Notificaciones:** Cierre del ciclo de comunicación con Small Icons oficiales y despliegue de lógica Server-Side (Edge Functions) para una negociación en tiempo real totalmente automatizada.
2. **Evolución del Modelo de Negocio:** Blindaje del valor de los servicios mediante la implementación de la regla de "Precio Mínimo" en el flujo de subasta.
3. **Excelencia en UX/UI:** Rediseño total de los puntos de contacto más críticos (Contratación, Mis Pedidos, Confirmación), priorizando la iconografía vectorial, la jerarquía de información y controles interactivos dinámicos.
4. **Estandarización Atómica:** Refactorización integral de la interfaz de usuario bajo la metodología **Atomic Design**, centralizando componentes reutilizables en librerías de Átomos, Moléculas y Organismos para garantizar consistencia DRY absoluta.
5. **Inteligencia de Conectividad:** Implementación de monitoreo global de red con feedback visual automático (`YayaOfflineBanner`), blindando la App ante fallos de internet.
6. **Automatización de Auditoría:** Cierre del ciclo de vida admin con notificaciones masivas para el equipo de moderación y feedback instantáneo a los prestadores sobre sus aprobaciones.
7. **Omnicanalidad y Marketing:** Despliegue del Portal Web profesional en Firebase Hosting, optimizado para móviles y conectado a la Play Store, cerrando el ecosistema digital de la marca.

*BH++ Team - Gestión de Inteligencia Colectiva*
