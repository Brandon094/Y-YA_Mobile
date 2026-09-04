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

### 🔹 Sesión Septiembre 2026 - v1.1.0 (Feature Release: Motor de Validaciones, Estandarización Geográfica por Municipio, Reputación en Perfil e Incremento de Versión)
En esta intervención, el Orquestador Maestro dirigió la implementación del motor centralizado de validaciones de datos (`ValidationUtils.kt`), la estandarización de componentes de ubicación por municipios, el perfeccionamiento del flujo de horarios por servicio, el onboarding del prestador, la prevención de traslapes en la asignación de disponibilidad, la integración de reputación y reseñas en el perfil, y la preparación del release oficial v1.1.0 (versionCode 5):

1. **Estrategia de Filtrado Geográfico por Municipio/Zona y Estandarización de Desplegables (`ExposedDropdownMenuBox`):**
   - Evolución de los modelos de dominio `UserProfile` y `Service` incorporando la propiedad opcional `municipality: String?` (con valor por defecto "La Plata").
   - Estandarización de la lista oficial de municipios de cobertura del Huila en `ValidationUtils.HUILA_MUNICIPALITIES`.
   - Reemplazo de campos de texto libre por selecciones desplegables inmutables (`ExposedDropdownMenuBox`) en `RegisterUserScreen`, `EditProfileScreen` y `CreateServiceScreen`, eliminando errores de tipeo e inconsistencias en la captura de datos de ubicación.
   - Sincronización del diálogo modal de filtro geográfico en `HomeScreen` consumiendo la fuente estandarizada de `ValidationUtils`.
   - Lógica de filtrado dinámico en `HomeViewModel.applyFilters()` que restringe el catálogo de servicios según el municipio seleccionado (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante, Todos).
   - **Corrección de Condición de Carrera en Filtro Geográfico (`HomeViewModel.loadData`):** Reorganización de la secuencia asíncrona de inicialización en `HomeViewModel.loadData()`. Se garantiza la descarga del perfil del usuario (`userProfile`) y la asignación previa de `selectedMunicipality` con su municipio real **antes** de invocar `applyFilters()`, eliminando el problema por el cual la carga inicial se filtraba erróneamente por el valor por defecto ("La Plata") y requería un refresco manual (pull-to-refresh).
   - **Optimización de Desplazamiento en Selector Modal de Municipios (`HomeScreen`):** Sustitución de la estructura rígida `Column` por `LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp))` dentro del diálogo modal `AlertDialog` de selección de municipios en `HomeScreen`. Esto elimina el desbordamiento de pantalla en dispositivos pequeños o listas largas, garantizando un desplazamiento vertical fluido y acceso completo al 100% de la lista de municipios de cobertura (incluyendo opciones finales como Neiva, Pitalito y Todos).
2. **Flujo de Onboarding del Prestador y Carga Inteligente de Disponibilidad por Servicio:**
   - **Onboarding Post-Registro de Prestadores:** Redirección automática de nuevos usuarios registrados con el rol `provider` hacia la pantalla de configuración de Jornada Maestra (`AvailabilityScreen`), asegurando la definición del horario base de atención antes de publicar talentos.
   - **Carga Automatizada de Jornada Maestra:** Incorporación del botón de acción rápida *"Cargar mi jornada maestra"* en `CreateServiceScreen`, que puebla automáticamente los días de prestación (`working_days`) basándose en `masterWorkingDays` recuperados desde `public.availability`.
   - **Detección y Prevención de Traslapes:** Implementación en `CreateServiceViewModel.loadProviderAvailabilityAndServices()` de un algoritmo que recupera los días ocupados y sus rangos horarios por otros servicios activos del mismo prestador (`occupiedDaysByOtherServices`).
   - **Feedback Contextual en Pantalla con Rangos Horarios:** Resaltado visual en el selector de días mediante colores de alerta (`errorContainer`) para días previamente comprometidos, acompañado de un mensaje informativo claro que detalla los títulos de los servicios asignados junto con sus rangos horarios activos (ej: `Desarrollo de aplicaciones móviles (08:00 - 18:00)`), dándole visibilidad completa para saber en qué horas del día tiene disponibilidad libre para ofertar otro servicio.
   - **Validación Estricta y Seguridad Horaria Previa al Guardado (`CreateServiceViewModel`):**
     - *Secuencia Horaria:* Verificación de que la hora de inicio (`startTime`) sea estrictamente anterior a la hora de fin (`endTime`).
     - *Conformidad con Jornada Maestra:* Comprobación de que todos los días asignados (`workingDays`) estén incluidos en la disponibilidad maestra del prestador (`public.availability`) y que el rango de horas esté contenido dentro de los límites de la jornada maestra (`masterStart` - `masterEnd`) para cada día.
     - *Validación Estricta de Traslape Horario:* Evaluación de colisiones temporales entre servicios activos compartidos mediante `ValidationUtils.isTimeRangeOverlapping`. En caso de solapamiento, se bloquea la creación/edición del servicio notificando el conflicto específico (ej: *"Conflicto de horario el Lunes: Ya tienes el servicio 'Servicio X' de 08:00 a 14:00"*).
3. **Arquitectura y Flujo Optimizado de Horarios y Días por Servicio:**
   - Separación estricta entre la Jornada Maestra de Disponibilidad del Prestador (`public.availability`) y los días (`working_days`) y horarios de atención asignados específicamente a cada talento o servicio (`public.services`).
   - Sincronización de validación cruzada en `ContratacionViewModel` que restringe el calendario de agendamiento a los días permitidos por servicio y bloquea opciones de horario fuera de rango o pasadas.
4. **Migración de Esquema de Base de Datos (Supabase PostgreSQL):**
   - Incorporación de las sentencias SQL de migración DDL (`ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS municipality text DEFAULT 'La Plata';` y `ALTER TABLE public.services ADD COLUMN IF NOT EXISTS municipality text DEFAULT 'La Plata';`).
5. **Incremento de Versión, Etiquetado Git y Publicación:**
   - Actualización de versión de la aplicación a `versionName = "1.1.0"` y `versionCode = 5` en `app/build.gradle.kts` para despliegue en Google Play Store.
   - **Registro de Etiqueta Git (Release Tag `v1.1.0`):** Etiquetado oficial del Release Tag `v1.1.0` en Git para GitHub con el mensaje `"Lanzamiento Oficial YÁYA v1.1.0 (versionCode 5)"`, oficializando el Hito de Versión Oficial en Producción / Play Store Ready (`versionCode = 5`, `versionName = "1.1.0"`).
6. **Corrección de Persistencia de Disponibilidad General (`AvailabilityViewModel` & `AvailabilityScreen`):**
   - Eliminación del error `null value in column "id" violates not-null constraint` en Supabase PostgreSQL mediante la generación de UUIDs de cliente (`java.util.UUID.randomUUID()`) previas al `upsert` cuando no existe un ID preexistente.
   - Normalización del formato de horas a 8 caracteres (`"HH:mm:ss"`) para cumplir con la restricción estricta del tipo SQL `time without time zone`.
   - Adición de un contenedor de error visual en `AvailabilityScreen` para presentar mensajes de excepción contextuales y retroalimentación clara al prestador en la UI ante fallos de guardado.
7. **Resolución de Violación de Restricción `NOT NULL` en `day_of_week` (`Availability.kt`):**
   - Eliminación de valores por defecto en las propiedades del modelo `Availability.kt` (`day_of_week`, `provider_id`, `start_time`, `end_time`) para evitar que `kotlinx.serialization` omita campos en el JSON enviado a Postgrest cuando su valor coincide con el valor por defecto (`day_of_week = 1`), erradicando el fallo `null value in column "day_of_week" violates not-null constraint`.
   - Reafirmación en la documentación de la arquitectura del modelo de disponibilidad vs. servicios:
     - `public.availability`: Marco o Jornada Maestra del Prestador (días/horas globales de disponibilidad).
     - `public.services`: Horarios y días específicos asignados a cada servicio en particular dentro de dicha jornada (ej. Mañana: Panadería / Tarde: Programación).
8. **Validación Reactiva en Tiempo Real y Deshabilitación Visual de Días fuera de Jornada Maestra (`CreateServiceScreen` & `CreateServiceViewModel`):**
   - **Deshabilitación Visual de Días fuera de Jornada Maestra:** En el selector de días (`FlowRow`), los días que no forman parte de la disponibilidad maestra del prestador (`masterWorkingDays`) se deshabilitan visualmente (`alpha = 0.3f`) y se desacoplan de interacción (`clickable(enabled = false)`), impidiendo que el usuario seleccione días en los que no trabaja.
   - **Validación Reactiva y Botón de Guardado Dinámico (`CreateServiceViewModel.validateServiceData`):** Evaluación reactiva `currentValidationError` calculada mediante `remember(...)` invocando a `validateServiceData`. Ante cualquier fallo detectado en tiempo real (días no incluidos en la jornada maestra, horarios fuera del rango maestro, hora de inicio >= hora de fin o traslape con otros servicios del prestador), se muestra un banner descriptivo de error en la pantalla y el botón *"Publicar Servicio / Guardar Cambios"* permanece estrictamente deshabilitado (`enabled = !isLoading && currentValidationError == null`).
9. **Refinamiento Semántico y Reorganización Visual de `ServiceCard` (Alineación con Reputación `public.ratings`):**
   - **Reubicación de `RatingIndicator` a Cabecera de Prestador:** La estrella e indicador numérico promedio de calificaciones se trasladaron a la cabecera de `ServiceCard`, directamente debajo del nombre del prestador (`state.domain.provider?.full_name`).
   - **Alineación con Tabla `public.ratings` (Reputación del Talento/Provider):** Esta mejora de UI alinea la interfaz con el modelo relacional de Supabase PostgreSQL, donde la tabla `public.ratings` evalúa la reputación global del prestador (`provider_id`), erradicando la ambigüedad previa donde la calificación aparentaba pertenecer al servicio individual.
   - **Reorganización de Footer:** La categoría del servicio y el indicador de días de disponibilidad se reubicaron en el pie de página de la tarjeta (`ServiceCard`) para un layout visualmente equilibrado e intuitivo.
10. **Visualización e Integración de Reputación y Reseñas en el Perfil (`ProfileViewModel`, `ProfileHeroHeader`, `ProfileScreen`, `ProfileOptionItem`):**
    - **Consulta y Cálculo de Reputación (`ProfileViewModel`):** Invocación a `public.ratings` filtrando por `provider_id` en `fetchProviderRatings` para calcular `averageRating` (promedio de estrellas), `totalRatings` (conteo de opiniones) y cargar la lista cronológica `providerRatings` para usuarios con rol `provider` o `admin`.
    - **Visualización en Cabecera Hero (`ProfileHeroHeader`):** Incorporación del componente `RatingIndicator` en la cabecera del perfil junto al badge de rol cuando el usuario es `provider` o `admin`.
    - **Sección "Mi Reputación y Reseñas" y Modal de Opiniones (`ProfileScreen` & `ProfileOptionItem`):** Integración de la opción *"Mi Reputación y Reseñas"* en la sección *"MI TALENTO"* del perfil con badge textual del promedio (ej. `⭐ 4.9 (18)` o `"Sin opiniones"`) mediante la extensión de `ProfileOptionItem` (`badgeText`). Al presionar la opción, se despliega un `ModalBottomSheet` con el listado scrolleable de reseñas recibidas (`YayaRatingItem`).
11. **Visor del Manual de Uso Integrado en la App con Segregación Estricta por Rol (`ManualConstants.kt`, `LegalViewerScreen`, `ProfileScreen`, `AppNavigation`):**
    - **Segregación Estricta por Rol (`ManualConstants.getManualContentForRole`):** Creación de la lógica de bifurcación de manuales en `ManualConstants.kt` que suministra contenido personalizado según el rol del usuario autenticado:
      * *Cliente (`role == "client"`):* Visualiza únicamente el "Manual para Clientes", enfocado en búsqueda local por municipio, agendamiento futuro inteligente, negociación "Handshake", chat y calificaciones.
      * *Prestador (`role == "provider"`):* Visualiza el "Manual para Prestadores", que combina las funciones de cliente con la gestión de talentos, asignación de municipios de atención, jornada maestra, detector de traslapes horarios y consulta de reputación en su perfil.
      * *Administrador (`role == "admin"`):* Visualiza el "Manual Maestro de YÁYA", incluyendo la auditoría de calidad de publicaciones, moderación con semáforo disciplinario (Amarillo/Naranja/Rojo), llamados de atención automáticos por chat y la capa de anonimato protegido "Equipo de Moderación YÁYA".
    - **Navegación Dinámica por Rol (`AppNavigation.kt`):** Enrutamiento en `UserManualRoute` de `AppNavigation.kt` que resuelve asíncronamente el rol activo (`activeRole`) del usuario desde Supabase Postgrest/Metadata y parametriza `LegalViewerScreen` asignando títulos adaptativos ("Manual para Clientes", "Manual para Prestadores", "Manual Maestro de YÁYA") y el contenido correspondiente.
    - **Estilo Formal, Ejecutivo y Técnico Sin Emojis:** Eliminación total de emojis en todos los textos de manuales (`ManualConstants.kt`, `USER_MANUAL.md` y `ADMIN_MANUAL.md`) adoptando un formato legal Markdown riguroso para cumplir con los estándares de documentación formal de BH++ Team.
    - **Acceso Inmediato desde Perfil (`ProfileScreen`):** Incorporación de la opción "Manual de Uso de la App" en el menú de Perfil con el icono `Icons.AutoMirrored.Filled.MenuBook`.
12. **Rediseño Completo UI/UX 2.0 de la Pantalla de Perfil (`ProfileScreen`, `ProfileHeroHeader`, `ProfileOptionItem`):**
    - **Hero Header 2.0:** Integración de un botón flotante de lápiz/editar (`IconButton Icons.Default.Edit`) en la esquina superior derecha del encabezado, eliminando la necesidad de un botón largo en la lista.
    - **Tarjetas de Acceso Rápido (Quick Action Cards):** Fila de 3 tarjetas compactas en grid para prestadores/administradores (*Mis Servicios*, *Solicitudes* con badge flotante en rojo de pendientes, y *Reputación* con calificación ⭐ 4.9).
    - **Navegación por Pestañas Segmentadas (`TabRow`):** Organización modular de las 14 opciones en 2 pestañas limpias:
      - *Pestaña 1 ("💼 Mi Operación"):* Operatividad diaria (Horario de trabajo, servicios publicados, solicitudes, mis pedidos, mensajes y panel admin).
      - *Pestaña 2 ("⚙️ Ajustes y Ayuda"):* Seguridad y soporte (Cambio de clave, Manual de uso, Términos y Condiciones, Política de Privacidad, Borrado de cuenta y Cerrar sesión).
13. **Optimización de Contraste de Color y Legibilidad en Tema Oscuro (`LoginScreen` & `RegisterUserScreen`):**
    - **Enlace de Recuperación de Clave (`LoginScreen`):** Sustitución del color estático `secondary` (`NavyBlue` `#1E2A38`, inaccesible sobre fondos oscuros) por `MaterialTheme.colorScheme.primary` (`RedPrimary` con `FontWeight.Bold`), logrando legibilidad 100% accesible para *"¿Olvidaste tu contraseña?"* tanto en Tema Claro como en Tema Oscuro (Deep Midnight).
    - **Selector de Rol y Textos Legales (`RegisterUserScreen`):** Reemplazo de los RadioButton aislados por Tarjetas Atómicas de Selección (`Surface` / `OutlinedCard`) con borde primario al estar seleccionadas. Especificación explícita de `color = MaterialTheme.colorScheme.onBackground` en las etiquetas de rol (*"Quiero contratar un servicio"*, *"Quiero ofrecer un talento"*) y en la fila de consentimiento legal (`LegalConsentRow`), garantizando texto blanco/slate azulado en Tema Oscuro y texto oscuro en Tema Claro.
14. **Auditoría y Sincronización General y Exhaustiva de Documentación (@docs):**
    - Sincronización integral de la enciclopedia de documentación de YÁYA a la versión v1.1.0 (`versionCode 5`).
    - Actualización de `README.md` con las nuevas características v1.1.0 (Motor de Validaciones, Filtrado Geográfico por Municipio, Reputación ⭐, Rediseño Profile 2.0, Visor de Manuales por rol, Onboarding de Prestadores y Contraste Adaptativo en Tema Oscuro).
    - Alineación completa de `TECHNICAL_MANUAL.md`, `USER_MANUAL.md`, `ADMIN_MANUAL.md`, `CHANGELOG.md`, `TECHNICAL_SHEET.md`, `ROADMAP.md` y `PLAY_STORE_CHECKLIST.md` al estado `🟢 Producción / Play Store Ready (v1.1.0 - versionCode 5)`.
    - Registro oficial del Release Tag `v1.1.0` en Git para GitHub con el mensaje `"Lanzamiento Oficial YÁYA v1.1.0 (versionCode 5)"` en la bitácora del Orquestador y en el `CHANGELOG.md`.
15. **Confirmación Atómica de Cierre de Sesión en Barra Inferior (`HomeScreen` & `YayaConfirmationDialog`):**
    - **Protección Antiacidentes y Mejora UX:** Integración de la molécula atómica `YayaConfirmationDialog` en el botón de cerrar sesión ubicado en la barra inferior de navegación de `HomeScreen`.
    - **Flujo de Desautenticación Segura:** Se previno el cierre de sesión accidental solicitando la confirmación activa e intencional del usuario antes de desautenticar la sesión y destruir el estado activo, garantizando coherencia con los estándares de seguridad y experiencia de usuario de YÁYA.
16. **Modernización y Reestructuración Interactiva del Portal Web de Manuales (`portal_web/manuales.html`):**
    - **Controlador Interactivo de Pestañas (`switchRoleTab`):** Implementación de pestañas dinámicas en JavaScript (*Rol Cliente*, *Rol Prestador*, *Rol Administrador*) para consultar de forma segregada y fluida el manual correspondiente a cada perfil.
    - **Matriz Atómica de Permisos:** Diseño e integración de bloques destacados con Tailwind CSS que desglosan de forma explícita *"LO QUE PUEDE HACER EL USUARIO"* (verde `emerald`) y *"LO QUE NO PUEDE HACER EL USUARIO"* (rojo `rose`) para los tres roles.
    - **Estandarización Formal Sin Emojis:** Redacción técnica formal ajustada al estándar legal Markdown sin emojis, con soporte completo para Tema Dual (Modo Claro / Modo Oscuro) e Interfaz Elástica Responsive.
    - **Sincronización con Manuales v1.1.0 de la App:** Alineación total con los manuales de la aplicación (`ManualConstants.kt`), cubriendo la cobertura por municipios del Huila, la jornada maestra, la detección inteligente de traslapes horarios, la reputación de talentos y el semáforo de moderación disciplinario.
17. **Optimización de Configuración Firebase Hosting (`firebase.json`) y Despliegue en Producción del Portal Web v1.1.0:**
    - **Configuración de URLs Limpias sin SPA Rewrites:** Parametrización de `firebase.json` con `cleanUrls: true`, `public: "portal_web"` y `trailingSlash: false`, eliminando reescrituras SPA para soportar la arquitectura multi-página estática de forma nativa. Esto habilita el acceso directo y profesional a la ruta `/manuales` mapeada desde `portal_web/manuales.html`.
    - **Despliegue Exitoso en Producción:** Publicación de 13 archivos estáticos en la infraestructura de Firebase Hosting (Google Cloud Infrastructure).
    - **Confirmación de URLs en Producción:**
      - Dominio Principal: [https://y-ya-d5929.web.app](https://y-ya-d5929.web.app)
      - Portal Web de Manuales: [https://y-ya-d5929.web.app/manuales](https://y-ya-d5929.web.app/manuales)
18. **Armonización y Unificación del Tema Oscuro Deep Midnight en el Portal Web (`darkBg: '#0F172A'` / `darkSurface: '#1E293B'`):**
    - **Estandarización de Paleta Nocturna Slate 900 / Slate 800:** Implementación unificada de `darkBg: '#0F172A'` (Slate 900) y `darkSurface: '#1E293B'` (Slate 800) en la totalidad de las páginas del portal web (`index.html`, `tecnica.html`, `manuales.html`, `terminos.html`, `privacidad.html`, `eliminar-cuenta.html` y `js/components.js`), reemplazando el fondo negro genérico (`#121212` / `#1E1E1E`).
    - **Coherencia e Identidad de Marca 100%:** Paridad visual absoluta entre la App móvil Android v1.1.0 (Deep Midnight) y el Portal Web en producción de YÁYA.
    - **Insignia de Versión en Footer Global:** Actualización de la insignia de versión en el pie de página de `components.js` a `v1.1.0 Stable (versionCode 5)`.
19. **Unificación y Fusión Oficial a la Rama de Producción (`desarrollo` ➔ `produccion` / `origin/produccion`):**
    - **Fusión e Integración Continua Exitosas:** Fusión y unificación oficial de la rama `desarrollo` a la rama de producción `produccion` (`origin/produccion`) en Git/GitHub.
    - **Métricas e Impacto Consolidado del Release:** Unificación consolidada de 63 archivos con un total de +3101 inserciones y -770 eliminaciones.
    - **Hito de Despliegue v1.1.0 (`versionCode = 5`):** Consolidación oficial de todo el trabajo de la versión 1.1.0 en la rama principal de producción, lista para distribución en Google Play Store y sincronización del entorno operativo.
20. **Resiliencia de Perfil y Solución a Violación de Clave Foránea `requests_client_id_fkey` (`ContratacionViewModel`):**
    - **Diagnóstico:** Identificación y corrección de la excepción PostgreSQL `Code: 23503 Details: Key is not present in table "profiles"` (`requests_client_id_fkey`) reportada al intentar crear una solicitud de servicio (`insert` en tabla `requests`) cuando un usuario autenticado en Auth (`auth.users`) no poseía aún un registro en la tabla `public.profiles`.
    - **Solución Defensiva `ensureProfileExists(user)`:** Incorporación del mecanismo atómico `ensureProfileExists` en `ContratacionViewModel.contratar()`. Previo a la inserción de la solicitud en `requests`, la función verifica si el perfil del cliente existe en `public.profiles`; si no se encuentra, lo crea automáticamente recuperando la metadata de Auth (`full_name`, `role`, `phone`, `address`, `municipality`).
    - **Garantía de Estabilidad:** Eliminación 100% garantizada del fallo de clave foránea al agendar citas y contrataciones.
21. **Sincronización Defensiva Automática entre Auth y `public.profiles` (`RegisterUserViewModel` & `LoginViewModel`):**
    - **Registro (`RegisterUserViewModel`):** Tras ejecutar `signUpWith(Email)` en `supabase-kt`, se extrae directamente `userResponse?.id` del objeto `UserInfo?` retornado (UUID en `auth.users`). Al obtener el UUID directamente de la respuesta de `signUpWith`, el sistema ya no depende de que el usuario haya iniciado sesión o verificado su correo electrónico para obtener su ID, permitiendo realizar la inserción inmediata del perfil en `public.profiles` (`postgrest["profiles"].upsert(newProfile)`) con su metadata inicial (`full_name`, `role`, `phone`, `address`, `municipality`) sin importar si el rol es `client`, `provider` o `admin`.
    - **Login (`LoginViewModel`):** Al iniciar sesión, la rutina defensiva `ensureProfileExists(user)` verifica si el usuario existe en `public.profiles`. Si la fila es inexistente, extrae la metadata de Auth (`full_name`, `role`, `phone`, `address`, `municipality`) e inserta automáticamente el registro faltante en `public.profiles`.
    - **Eliminación Total de Errores de Clave Foránea (`23503`):** Se garantiza que cualquier usuario autenticado en Supabase Auth posea de forma garantizada su fila en `public.profiles`, satisfaciendo todas las restricciones de clave foránea en las tablas `requests`, `services`, `ratings`, `messages` y `reports`.
22. **Extracción Directa de `userResponse?.id` en `RegisterUserViewModel`:**
    - **Respuesta Directa de `signUpWith`:** Aprovechamiento de la respuesta de `signUpWith(Email)` en `supabase-kt` que suministra directamente la instancia de `UserInfo?` conteniendo la propiedad `userResponse?.id` (el UUID generado en `auth.users`).
    - **Independencia de Confirmación/Login e Inserción Inmediata:** Se elimina la necesidad de haber iniciado sesión previa o confirmado el correo electrónico para obtener el UUID, ejecutando de forma inmediata `postgrest["profiles"].upsert(newProfile)` en `public.profiles` para todos los roles (`client`, `provider`, `admin`).
23. **Indicador Visual de Rol Seleccionado y Validación Estricta de Dominios de Rol (`RegisterUserScreen` & `RegisterUserViewModel`):**
    - **Feedback Visual de Rol Seleccionado (`RegisterUserScreen`):** Incorporación de un indicador contextual permanente en la cabecera del selector de roles (*"Rol: Cliente"* / *"Rol: Prestador"*), otorgando retroalimentación visual inmediata al usuario sobre la opción activa.
    - **Validación Estricta de Rol en ViewModel (`RegisterUserViewModel`):** Restricción y validación estricta del parámetro `role` limitado exclusivamente a dominios válidos (`client`, `provider`, `admin`), evitando el procesamiento de solicitudes de registro con roles vacíos o malformados y garantizando la integridad del dominio de datos.
24. **Solución a Error de Serialización PostgreSQL 23502 en Registro (`UserProfile.kt`):**
    - **Diagnóstico del Error PostgreSQL 23502:** Durante el registro con rol `client`, la propiedad `role: String = "client"` en `UserProfile.kt` coincidía con su valor por defecto en Kotlin. `kotlinx.serialization` omitía dicha propiedad del cuerpo JSON codificado en la petición POST de Postgrest (`columns=id,full_name,phone,document_id,birth_date,address`). Al llegar a PostgreSQL sin la columna `role`, se violaba la restricción `NOT NULL` (`Code: 23502`).
    - **Solución Implementada:** Eliminación de los valores por defecto de las propiedades no nulas `id`, `full_name` y `role` en `UserProfile.kt`. Esto obliga a `kotlinx.serialization` a incluir siempre la propiedad `"role"` en las peticiones HTTP POST/UPSERT hacia Postgrest, garantizando que PostgreSQL reciba explícitamente `"role": "client"` o `"role": "provider"` y eliminando la excepción 23502.
25. **Compilación Exitosa de Binario de Producción `.aab` (`versionCode = 6`):**
    - **Generación de Android App Bundle:** Ejecución exitosa de la tarea Gradle `app:bundleRelease` generando el binario de producción `.aab` firmado y optimizado con `versionName = "1.1.0"` y `versionCode = 6` (`app/build.gradle.kts`).
    - **Sincronización de Documentación:** Actualización integral de la enciclopedia de documentación del proyecto (`MASTER_ORCHESTRATOR.md`, `CHANGELOG.md`, `TECHNICAL_MANUAL.md`, `TECHNICAL_SHEET.md` y `PLAY_STORE_CHECKLIST.md`) reflejando la versión de producción `versionCode = 6` para su despliegue en Google Play Store.
26. **Guía Oficial del Stand de Exposición para la Muestra SENA (`PITCH_DECK_FERIA.md`):**
    - **Guía de Presentación para el Stand - Muestra SENA 2026:** Adaptación de la guía oficial de presentación y exposición del proyecto YÁYA para el stand en la Muestra de Tecnólogo SENA (ADSO) en `docs/01-business/PITCH_DECK_FERIA.md`.
    - **Guión Explicativo para el Stand:** Definición del guión narrativo amigable de 1 a 2 minutos para la atención de visitantes en el stand, enfocado en la bienvenida, la problemática de servicios en municipios del Huila (La Plata, Nátaga, Paicol, Neiva) y la solución digital de intermediación directa.
    - **Secuencia de Demostración en Vivo y Puntos Destacados ADSO:** Diseño del flujo de demostración práctica en 5 pasos (Filtrado Geográfico por Municipio, Agendamiento Inteligente con Validaciones de Horarios y Traslapes, Negociación Handshake en Chat Realtime, Perfil 2.0 con Manual de Uso Integrado y Portal Web Oficial) junto con la síntesis de arquitectura técnica para instructores y compañeros (Kotlin, Jetpack Compose, Atomic Design, Clean MVVM, Supabase PostgreSQL Realtime, Firebase Hosting y binario `.aab` versionCode 6 Play Store Ready).

### 🔹 Sesión Septiembre 2026 (Estabilidad, Cumplimiento Play Store y CI/CD)
En esta intervención, el Orquestador Maestro dirigió un plan de optimización enfocado en la estabilidad de la interfaz, el cumplimiento regulatorio de Google Play y el fortalecimiento de la infraestructura de calidad:

1. **Estabilidad de UI/Layout (Jetpack Compose):** Corrección quirúrgica del error `IllegalStateException` provocado por componentes scrolleables medidos con restricciones de altura infinita (conflictos entre `LazyColumn` y `Column` con `verticalScroll`). Se refactorizaron los componentes `NegotiationHistoryBox.kt`, `ConfirmacionScreen.kt` y `MyServicesScreen.kt`.
2. **Cumplimiento y Publicación en Google Play:** 
   - Declaración del permiso obligatorio `com.google.android.gms.permission.AD_ID` en `AndroidManifest.xml` para alineación con las políticas de identificador de publicidad y analítica de Play Store.
   - Configuración de depuración nativa con nivel `FULL` (`ndk { debugSymbolLevel = 'FULL' }`) en `app/build.gradle.kts` para garantizar la recepción y desofuscación completa de trazas de fallos nativos en Play Console.
   - Preparación de versión `versionName = "1.1.0"` (`versionCode = 5`) en `app/build.gradle.kts` para despliegue de release oficial.
3. **Infraestructura de CI/CD, Resiliencia y Calidad (`.github/workflows/codeql.yml`):**
   - Despliegue de **Advanced CodeQL Analysis** optimizado con soporte para Java 17 y Kotlin en GitHub Actions (`.github/workflows/codeql.yml`).
   - **Resolución del Error "Malformed root json" en CI/CD:** Optimización del flujo de inyección del secreto `secrets.GOOGLE_SERVICES_JSON` en GitHub Actions mediante un mecanismo de autodetección y decodificación para formatos Base64 o JSON plano, inyección automatizada de plantilla de respaldo (*fallback*) con paquete `com.bhplusplus.yaya` ante secretos vacíos o malformados, y verificación estricta de sintaxis con `jq empty` previo al paso de compilación `./gradlew assembleDebug`, garantizando 100% de estabilidad en los builds de análisis de seguridad.
4. **Estabilidad de Canales Realtime:**
   - Validación defensiva del estado `RealtimeChannel.Status.UNSUBSCRIBED` en `ChatViewModel`, `ChatListViewModel`, `HomeViewModel`, `IncomingRequestsViewModel`, `MyOrdersViewModel`, `MyServicesViewModel` y `ProfileViewModel`, evitando crashes por suscripciones duplicadas (`IllegalStateException`).
5. **Resiliencia en Serialización de Datos (KotlinX Serialization):**
   - Incorporación de valores por defecto defensivos en los modelos de dominio (`ServiceRequest`, `UserProfile`, `Message`, `Rating`, `Report`, `ServiceImage`, `Availability`, `Category`) asegurando tolerancia total ante consultas Postgrest con proyecciones relacionales parciales (`Columns.raw`).
6. **Motor de Validaciones de Entrada de Datos (DRY & MVVM):**
   - Creación de `ValidationUtils.kt` e integración de soporte para `errorMessage` en el átomo `YayaTextField` para retroalimentación visual en tiempo real (nombres alfabéticos sin números, documento DNI/CC de 6 a 12 números, teléfono exacto de 10 dígitos, correo electrónico RFC/Patterns, contraseña segura, fechas de nacimiento no futuras y agendamiento de citas con validación de dirección, fecha no pasada y hora no transcurrida).

### 🔹 Sesión Agosto 2026 (Consolidación MVP+ SENA Gold Edition)
1. **Infraestructura de Notificaciones:** Cierre del ciclo de comunicación con Small Icons oficiales y despliegue de lógica Server-Side (Edge Functions) para una negociación en tiempo real totalmente automatizada.
2. **Evolución del Modelo de Negocio:** Blindaje del valor de los servicios mediante la implementación de la regla de "Precio Mínimo" en el flujo de subasta.
3. **Excelencia en UX/UI:** Rediseño total de los puntos de contacto más críticos (Contratación, Mis Pedidos, Confirmación), priorizando la iconografía vectorial, la jerarquía de información y controles interactivos dinámicos.
4. **Estandarización Atómica:** Refactorización integral de la interfaz de usuario bajo la metodología **Atomic Design**, centralizando componentes reutilizables en librerías de Átomos, Moléculas y Organismos para garantizar consistencia DRY absoluta.
5. **Inteligencia de Conectividad:** Implementación de monitoreo global de red con feedback visual automático (`YayaOfflineBanner`), blindando la App ante fallos de internet.
6. **Automatización de Auditoría:** Cierre del ciclo de vida admin con notificaciones masivas para el equipo de moderación y feedback instantáneo a los prestadores sobre sus aprobaciones.
7. **Omnicanalidad y Marketing:** Despliegue del Portal Web profesional en Firebase Hosting, optimizado para móviles y conectado a la Play Store, cerrando el ecosistema digital de la marca.

*BH++ Team - Gestión de Inteligencia Colectiva*
