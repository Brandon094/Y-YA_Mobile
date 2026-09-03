# Manual Maestro de Administración - YÁYA (v1.1.0)

Este documento constituye la guía de referencia técnica y operativa para los usuarios con rol de Administrador (Equipo de Moderación BH++). Estipula los procedimientos, herramientas y límites para la supervisión de contenidos, moderación disciplinaria y control de calidad del ecosistema YÁYA.

---

## Visor del Manual Integrado en la Aplicación
El contenido de este manual maestro se encuentra integrado en la aplicación móvil para el rol administrativo:
1. El usuario autenticado como administrador (`role == "admin"`) ingresa a la pestaña "Mi Perfil".
2. Selecciona la opción "Manual de Uso de la App".
3. El sistema invoca la función `ManualConstants.getManualContentForRole("admin")` y despliega la vista `LegalViewerScreen` bajo el título "Manual Maestro de YÁYA", permitiendo la lectura inmersiva del documento maestro sin emojis ni distracciones.

---

## 1. Acceso al Dashboard Administrativo
El acceso al Panel Administrativo está integrado en el flujo de navegación principal de la aplicación:
1. Al iniciar sesión, el Administrador aterriza en la pantalla principal ("Home"), disponiendo de acceso completo para explorar la oferta de servicios, contratar o interactuar como usuario general.
2. Para ingresar al módulo de moderación, navega a la sección "Mi Perfil".
3. En la parte superior de las opciones de perfil, se despliega la acción exclusiva "Panel Administrativo", la cual incluye un indicador de conteo (badge) con la cantidad de servicios pendientes por auditar.
4. El Dashboard Administrativo permite el retorno fluido a la vista general mediante la acción de retroceso del sistema (`Back`).

---

## 2. Auditoría y Control de Calidad de Servicios
Todo servicio creado o actualizado por la comunidad de prestadores ingresa automáticamente en estado "En Revisión" (`status = 'in_review'`) hasta su validación por el equipo administrativo.

* **Criterios de Verificación:** Evaluar que el título, descripción, categoría e imágenes adjuntas cumplan los estándares de veracidad, respeto y calidad exigidos por YÁYA.
* **Validación de Cobertura Geográfica:** Verificar que el municipio asignado al servicio (La Plata, Nátaga, Paicol, Tesalia, Garzón, Neiva, Pitalito, Gigante) corresponda a una zona de cobertura válida según los registros del prestador.
* **Aprobación de Publicación:** Al aprobar el servicio, su estado cambia a `active`, quedando visible en el catálogo público para los clientes del municipio correspondiente.
* **Pausa o Rechazo de Publicación:** Ante la detección de precios irrisorios, información engañosa o contenido inapropiado, el administrador procede a pausar el servicio (`status = 'paused'`) para notificar al prestador.

---

## 3. Gestión de Reportes Comunitarios y Semáforo Disciplinario
El módulo de atención a denuncias agrupa automáticamente las quejas emitidas por los usuarios según el perfil del infractor registrado (`ReportedUserSummary`).

### Semáforo de Severidad Progresiva
* **Nivel Amarillo (1-2 Reportes - Riesgo Bajo):** Se recomienda emitir un Llamado de Atención preventivo.
* **Nivel Naranja (3-4 Reportes - Riesgo Medio):** Se recomienda la Suspensión Temporal de los servicios del prestador (`status = 'paused'`).
* **Nivel Rojo (5 o más Reportes - Riesgo Alto):** Se recomienda la Eliminación Definitiva de la Cuenta.

### Acciones Directas de Moderación
* **Llamado de Atención Automático:** Envía un mensaje disciplinario estandarizado vía chat directo al usuario reportado desde el panel de administración, informando la falta cometida sin revelar la identidad del moderador.
* **Suspender Prestador:** Desactiva de forma inmediata la totalidad de los servicios asociados a la cuenta del infractor (`status = 'paused'`).
* **Eliminar Cuenta:** Cancela la cuenta de usuario de manera definitiva y remueve sus datos de la plataforma en caso de reincidencia o infracción grave.

---

## 4. Capa de Anonimato Protegido ("Equipo de Moderación YÁYA")
Para resguardar la seguridad e integridad del equipo de administración, el sistema aplica una capa de enmascaramiento de identidad:

* **Enmascaramiento en Chat y Notificaciones:** Toda interacción de un administrador con usuarios comunitarios (incluyendo llamados de atención y mensajes de soporte) reemplaza el nombre personal del moderador por la denominación oficial "Equipo de Moderación YÁYA" y su avatar por el isotipo institucional de la marca.
* **Notificaciones Push Enmascaradas:** Las alertas generadas mediante Edge Functions se envían formalmente bajo la firma "Equipo de Moderación YÁYA".
* **Comunicación Interna:** La identidad real de los administradores únicamente es visible en las interacciones entre miembros del equipo directivo.

---

## 5. Matriz de Facultades y Restricciones del Administrador

### LO QUE PUEDE HACER EL ADMINISTRADOR (FACULTADES)
1. **Auditar y Aprobar o Pausar Servicios:** Gestionar la visibilidad pública del catálogo de prestaciones según los estándares del sistema.
2. **Emitir Llamados de Atención Automáticos:** Notificar advertencias disciplinarias vía chat a los usuarios reportados.
3. **Aplicar Sanciones Progresivas:** Suspender servicios o eliminar cuentas de infractores según el semáforo disciplinario.
4. **Operar bajo la Capa de Anonimato Protegido:** Interactuar en la plataforma protegiendo su identidad bajo la denominación "Equipo de Moderación YÁYA".
5. **Monitorear el Ecosistema:** Consultar métricas operativas, usuarios registrados y estado general de la plataforma.

### LO QUE NO PUEDE HACER EL ADMINISTRADOR (RESTRICCIONES)
1. **Exponer su Identidad Real:** Inhabilitado para revelar datos personales o nombres reales ante usuarios objeto de moderación.
2. **Alterar Acuerdos Legítimos:** Inhabilitado para modificar valores económicamente negociados, fechas acordadas o compromisos cerrados entre cliente y prestador.
3. **Eliminar Cuentas sin Justificación:** Prohibido aplicar sanciones definitivas sin un historial razonable de denuncias o una falta grave debidamente documentada.

---
*YÁYA - Documento Oficial de Operaciones Administrativas. BH++ Team (2026).*
