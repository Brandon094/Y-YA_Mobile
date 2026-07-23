# 🧪 Guía de Pruebas Manuales (QA) - YÁYA

Este documento detalla los pasos exactos para validar que todas las funcionalidades del sistema YÁYA funcionen correctamente bajo estándares de calidad profesional.

---

## 🏗️ Preparación
1. **Limpieza:** (Opcional) Borrar datos de prueba viejos en Supabase.
2. **Cuentas:** Asegúrate de tener:
   - Una cuenta con rol `admin` (Brandon, Mauricio o Harold).
   - Una cuenta con rol `provider`.
   - Una cuenta con rol `client`.

---

## 🚩 FLUJO 1: Gobernanza y Moderación (Admin)
**Objetivo:** Validar que el administrador tiene el control total.

| Paso | Acción | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- |
| 1 | Iniciar sesión como Admin. | Entra directo al Dashboard Administrativo. | [ ] |
| 2 | Ver lista de Usuarios. | La pestaña "Usuarios" muestra todos los perfiles reales. | [ ] |
| 3 | Ver lista de Pendientes. | Aparecen los servicios que aún no han sido aprobados. | [ ] |
| 4 | Aprobar un servicio ✅. | El servicio desaparece de la lista y se vuelve visible en el Home. | [ ] |
| 5 | Rechazar un servicio ❌. | El servicio queda como 'inactive' y no sale en el Home. | [ ] |

---

## 💼 FLUJO 2: Ciclo de Vida del Talento (Prestador)
**Objetivo:** Validar que un prestador puede ofrecer y gestionar servicios.

| Paso | Acción | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- |
| 1 | Crear un nuevo servicio. | Sale el diálogo: "¡Servicio Publicado! En revisión...". | [ ] |
| 2 | Ir a "Mis Servicios". | El switch está bloqueado (gris) y dice "En Revisión". | [ ] |
| 3 | Tras aprobación de Admin. | El switch se habilita y el prestador puede pausar/activar. | [ ] |
| 4 | Recibir solicitud. | La campana muestra un Badge (numerito) rojo con el conteo. | [ ] |
| 5 | Responder solicitud. | Puede Aceptar, Rechazar o enviar una Contraoferta de precio. | [ ] |

---

## 🛒 FLUJO 3: Experiencia de Contratación (Cliente)
**Objetivo:** Validar que el cliente puede encontrar y contratar talento de forma segura.

| Paso | Acción | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- |
| 1 | Buscar en el Home. | El buscador filtra en tiempo real. | [ ] |
| 2 | Búsqueda vacía. | Sale `EmptyServicesView` con opción de limpiar filtros. | [ ] |
| 3 | Ver detalle de servicio. | El botón de bandera (Reportar) y Chat están visibles. | [ ] |
| 4 | Contratar (Horario). | El sistema bloquea si eliges una hora fuera de la jornada del prestador. | [ ] |
| 5 | Negociar. | En "Mis Pedidos", el cliente puede aceptar o mejorar la oferta del prestador. | [ ] |

---

## 💬 FLUJO 4: Comunicación y Notificaciones (Dual)
**Objetivo:** Validar el tiempo real.

| Paso | Acción | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- |
| 1 | Abrir Chat. | Se carga el historial previo de Supabase. | [ ] |
| 2 | Enviar mensaje. | El mensaje aparece al instante con color primario. | [ ] |
| 3 | Recibir mensaje. | El scroll baja automáticamente y el mensaje llega sin recargar. | [ ] |
| 4 | Notificación Push. | (Con app cerrada) Al crear pedido, el celular del prestador debe pitar/vibrar. | [ ] |

---

## ⭐ FLUJO 5: Cierre y Reputación (Cliente)
**Objetivo:** Validar el sistema de estrellas.

| Paso | Acción | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- |
| 1 | Finalizar servicio. | (Cambiando estado a `completed`). | [ ] |
| 2 | Calificar. | Aparece el botón dorado "Calificar Prestador" en Mis Pedidos. | [ ] |
| 3 | Enviar Reseña. | Se guardan las estrellas y el comentario en la tabla `ratings`. | [ ] |

---
**Generado por el Orquestador Maestro de BH++**
