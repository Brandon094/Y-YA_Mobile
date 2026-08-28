package com.bhplusplus.yaya.utils

object LegalConstants {
    const val TERMS_AND_CONDITIONS = """
# Términos y Condiciones de Uso - YÁYA

Última actualización: Agosto 2026

Bienvenido a YÁYA, una plataforma operada por BH++ Team. Al utilizar nuestra aplicación móvil, usted acepta cumplir con los siguientes términos y condiciones. Por favor, léalos atentamente.

## 1. Descripción del Servicio
YÁYA es un ecosistema digital que actúa exclusivamente como intermediario para conectar a prestadores de servicios independientes ("Prestadores") con usuarios que buscan contratar dichos servicios ("Clientes"). YÁYA no presta los servicios listados ni es empleador de los Prestadores.

## 2. Registro y Cuenta Universal
* Elegibilidad: Debe tener al menos 18 años para registrarse.
* Cuenta Única: YÁYA utiliza un modelo de "Cuenta Universal" donde un mismo usuario puede actuar como Cliente o Prestador.
* Veracidad: Usted se compromete a proporcionar información real, incluyendo su documento de identidad y datos de contacto.
* Seguridad: Usted es responsable de mantener la confidencialidad de su contraseña y de todas las actividades realizadas en su cuenta.

## 3. Uso de la Plataforma
* Publicación de Servicios: Los Prestadores deben describir sus habilidades de manera honesta. El sistema cuenta con un proceso de moderación administrativa previo a la publicación.
* Negociación: Los precios y términos específicos del servicio se acuerdan mediante el flujo de negociación y chat interno de la app.
* Comportamiento: Queda prohibido el uso de lenguaje ofensivo, acoso, spam o la publicación de servicios ilegales.

## 4. Pagos y Comisiones
* Actualmente, YÁYA facilita la negociación del precio, pero el pago se realiza directamente entre el Cliente y el Prestador bajo las condiciones acordadas. 
* YÁYA se reserva el derecho de implementar pasarelas de pago y esquemas de suscripción (SaaS) en versiones futuras, previa notificación a los usuarios.

## 5. Sistema de Reputación y Reportes
* Calificaciones: Al finalizar un servicio, el Cliente puede calificar al Prestador. Estas valoraciones son públicas y forman parte de la reputación del usuario.
* Reportes: Los usuarios pueden reportar incidentes de mal comportamiento. YÁYA se reserva el derecho de suspender o eliminar cuentas que violen estas normas o acumulen reportes negativos.

## 6. Limitación de Responsabilidad
YÁYA no se hace responsable por:
* La calidad, puntualidad o seguridad de los servicios prestados.
* Conflictos surgidos entre usuarios durante o después de la prestación del servicio.
* Daños y perjuicios directos o indirectos derivados del uso de la plataforma.

## 7. Propiedad Intelectual
Todos los derechos sobre el diseño, código, logos y marca YÁYA pertenecen a BH++ Team. Queda prohibida su reproducción total o parcial sin autorización.

## 8. Modificaciones
Nos reservamos el derecho de modificar estos términos en cualquier momento. El uso continuo de la aplicación tras dichos cambios constituye la aceptación de los nuevos términos.

---
BH++ Team - Ingeniería de Software con Propósito
    """

    const val PRIVACY_POLICY = """
# Política de Privacidad - YÁYA

Última actualización: Agosto 2026

En YÁYA, operada por BH++ Team, nos tomamos muy en serio la seguridad de sus datos personales. Esta política detalla qué información recopilamos y cómo la protegemos.

## 1. Información que Recopilamos
Al utilizar YÁYA, recopilamos la siguiente información:
* Identidad: Nombre completo, número de documento de identidad y fecha de nacimiento.
* Contacto: Correo electrónico, número de teléfono y dirección de residencia/prestación.
* Perfil: Foto de perfil (avatar) y descripciones de servicios.
* Transacciones: Historial de pedidos, acuerdos de precios y calificaciones recibidas.
* Comunicación: Mensajes enviados a través del chat interno de la aplicación.
* Técnica: Token de notificaciones push (FCM) e identificadores únicos de dispositivo.

## 2. Uso de la Información
Utilizamos sus datos exclusivamente para:
* Gestionar su cuenta y perfil.
* Facilitar la conexión y negociación entre Clientes y Prestadores.
* Enviar notificaciones en tiempo real sobre el estado de sus solicitudes.
* Mantener el sistema de reputación y seguridad (moderación de reportes).
* Mejorar la experiencia de usuario y el rendimiento de la aplicación.

## 3. Seguridad de los Datos
Implementamos estándares de seguridad de nivel industrial:
* Encriptación: Todas las comunicaciones se realizan vía HTTPS.
* Control de Acceso: Utilizamos Row Level Security (RLS) en nuestra base de datos para asegurar que cada usuario acceda únicamente a la información que le corresponde.
* Persistencia: Las sesiones son gestionadas de manera segura mediante autenticación de Supabase.

## 4. Terceros y Servicios Externos
YÁYA utiliza infraestructura de terceros confiables:
* Supabase: Para la base de datos PostgreSQL, autenticación y almacenamiento.
* Firebase (Google): Para el envío de notificaciones push a su dispositivo móvil.
No vendemos ni comercializamos su información personal a anunciantes externos.

## 5. Sus Derechos (ARCO)
Usted tiene derecho a:
* Acceder a sus datos personales guardados en el sistema.
* Rectificar o actualizar cualquier información incorrecta desde la sección "Editar Perfil".
* Cancelar o eliminar su cuenta de la plataforma.
* Oponerse al procesamiento de sus datos para fines no relacionados con la prestación del servicio principal.

## 6. Retención de Información
Conservaremos sus datos mientras su cuenta esté activa o sea necesario para cumplir con fines legales o de resolución de conflictos de reputación dentro de la plataforma.

## 7. Contacto
Si tiene dudas sobre el tratamiento de sus datos, puede contactar al equipo de BH++ Team a través de los canales oficiales del proyecto.

---
Comprometidos con la privacidad y la confianza digital.
    """
}
