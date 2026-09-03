package com.bhplusplus.yaya.utils

/**
 * MANUALES DE USO INTEGRADOS EN LA APLICACIÓN (v1.1.0)
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
# Manual de Uso para Clientes - YÁYA (v1.1.0)

Bienvenido a YÁYA. La plataforma donde el talento local se conecta con soluciones inmediatas, transparentes y seguras. Este manual orienta detalladamente el funcionamiento del software para el rol de Cliente.

---

# 1. GUÍA DEL CLIENTE (SOLICITANTE)

## 1.1. Búsqueda y Filtrado Local
* **Búsqueda por Municipio:** Seleccione su municipio (La Plata, Nátaga, Paicol, Neiva, Garzón, Pitalito, etc.) desde la barra superior para explorar servicios locales de su zona.
* **Exploración por Categorías:** Filtre talentos por rubro (Hogar, Tecnología, Mascotas, Salud, etc.) o mediante palabras clave en el buscador integrado.
* **Reputación del Prestador:** Consulte las estrellas promedio y las opiniones reales recibidas por el prestador en su trayectoria dentro de la plataforma.

## 1.2. Contratación y Negociación ("Handshake")
* **Agendamiento Inteligente:** Programe citas eligiendo dirección, fecha y hora. El sistema le permite elegir únicamente días habilitados y horarios disponibles.
* **Negociación de Tarifas ("Handshake"):** Proponga una oferta económica inicial y negocie directamente con el prestador hasta llegar a un acuerdo transaccional.
* **Chat en Tiempo Real:** Comuníquese de forma directa e instantánea con el prestador para coordinar detalles del servicio.
* **Sistema de Calificaciones:** Evalúe al prestador con estrellas (1 a 5) y comentarios al finalizar el servicio.

---

# 2. FACULTADES Y RESTRICCIONES DEL CLIENTE

## 2.1. LO QUE PUEDE HACER EL CLIENTE
* Buscar y solicitar servicios en cualquier municipio registrado en la plataforma.
* Negociar precios mediante contraofertas antes de confirmar la solicitud.
* Calificar y redactar reseñas sobre el desempeño del prestador al completar la reserva.
* Mantener comunicación directa mediante chat en tiempo real.

## 2.2. LO QUE NO PUEDE HACER EL CLIENTE
* **Agendamiento en Fechas o Horas Pasadas:** El sistema prohíbe de forma estricta agendar citas en fechas pasadas o en horas que hayan transcurrido durante el día.
* **Agendamiento Fuera de Jornada:** No es posible seleccionar horas ni días en los que el prestador no preste servicio o mantenga compromisos previos.
* **Registro de Datos Inválidos:** No se permite el registro de documentos de identidad con formato incorrecto (deben tener entre 6 y 12 dígitos) ni números telefónicos diferentes de 10 dígitos.
* **Infracciones de Convivencia:** Prohibido el uso del chat para conductas inapropiadas o engañosas. Las cuentas infractoras serán sujetas a advertencias disciplinarias, suspensión o eliminación permanente.

---
*YÁYA - Documento Oficial BH++ Team. Versión 1.1.0*
    """.trimIndent()

    // --- 2. MANUAL PARA PRESTADORES (CLIENTE + PRESTADOR) ---
    val PROVIDER_ROLE_MANUAL_CONTENT = """
# Manual de Uso para Prestadores de Servicios - YÁYA (v1.1.0)

Bienvenido al manual técnico de uso para Prestadores de Servicios. La cuenta de usuario en YÁYA es universal, lo que le permite actuar como cliente solicitante y publicar talentos como prestador.

---

# 1. GESTIÓN DE TALENTOS Y SERVICIOS

## 1.1. Publicación de Servicios
* **Publicación y Edición:** Registre servicios especificando título, descripción, precio base, materiales e imágenes de portafolio.
* **Municipio de Atención:** Asigne la localidad específica (La Plata, Nátaga, Neiva, etc.) donde prestará el servicio.

## 1.2. Gestión de Horarios y Disponibilidad
* **Jornada Maestra ("Mi Horario"):** Configure sus días y horas hábiles globales de trabajo en la sección "Mi Horario de Trabajo".
* **Horarios por Servicio:** Asigne días y rangos de tiempo específicos para cada uno de sus servicios.
* **Detección Inteligente de Traslapes:** El sistema verifica y le notifica automáticamente si intenta asignar a un servicio un horario que colisiona con otro de sus servicios activos.

## 1.3. Gestión de Solicitudes y Reputación
* **Aceptación o Contraoferta:** Decida si acepta la tarifa propuesta por el cliente o envía una contraoferta ajustada.
* **Consulta de Reputación:** Revise su promedio de estrellas y las opiniones acumuladas por sus clientes desde la sección "Mi Reputación y Reseñas" de su perfil.

---

# 2. FUNCIONES DE CLIENTE (CONTRATACIÓN)
Como prestador, también puede utilizar YÁYA para solicitar servicios de otros talentos:
* Búsqueda por municipio, agendamiento de citas, negociación transaccional, chat en tiempo real y envío de calificaciones.

---

# 3. FACULTADES Y RESTRICCIONES DEL PRESTADOR

## 3.1. LO QUE PUEDE HACER EL PRESTADOR
* Publicar múltiples servicios definiendo municipio de atención y horarios específicos.
* Utilizar la función de carga rápida de jornada maestra al configurar días de atención.
* Aceptar, contraofertar o rechazar solicitudes recibidas.
* Consultar su historial de opiniones y reputación acumulada.

## 3.2. LO QUE NO PUEDE HACER EL PRESTADOR
* **Ofertar Fuera de Jornada:** No es posible seleccionar días u horarios desactivados en la jornada maestra.
* **Traslapar Horarios:** No es posible asignar dos servicios distintos en el mismo intervalo de tiempo durante el mismo día.
* **Publicaciones Engañosas:** Prohibido publicar precios irreales o imágenes no autorizadas. Todas las publicaciones son auditadas por el equipo administrativo.

---
*YÁYA - Documento Oficial BH++ Team. Versión 1.1.0*
    """.trimIndent()

    // --- 3. MANUAL MAESTRO PARA ADMINISTRADORES ---
    val ADMIN_ROLE_MANUAL_CONTENT = """
# Manual Maestro de Administración - YÁYA (v1.1.0)

Este documento constituye el manual maestro de la plataforma, cubriendo las funciones del Dashboard Administrativo junto con las capacidades operativas de cliente y prestador.

---

# 1. MÓDULO ADMINISTRATIVO (DASHBOARD)

## 1.1. Auditoría de Calidad
* **Revisión de Servicios:** Evaluar y aprobar o pausar publicaciones de servicios para garantizar los estándares de calidad de YÁYA.
* **Control de Contenidos:** Retirar del catálogo cualquier publicación que no cumpla con las políticas del sistema.

## 1.2. Moderación Disciplinaria Progresiva
* **Semáforo de Severidad:** El panel agrupa las denuncias comunitarias por usuario:
  * Nivel Amarillo (1-2 Reportes): Llamado de atención recomendado.
  * Nivel Naranja (3-4 Reportes): Suspensión temporal de servicios.
  * Nivel Rojo (5+ Reportes): Eliminación permanente de la cuenta.
* **Llamado de Atención Automático:** Enviar notificaciones disciplinarias vía chat desde el panel.
* **Acciones Directas:** Desactivación inmediata de servicios o eliminación permanente de cuentas infractoras.

## 1.3. Capa de Anonimato
* **Protección de Identidad:** Al realizar acciones de moderación con usuarios comunitarios, el nombre del administrador se sustituye por "Equipo de Moderación YÁYA" y el avatar por el isotipo oficial.

---

# 2. MÓDULO OPERATIVO GENERAL
El usuario administrador dispone de acceso integral a todas las funciones de la aplicación:
* Configuración de disponibilidad maestra, publicación de servicios por municipio y prevención de traslapes.
* Contratación de servicios, negociación transaccional, chat en tiempo real y módulo de calificaciones.

---

# 3. FACULTADES Y RESTRICCIONES DEL ADMINISTRADOR

## 3.1. LO QUE PUEDE HACER EL ADMINISTRADOR
* Aprobar o rechazar solicitudes de publicación de servicios en la plataforma.
* Aplicar sanciones disciplinarias mediante avisos, suspensión o eliminación definitiva de cuentas.
* Operar en la comunidad bajo la capa de identidad protegida.

## 3.2. LO QUE NO PUEDE HACER EL ADMINISTRADOR
* **Revelar Identidad:** Exponer datos personales o identidad real ante usuarios objeto de moderación.
* **Alterar Acuerdos Legítimos:** Interferir o modificar negociaciones cerradas correctamente entre clientes y prestadores.

---
*YÁYA - Documento Oficial BH++ Team. Versión 1.1.0*
    """.trimIndent()
}
