# 📖 Biblia Técnica de Ingeniería y Despliegue - YÁYA

Este documento constituye la fuente de verdad técnica para la plataforma **YÁYA**. Detalla la arquitectura, el modelado de datos, los estándares de codificación y los procedimientos de despliegue para garantizar la escalabilidad y mantenibilidad del sistema por parte de **BH++ Team**.

---

## 1. Arquitectura de Software: Clean MVVM + Atomic Design

YÁYA implementa una arquitectura robusta basada en la separación estricta de responsabilidades, permitiendo que la interfaz sea elástica y la lógica de negocio totalmente independiente.

### 1.1. Capas del Sistema (Package Structure)
```mermaid
graph TD
    UI[ui.screens] --> VM[ui.viewmodels]
    VM --> Domain[data.models]
    VM --> Supabase[data.SupabaseManager]
    UI --> Components[ui.components.atoms/molecules/organisms]
```

*   **UI Layer (Stateless):** Funciones Composable puras que renderizan un `UiState`.
*   **ViewModel Layer:** Controladores de estado que gestionan corrutinas y transforman datos crudos en información lista para la vista (Principio DRY).
*   **Data Layer:** Integración directa con Supabase mediante el cliente global, gestionando Auth, Postgrest y Realtime.

### 1.3. Arquitectura de Clases (MVVM Pattern)
Para cada pantalla, se implementa el siguiente flujo de componentes:

*   **UiState (Data Class):** Inmutable, representa el estado total de la pantalla.
*   **ViewModel (ViewModel):** Única fuente de verdad. Se comunica con el `SupabaseManager` y actualiza el `UiState`.
*   **Screen (Composable):** Recibe el `UiState` y emite eventos de usuario al ViewModel.
*   **Components (Composables):** Átomos y moléculas desacoplados que renderizan partes específicas del `UiState`.

### 1.4. Engine de Validaciones de Entrada (ValidationUtils - DRY)
El componente `ValidationUtils` centraliza las reglas de negocio para la captura y edición de datos de usuario en toda la aplicación:
*   **Nombres:** Letras latinas (incluyendo tildes y ñ), espacios y guiones. Sin dígitos numéricos.
*   **Documento DNI/CC:** Formato exclusivamente numérico entre 6 y 12 dígitos.
*   **Teléfono móvil:** Cadena de exactamente 10 dígitos numéricos.
*   **Correo Electrónico:** Formato estándar RFC/Patterns (`Patterns.EMAIL_ADDRESS`).
*   **Contraseña Segura:** Mínimo 8 caracteres combinando mayúscula, minúscula y número o carácter especial.
*   **Fecha de Nacimiento:** Bloqueo en la UI de días futuros en el calendario (`SelectableDates`) y validación de fecha cronológica no futura (`isValidBirthDate`).
*   **Agendamiento de Citas:** Dirección de atención válida (mín. 5 caracteres), restricción UI de calendario a días presentes o futuros (`SelectableDates`), verificación de fecha no pasada (`isValidFutureDate`) y validación de hora no transcurrida para el día actual (`isValidScheduleTime`).
*   **Municipios y Cobertura:** Lista inmutable centralizada en `ValidationUtils.HUILA_MUNICIPALITIES` que suministra las opciones de municipios de cobertura en el departamento del Huila (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante) a componentes desplegables `ExposedDropdownMenuBox` en `RegisterUserScreen`, `EditProfileScreen`, `CreateServiceScreen` y al filtro geográfico de `HomeScreen`, eliminando errores de tipeo y campos de texto libre.

### 1.5. Arquitectura de Filtrado Geográfico por Municipio/Zona y Flujo de Horarios por Servicio
YÁYA implementa una estrategia de filtrado multizona y gestión elástica de tiempos de atención para segmentar la oferta de servicios según la ubicación y disponibilidad del prestador:
*   **Estandarización de Ubicación con `ExposedDropdownMenuBox`:** Los formularios de captura e interacción (`RegisterUserScreen`, `EditProfileScreen`, `CreateServiceScreen`) y el diálogo modal de filtro geográfico en `HomeScreen` consumen la lista inmutable `ValidationUtils.HUILA_MUNICIPALITIES`. Los campos de texto libre fueron reemplazados por selecciones desplegables inmutables con `ExposedDropdownMenuBox`, garantizando consistencia total en la base de datos y eliminando errores tipográficos.
*   **Modelos de Datos de Ubicación:** La propiedad opcional `municipality: String?` ("La Plata" por defecto) se integra en los modelos de dominio `UserProfile` y `Service`.
*   **Controles UI y Lógica de Filtrado:** El componente `HomeTopBar` expone un chip interactivo de selección de municipio y `HomeViewModel.applyFilters()` filtra dinámicamente el catálogo reactivo permitiendo seleccionar municipios específicos o la opción global "Todos".
*   **Flujo de Onboarding del Prestador (Post-Registro):**
    *   **Navegación Guiada de Registro:** Al completar el registro con rol `provider`, el sistema ejecuta la redirección automática hacia la pantalla de configuración de Jornada Maestra (`AvailabilityScreen`).
    *   **Establecimiento de Base Temporal:** Garantiza que todo nuevo prestador configure su rango de disponibilidad laboral general y días activos en la tabla `public.availability` antes de publicar servicios o recibir solicitudes de agendamiento.
*   **Carga Inteligente de Disponibilidad y Prevención de Traslapes de Días:**
    *   **Consulta de Disponibilidad y Servicios Existentes:** `CreateServiceViewModel` ejecuta `loadProviderAvailabilityAndServices(currentEditingServiceId)` para recuperar de manera concurrente la jornada maestra del prestador (`masterWorkingDays`) y los servicios activos creados previamente (`public.services`), construyendo un mapa de ocupación `occupiedDaysByOtherServices: Map<Int, String>` que concatena el título del servicio con su rango horario `(start_time - end_time)` activo (ej. `Desarrollo de aplicaciones móviles (08:00 - 18:00)`).
    *   **Acción Rápida *"Cargar mi jornada maestra"*:** Permite precargar instantáneamente en el estado UI `selectedDays` los días configurados en la disponibilidad general del prestador.
    *   **Prevención de Cruces y Alerta Contextual con Rangos Horarios:** En `CreateServiceScreen`, los días identificados en `occupiedDaysByOtherServices` se visualizan con un indicador cromáticamente diferenciado (`errorContainer` y texto en color `error`). Además, un componente de texto informativo contextual detalla explícitamente qué días y rangos horarios ya están asignados a otros servicios del prestador (ej. *"ℹ️ Días asignados a otros de tus servicios: Lunes: Limpieza de muebles (08:00 - 12:00), Miércoles: Plomería (14:00 - 18:00)"*), brindando visibilidad de horas libres y evitando la duplicidad o sobrecarga no intencionada de horarios.
*   **Separación entre Disponibilidad Maestra y Horarios por Servicio:**
    *   **Jornada Maestra del Prestador (`public.availability`):** Define el rango horario global (hora de inicio y hora de fin) y días laborables generales configurados por el usuario prestador en su perfil general.
    *   **Horarios por Servicio (`public.services`):** Especifica los días de atención (`working_days`) y particularidades aplicables de manera independiente a cada servicio o talento publicado.
    *   **Validación Sincronizada en Agendamiento (`ContratacionViewModel`):** Al iniciar un proceso de contratación, `ContratacionViewModel` intercepta el calendario y horario de la cita: restringe los días seleccionables a aquellos habilitados para el servicio específico (`currentService.working_days`) y valida que la hora propuesta coincida con el rango activo en la tabla `availability`, bloqueando selecciones fuera de rango o en horas pasadas.
    *   **Resolución de Persistencia, Serialización y Formateo SQL (`AvailabilityViewModel` & `Availability.kt`):** Corrección del fallo de guardado en `public.availability` mediante la generación de UUIDs de cliente (`java.util.UUID.randomUUID()`) previa a la operación de `upsert`, resolviendo la restricción not-null en la columna `id` de Supabase PostgreSQL (evitando el envío de `"id": null`). Se removieron además los valores por defecto en las propiedades de `Availability.kt` (`day_of_week`, `provider_id`, `start_time`, `end_time`) para impedir que `kotlinx.serialization` omita campos en el payload JSON cuando coinciden con sus valores por defecto, solucionando la excepción `null value in column "day_of_week" violates not-null constraint`. Asimismo, se normalizó el formato de las horas a 8 caracteres (`"HH:mm:ss"`, ej. `"08:00:00"`), asegurando alineación estricta con el tipo SQL `time without time zone` y desplegando errores contextuales en `AvailabilityScreen` mediante un contenedor visual dedicado.

---

## 2. Modelado de Datos y Seguridad (Supabase PostgreSQL)

### 2.1. Diagrama Entidad-Relación (ERD)
YÁYA utiliza un esquema relacional optimizado para la intermediación de servicios:

```mermaid
erDiagram
    PROFILES ||--o{ SERVICES : "publica"
    PROFILES ||--o{ REQUESTS : "contrata"
    SERVICES ||--o{ REQUESTS : "es solicitado"
    REQUESTS ||--o| RATINGS : "genera"
    PROFILES ||--o{ MESSAGES : "envía/recibe"
```

### 2.2. Seguridad a Nivel de Fila (RLS)
Todas las tablas en Supabase tienen políticas **RLS** activas:
*   **Lectura:** Pública para perfiles y servicios activos.
*   **Escritura:** Restringida al `auth.uid()` del propietario (proteger identidad y finanzas).
*   **Negociación:** Solo el cliente y el prestador vinculados a una `request_id` pueden actualizar su estado.

### 2.3. Resiliencia en Serialización (KotlinX Serialization)
La estrategia de serialización y deserialización de modelos en YÁYA responde a dos reglas críticas de arquitectura según el flujo de datos:

1. **Deserialización Tolerante (Lecturas y Proyecciones Parciales):**
   Los modelos de consulta e integración (`ServiceRequest`, `UserProfile`, `Message`, `Rating`, `Report`, `Category`) asignan valores por defecto defensivos a sus atributos. Esto garantiza que las respuestas JSON provenientes de consultas con proyecciones relacionales parciales (ej: `Columns.raw("id, services!inner(provider_id)")`) se deserialicen sin arrojar excepciones `MissingFieldException`.

2. **Regla de No Omisión en Inserción/Upsert (Columnas PostgreSQL `NOT NULL` sin `DEFAULT`):**
   * **Regla Técnica:** No se deben asignar valores por defecto en las data classes de Kotlin a propiedades mapeadas a columnas de tablas PostgreSQL que tengan restricción `NOT NULL` y carezcan de cláusula SQL `DEFAULT` (ej. `day_of_week`, `provider_id`, `start_time`, `end_time` en `Availability.kt`).
   * **Causa de Falla:** Por comportamiento predeterminado, `kotlinx.serialization` omite del objeto JSON saliente cualquier propiedad cuyo valor coincida exactamente con su valor por defecto en Kotlin (ej. `day_of_week = 1`).
   * **Efecto en Postgrest:** Al recibir la petición `UPSERT` o `INSERT` sin la clave en la carga JSON, Postgrest no envía dicho campo a PostgreSQL. Al no tener la columna una instrucción `DEFAULT` en la base de datos, PostgreSQL aborta la transacción con la excepción:
     `null value in column "day_of_week" violates not-null constraint`.
   * **Solución Aplicada:** Remover los valores por defecto en la definición de la data class de Kotlin. Al carecer de valor por defecto, `kotlinx.serialization` se ve forzado a codificar explícitamente la propiedad en el payload JSON hacia Supabase.

### 2.4. Migración de Esquema (DDL Supabase PostgreSQL)
Para soportar el filtrado geográfico por municipio, la base de datos de Supabase integra la adición de la columna `municipality` en las tablas `public.profiles` y `public.services`:

```sql
-- Migración para agregar la columna municipality a las tablas public.profiles y public.services
ALTER TABLE public.profiles 
ADD COLUMN IF NOT EXISTS municipality text DEFAULT 'La Plata';

ALTER TABLE public.services 
ADD COLUMN IF NOT EXISTS municipality text DEFAULT 'La Plata';
```

---

## 3. Stack Tecnológico y Dependencias Clave

*   **Lenguaje:** Kotlin 2.4.10 (K2 Compiler, JVM Target 17).
*   **Target SDK:** Android API 37 (minSdk 26, versionCode 5, versionName "1.0.1").
*   **UI:** Jetpack Compose (Material 3) con soporte para arquitectura atómica y componentes de desplazamiento optimizados.
*   **Backend:** Supabase (PostgreSQL + Realtime + Storage).
*   **Permisos y Cumplimiento Google Play:** Declaración de `com.google.android.gms.permission.AD_ID` para servicios de analítica e identificador de anuncios en Google Play.
*   **Security Hardening:** Mitigación proactiva de vulnerabilidades (CVEs) mediante restricciones de dependencia (Netty, Bouncy Castle, HttpClient, Guava, jose4j).
*   **Observabilidad:** Firebase Crashlytics & Analytics (Telemetría de errores y métricas). Se utiliza el wrapper `CrashReporter.kt` para centralizar los reportes.
*   **Notificaciones:** Firebase Cloud Messaging (FCM V1) via Edge Functions.
*   **Multimedia:** Coil 3.1.0 (Async Image Loading).
*   **Reactividad:** Kotlin Flows & Coroutines para flujos asíncronos no bloqueantes.

---

## 4. Guía de Entorno de Desarrollo Local

### 4.1. Configuración del Repositorio
1.  **Clonar:** `git clone https://github.com/Brandon094/Y-YA_Mobile.git`
2.  **Android Studio:** Abrir proyecto con versión Ladybug o superior.
3.  **Variables de Entorno:** Configurar `secrets.properties` en la raíz:
    ```properties
    SUPABASE_URL="https://tu-proyecto.supabase.co"
    SUPABASE_ANON_KEY="tu-anon-key"
    ```

### 4.2. Compilación
*   **Debug:** Ejecutar tarea `app:assembleDebug`.
*   **Release:** Configurar el archivo `.jks` y ejecutar `app:bundleRelease` (genera el archivo `.aab` para Google Play con `versionCode = 5` y símbolos de depuración nativos NDK en nivel `FULL`).

---

## 5. Motor de Notificaciones, Tiempo Real y Estabilidad Visual

### 5.1. Observabilidad y Telemetría (Firebase)
YÁYA utiliza un motor dual de captura de errores para garantizar un 99.9% de estabilidad:
1.  **Crashes Fatales:** Capturados automáticamente por la SDK de Firebase Crashlytics.
2.  **Excepciones No Fatales:** Gestionadas por `CrashReporter.kt`. Este componente permite enviar logs personalizados y trazas de error desde bloques `try-catch`, permitiendo depurar fallos en la lógica de Supabase o red sin que la App se cierre para el usuario.

### 5.2. Flujo de Notificación Push (Edge Functions)
YÁYA utiliza lógica Server-Side para automatizar alertas sin saturar el cliente:
1.  Un evento (INSERT/UPDATE) en la DB dispara un **Webhook**.
2.  La Edge Function `notify-yaya-updates` (TypeScript) procesa el evento.
3.  Se identifica al destinatario y se envía la alerta via **FCM API V1**.

### 5.3. Conectividad Resiliente
Implementación de `ConnectivityObserver` basado en Flows que monitorea el hardware de red y notifica mediante el `YayaOfflineBanner` atómico.

### 5.4. Estabilidad de Layouts en Jetpack Compose
Para evitar excepciones en runtime como `IllegalStateException` durante la fase de medición, los layouts scrolleables evitan anidar componentes de scroll ilimitado (`LazyColumn` dentro de `Column` con `verticalScroll` sin restricciones fijas). Los contenedores en `NegotiationHistoryBox.kt`, `ConfirmacionScreen.kt` y `MyServicesScreen.kt` están optimizados con restricciones explícitas de altura (`heightIn`/`fillMaxHeight`).

---

### 6. Pipeline de Despliegue y Calidad (CI/CD)

1.  **Advanced CodeQL Analysis:** GitHub Actions (`.github/workflows/codeql.yml`) ejecuta análisis estático de código enfocado en Java 17 y Kotlin (`java-kotlin`), evaluando reglas de seguridad extendida (`security-extended,security-and-quality`).
2.  **Secret Management:** Inyección automatizada de `google-services.json` durante el pipeline de integración continua utilizando secretos de GitHub y sintaxis heredoc.
3.  **App Bundle & Depuración Nativa:** Generación del binario `.aab` con firma de producción SHA-256, `versionCode = 5` y la directiva `ndk { debugSymbolLevel = 'FULL' }` en `app/build.gradle.kts` para análisis nativo completo en Play Console.

---
*Documento certificado por la Dirección Técnica de BH++ Team - 2026*
