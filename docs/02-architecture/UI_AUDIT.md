# 🛡️ Auditoría de Calidad y Requisitos - YÁYA (v0.1.5-alpha)

Este documento sirve como guía oficial para el proceso de verificación de requisitos y aseguramiento de la calidad basado en el estándar **ISO/IEC 25010**.

---

## 1. Solicitud de Requisitos del Sistema
*Consulte el archivo completo en: [REQUIREMENTS.md](../01-business/REQUIREMENTS.md)*

### Resumen Técnico
- **RF Destacado:** Módulo de Negociación (Subasta de precios) y Chat Realtime con sistema de visto.
- **RNF Destacado:** Seguridad a nivel de fila (RLS) y UI Reactiva con tiempos de respuesta < 2s.

---

## 2. Historias de Usuario (Sintaxis BDD)

### Escenario 1: Negociación de Tarifa (Flow Core)
- **DADO** que un Cliente ha enviado una solicitud de servicio con un precio inicial.
- **CUANDO** el Prestador recibe la solicitud y envía una contraoferta con un nuevo valor.
- **ENTONCES** el sistema debe actualizar el `final_price` en tiempo real y notificar al Cliente para su aceptación o rechazo.

### Escenario 2: Comunicación y Visto (Realtime)
- **DADO** que un Prestador tiene la pantalla de chat abierta con un Cliente.
- **CUANDO** el Cliente envía un mensaje nuevo.
- **ENTONCES** el mensaje debe aparecer instantáneamente en la pantalla del Prestador y marcarse como "leído" (`is_read: true`) en la base de datos de forma automática.

### Escenario 3: Gestión de Portafolio Multimedia
- **DADO** que un Prestador está creando un nuevo servicio.
- **CUANDO** selecciona múltiples imágenes de su galería y guarda el servicio.
- **ENTONCES** el sistema debe subir los archivos al bucket de Storage, generar URLs públicas y persistirlas en la tabla `service_images` para su visualización en el catálogo.

---

## 3. Matriz de Calidad ISO/IEC 25010 (Checklist)

| Categoría | Ítem de Verificación | Cumple | Evidencia / Comentario |
| :--- | :--- | :--- | :--- |
| **Adecuación Funcional** | ¿Funciones cubren el 100% de requisitos? | Sí | Implementados hitos 1 al 5 (Auth, Negociación, Chat, Admin). |
| | ¿Cálculos de negocio son precisos? | Sí | El flujo de `final_price` en `requests` garantiza integridad económica. |
| **Eficiencia** | ¿Responde en < 2 segundos? | Sí | Uso de Kotlin Coroutines y carga asíncrona optimizada. |
| | ¿Consumo de recursos estable? | Sí | Perfilado de memoria realizado con Android Profiler. |
| **Compatibilidad** | ¿Funciona en SO objetivo? | Sí | Soporte nativo para Android 8.0 hasta Android 15. |
| | ¿Integración con APIs correcta? | Sí | Sincronización perfecta con Supabase Postgrest y Realtime. |
| **Usabilidad** | ¿Interfaz intuitiva y coherente? | Sí | Basado 100% en Material Design 3 con sistema de temas. |
| | ¿Mensajes de error claros? | Sí | Captura de excepciones con mensajes amigables al usuario. |
| **Fiabilidad** | ¿Maneja fallos sin cerrarse (crashes)? | Sí | Implementación de `try-catch` en ViewModels y Scaffold de seguridad. |
| | ¿Evita pérdida de datos? | Sí | Persistencia inmediata en PostgreSQL tras cada acción del usuario. |
| **Seguridad** | ¿Valida autenticación y cifra claves? | Sí | Supabase Auth maneja tokens JWT y cifrado industrial. |
| | ¿Protegido contra Inyección SQL? | Sí | Acceso vía API Postgrest (Supabase) que parametriza toda consulta. |
| **Mantenibilidad** | ¿Código estructurado y documentado? | Sí | Arquitectura MVVM, Clean Code y manual técnico completo. |
| | ¿Existen pruebas de validación? | Sí | Guía de QA Manual (`MANUAL_TESTING_GUIDE.md`) ejecutada. |
| **Portabilidad** | ¿Fácil de desplegar/migrar? | Sí | Configuración vía `google-services.json` y variables de entorno. |

---
*BH++ Team - Ingeniería de Software con Propósito*
