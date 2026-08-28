# Arquitectura de Software - YÁYA (Master Document)

Este documento define la columna vertebral técnica de YÁYA, detallando los patrones de diseño, la jerarquía de componentes y las decisiones de ingeniería que garantizan un software de clase empresarial.

## 1. Patrón Arquitectónico: Clean MVVM
YÁYA implementa una versión purista del patrón **Model-View-ViewModel**, desacoplando totalmente la lógica de negocio de la representación visual.

### 1.1. Capa de Vista (Passive View) - Atomic Design
La interfaz de usuario se construye siguiendo la metodología **Atomic Design**, lo que permite una reutilización extrema y una consistencia visual inquebrantable.

- **Átomos:** Componentes atómicos e indivisibles que no poseen lógica de negocio. (Ej: `YayaButton`, `YayaTextField`, `YayaAvatar`, `YayaLogo`).
- **Moléculas:** Grupos de átomos que funcionan como una unidad funcional simple. (Ej: `RatingIndicator`, `DayIndicator`, `DetailRow`, `ChatBubble`, `YayaNegotiationDialog`).
- **Organismos:** Secciones complejas orquestadas que forman bloques lógicos de una pantalla. (Ej: `ServiceCard`, `HomeTopBar`, `ProviderCard`, `IncomingRequestCard`, `MyOrderCard`).
- **Páginas (Screens):** Composables de alto nivel que orquestan los organismos e inyectan el estado desde el ViewModel.

**Reglas de Oro de la UI:**
- **Stateless:** Las pantallas no mantienen estado interno de negocio.
- **Passive:** La vista no decide qué mostrar; solo renderiza el `UiState` procesado por el ViewModel.
- **Universal Accessibility:** Soporte obligatorio para fuentes al **200%** mediante `FlowRow`, `sizeIn` y pesos dinámicos.

### 1.2. Capa de ViewModel (The Orchestrator)
El ViewModel es el único responsable de la toma de decisiones y la transformación de datos.
- **UiState Driven:** Expone un modelo de datos único y procesado a la vista.
- **DRY Transformation:** Utiliza `FormatterUtils.kt` para centralizar el formateo de moneda colombiana compacta ($ 50k), normalización de fechas y tiempos.
- **Business Logic:** Valida estados de disponibilidad, gestiona el flujo de negociación y controla las suscripciones en tiempo real.

### 1.3. Capa de Datos (Supabase Core)
- **SupabaseManager:** Actúa como un Singleton que gestiona la comunicación con el Backend-as-a-Service.
- **Realtime Reactivity:** Implementa suscripciones a canales de PostgreSQL para actualizaciones instantáneas de mensajes, pedidos y estados.

## 2. Flujo Transaccional: Handshake Digital
YÁYA implementa un protocolo de seguridad tripartito para garantizar la integridad de los acuerdos comerciales:
1.  **Negociación (`pending`):** Fase de subasta bidireccional ilimitada.
2.  **Acuerdo (`accepted`):** El precio es fijado por una de las partes y aceptado por la otra.
3.  **Confirmación de Inicio (`in_progress`):** El cliente debe realizar un "Handshake" digital pulsando "Confirmar y Empezar". Solo este estado desbloquea la capacidad del prestador para finalizar el trabajo.
4.  **Cierre (`completed`):** El trabajo termina y se habilita automáticamente el sistema de reputación (Rating).

## 3. Estrategia de Feedback: Skeleton Shimmers
Para eliminar la percepción de latencia, se implementa una infraestructura de **Skeleton Screens** que replican la estructura atómica exacta de la pantalla destino. 
- Opacidad optimizada (**alpha 0.25**) para una visibilidad Premium.
- Cobertura total en flujos críticos (Home, Detalles, Contratación, Admin).

## 4. Navegación Type-Safe y Start-up Engine
- **Direct Route:** Uso de Splash Screen API nativa sincronizada con `MainViewModel` para un arranque sin parpadeos.
- **Seguridad de Tipos:** Todas las rutas son objetos `@Serializable`, eliminando errores de tipado en strings de navegación.
- **Detección de Conectividad:** Monitoreo global mediante `ConnectivityObserver` (Flow-based). La aplicación muestra una `YayaOfflineBanner` atómica en tiempo real ante fallos de red, garantizando feedback continuo.

## 5. Roles y Acceso Universal
Unificación de perfil bajo un ID único que soporta transacciones cruzadas (un prestador puede ser cliente y viceversa sin fricción).
- **Anonimato Administrativo:** Para proteger la integridad de los administradores (Mauro, Harold, Brandon), el sistema implementa una capa de enmascaramiento en el Chat. Cuando un usuario no-admin interactúa con un administrador, el nombre se reemplaza por "Equipo de Moderación" y el avatar por el isotipo oficial de YÁYA.
- El rol `admin` posee una capa de acceso híbrido que le permite interactuar con el ecosistema y moderar desde su perfil.

## 6. Motor de Notificaciones (Push Architecture)
YÁYA utiliza una infraestructura híbrida para alertas globales impulsada por **Supabase Edge Functions**:
1.  **Registro:** El cliente Android sincroniza el token FCM con `public.profiles`.
2.  **Disparadores (Webhooks):** Eventos en las tablas `requests`, `messages` y `services` activan la Edge Function.
3.  **Lógica Multi-Destino:** La función unificada soporta el envío a múltiples administradores simultáneamente y feedback directo a usuarios sobre cambios de estado.
4.  **FCM V1:** La entrega se realiza mediante la API HTTP v1 de Firebase, garantizando alta prioridad y entrega confiable.

## 7. Moderación y Sanciones Progresivas
El sistema administrativo implementa una lógica de protección comunitaria basada en la acumulación de reportes:
- **Agrupamiento Atómico:** Los reportes se consolidan por infractor (`ReportedUserSummary`) para facilitar la toma de decisiones masivas.
- **Semáforo de Severidad:** Cálculo en tiempo real del nivel de riesgo (Llamado de atención, Suspensión, Eliminación) basado en umbrales de reincidencia (3 y 5 reportes).
- **Advertencias Automatizadas:** Los administradores pueden enviar "Llamados de Atención" pre-diseñados mediante el sistema de chat, automatizando la comunicación preventiva sin necesidad de redacción manual.

## 8. Gestión de Cumplimiento Legal
YÁYA integra un motor de visualización de documentos normativos:
- **Centralización de Contenido:** Uso de `LegalConstants.kt` como punto único de verdad para textos legales.
- **Dynamic Legal Rendering:** La `LegalViewerScreen` implementa un procesador de texto que transforma sintaxis Markdown (encabezados, listas) en componentes estilizados de Material 3 con identidad visual Premium.
- **Acceptance Flow:** El registro requiere la confirmación binaria (Checkboxes) vinculada al estado de habilitación del proceso de creación de cuenta.

---
*Documento Maestro de Arquitectura - BH++ Senior Engineering*
