package com.bhplusplus.yaya.utils

/**
 * MANUALES DE USO INTEGRADOS EN LA APLICACIÓN (v1.2.0)
 * Contenido oficial segregado por rol de usuario para garantizar una experiencia
 * enfocada, técnica y profesional.
 */
object ManualConstants {

    /**
     * Retorna el contenido del manual personalizado según el rol del usuario logueado.
     */
    fun getManualContentForRole(role: String): String {
        return when (role) {
            "admin" -> ADMIN_ROLE_MANUAL_CONTENT
            "provider" -> PROVIDER_ROLE_MANUAL_CONTENT
            else -> CLIENT_ROLE_MANUAL_CONTENT
        }
    }

    // --- 1. MANUAL PARA CLIENTES ---
    val CLIENT_ROLE_MANUAL_CONTENT = """
# Manual de Uso para Clientes - YÁYA (v1.2.0)

Bienvenido a YÁYA. La plataforma donde el talento local se conecta con soluciones inmediatas, transparentes y seguras. Este manual orienta detalladamente el funcionamiento del software para el rol de Cliente.

---

# 1. GUÍA DEL CLIENTE (SOLICITANTE)

## 1.1. Búsqueda y Filtrado Local
* **Búsqueda por Municipio:** Seleccione su municipio (La Plata, Nátaga, Paicol, Neiva, etc.) desde la barra superior para explorar servicios locales de su zona.
* **Exploración por Categorías:** Filtre talentos por rubro o mediante palabras clave en el buscador integrado.
* **Reputación del Prestador:** Consulte las estrellas promedio y las opiniones reales recibidas por el prestador en su trayectoria.
* **Guía Spotlight:** Nuestra App le enseñará a usar la interfaz iluminando botones clave para facilitar su aprendizaje.

## 1.2. Contratación y Negociación ("Handshake")
* **Agendamiento Inteligente:** Programe citas eligiendo dirección, fecha y hora. El sistema valida automáticamente días habilitados y horarios libres.
* **Negociación Handshake Digital:** Proponga una oferta inicial y negocie directamente con el prestador hasta llegar a un acuerdo.
* **Chat en Tiempo Real:** Comuníquese de forma directa e instantánea para coordinar detalles técnicos.
* **Sistema de Calificaciones:** Evalúe al prestador con estrellas y comentarios al finalizar la reserva.

---

# 2. FACULTADES Y RESTRICCIONES DEL CLIENTE

## 2.1. LO QUE PUEDE HACER EL CLIENTE
* Buscar y solicitar servicios en cualquier municipio activo de la plataforma.
* Negociar precios mediante contraofertas antes de confirmar la solicitud.
* Calificar y redactar reseñas sobre el desempeño del prestador al completar el servicio.
* Mantener comunicación directa mediante chat en tiempo real.

## 2.2. LO QUE NO PUEDE HACER EL CLIENTE
* **Publicar Servicios:** Facultad restringida exclusivamente al Rol de Prestador.
* **Agendamiento Inválido:** Prohibido agendar citas en fechas pasadas, horas transcurridas o en días no trabajados por el talento.
* **Registro de Datos Inválidos:** No se permite el registro de documentos de identidad con formato incorrecto ni números telefónicos diferentes de 10 dígitos.
* **Restricción de Edad:** No es posible registrarse si es menor de 15 años cumplidos.

---
*YÁYA - Documento Oficial BH++ Team. Versión 1.2.0*
    """.trimIndent()

    // --- 2. MANUAL PARA PRESTADORES (CLIENTE + PRESTADOR) ---
    val PROVIDER_ROLE_MANUAL_CONTENT = """
# Manual de Uso para Prestadores de Servicios - YÁYA (v1.2.0)

Bienvenido al manual técnico de uso para Prestadores de Servicios. La cuenta de usuario en YÁYA es universal, lo que le permite actuar como cliente solicitante y publicar talentos como prestador.

---

# 1. GESTIÓN DE TALENTOS Y SERVICIOS

## 1.1. Publicación Asistida (Wizard)
* **Proceso en Pasos:** Registre servicios mediante un asistente guiado de 2 etapas para reducir la fatiga visual.
* **Duración Estructurada:** Defina el tiempo estimado del servicio combinando un valor numérico y una unidad (Minutos, Horas, Días).

## 1.2. Gestión de Horarios y Disponibilidad
* **Jornada Maestra Compacta:** Configure sus días y horas hábiles globales en una vista única sin scroll con atajos de un solo clic.
* **Horarios por Servicio:** Asigne días y rangos específicos para cada uno de sus servicios ofertados.
* **Detección de Traslapes:** El sistema le alerta visualmente si intenta asignar un horario que colisiona con otro de sus servicios activos.

## 1.3. Gestión de Solicitudes y Reputación
* **Aceptación o Contraoferta:** Decida si acepta la tarifa del cliente o envía una propuesta económica ajustada.
* **Consulta de Reputación:** Revise su promedio de estrellas y las opiniones reales recibidas por sus clientes.

---

# 2. FUNCIONES DE CLIENTE (CONTRATACIÓN)
Como prestador, usted posee un **Rol Híbrido** y conserva todas las facultades del cliente:
* Búsqueda por municipio, agendamiento de citas, negociación, chat y calificaciones.

---

# 3. FACULTADES Y RESTRICCIONES DEL PRESTADOR

## 3.1. LO QUE PUEDE HACER EL PRESTADOR
* Publicar talentos, gestionar agenda, cargar disponibilidad maestra y contratar servicios de otros colegas.
* Utilizar la Guía Spotlight interactiva para dominar todas las herramientas del sistema.
* Aceptar, contraofertar o rechazar solicitudes recibidas de clientes.

## 3.2. LO QUE NO PUEDE HACER EL PRESTADOR
* **Ofertar Fuera de Jornada:** No es posible seleccionar días u horarios desactivados en la jornada maestra general.
* **Traslapar Horarios:** Bloqueo de guardado si dos servicios coinciden en el mismo intervalo de tiempo el mismo día.
* **Restricción de Edad:** No es posible registrarse si es menor de 15 años cumplidos.

---
*YÁYA - Documento Oficial BH++ Team. Versión 1.2.0*
    """.trimIndent()

    // --- 3. MANUAL MAESTRO PARA ADMINISTRADORES ---
    val ADMIN_ROLE_MANUAL_CONTENT = """
# Manual Maestro de Administración - YÁYA (v1.2.0)

Este documento constituye el manual maestro de la plataforma, cubriendo las funciones del Dashboard Administrativo junto con las capacidades operativas integrales.

---

# 1. MÓDULO ADMINISTRATIVO (DASHBOARD)

## 1.1. Gestión Directa de Usuarios
* **Suspensión y Reactivación:** Inhabilite o habilite cuentas de usuario instantáneamente desde la pestaña 'Usuarios'. La interfaz optimista permite cambios en milisegundos sin recargas.
* **Purga Atómica (RPC):** Al eliminar un usuario, el sistema ejecuta una función server-side que purga en cascada chats, pedidos e imágenes, garantizando una base de datos limpia.

## 1.2. Auditoría de Calidad
* **Revisión de Servicios:** Evaluar, aprobar o pausar publicaciones para garantizar los estándares de calidad y veracidad de YÁYA.
* **Semáforo Disciplinario:** Gestión de reportes agrupados por infractor (Amarillo, Naranja y Rojo) para una toma de decisiones informada.

## 1.3. Capa de Anonimato
* **Protección de Identidad:** Al realizar acciones de moderación, el nombre del administrador se sustituye por 'Equipo de Moderación YÁYA' para resguardar su integridad.

---

# 2. MÓDULO OPERATIVO GENERAL
El administrador dispone de acceso total:
* Configuración de disponibilidad, publicación de servicios, prevención de traslapes y contratación integral.
* Utilización del sistema de tutoriales Spotlight en las 8 pantallas críticas del ecosistema.

---

# 3. FACULTADES Y RESTRICCIONES DEL ADMINISTRADOR

## 3.1. LO QUE PUEDE HACER EL ADMINISTRADOR
* Auditar servicios, gestionar usuarios (suspender/eliminar) y emitir llamados de atención automáticos por chat.
* Consultar métricas de uso y monitorear la salud relacional de la plataforma.

## 3.2. LO QUE NO PUEDE HACER EL ADMINISTRADOR
* **Revelar Identidad:** Exponer datos personales ante usuarios comunitarios.
* **Alterar Acuerdos:** Modificar precios negociados o fechas acordadas válidamente entre usuarios.

---
*YÁYA - Documento Oficial BH++ Team. Versión 1.2.0*
    """.trimIndent()
}
