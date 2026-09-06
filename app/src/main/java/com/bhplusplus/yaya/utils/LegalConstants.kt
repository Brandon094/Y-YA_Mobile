package com.bhplusplus.yaya.utils

object LegalConstants {
    const val TERMS_AND_CONDITIONS = """
# Términos y Condiciones de Uso - YÁYA

Última actualización: Septiembre 2026

Bienvenido a YÁYA, una plataforma operada por BH++ Team. Al utilizar nuestra aplicación móvil, usted acepta cumplir con los siguientes términos y condiciones. Por favor, léalos atentamente.

## 1. Descripción del Servicio
YÁYA es un ecosistema digital que actúa exclusivamente como intermediario para conectar a prestadores de servicios independientes ("Prestadores") con usuarios que buscan contratar dichos servicios ("Clientes"). YÁYA no presta los servicios listados ni es empleador de los Prestadores.

## 2. Registro y Roles de Usuario
* Elegibilidad: Debe tener al menos 15 años para registrarse. En caso de ser menor de edad, el uso debe ser supervisado por un tutor legal.
* Cuenta Universal: YÁYA utiliza un modelo de cuenta única donde el acceso a funcionalidades depende del rol activo:
    - Rol Cliente: Facultad exclusiva para buscar, negociar y contratar servicios.
    - Rol Prestador: Facultad para publicar talentos, gestionar su agenda y también contratar servicios de terceros.
* Veracidad: Usted se compromete a proporcionar información real, incluyendo su documento de identidad verificado.
* Seguridad: Usted es responsable de mantener la confidencialidad de su contraseña.

## 3. Uso de la Plataforma
* Publicación de Servicios: Los Prestadores deben describir sus habilidades de manera honesta. El sistema cuenta con un proceso de moderación administrativa previo a la publicación.
* Negociación: Los precios se acuerdan mediante el Handshake Digital y chat interno de la app.
* Comportamiento: Queda prohibido el uso de lenguaje ofensivo, acoso, spam o la publicación de servicios ilegales.

## 4. Pagos y Comisiones
* Actualmente, YÁYA facilita la negociación, pero el pago se realiza directamente entre las partes bajo las condiciones acordadas. 
* YÁYA se reserva el derecho de implementar pasarelas de pago y esquemas de suscripción (SaaS) en el futuro.

## 5. Sistema de Reputación y Sanciones
* Calificaciones: Los Clientes pueden calificar al Prestador. Estas valoraciones son públicas.
* Moderación: YÁYA utiliza un Semáforo Disciplinario basado en reportes para suspender o eliminar cuentas infractoras.

## 6. Limitación de Responsabilidad
YÁYA no se hace responsable por la calidad de los servicios ni por conflictos surgidos entre usuarios.

## 7. Propiedad Intelectual
Todos los derechos sobre el diseño, código y marca YÁYA pertenecen a BH++ Team.

---
BH++ Team - Ingeniería de Software con Propósito
    """

    const val PRIVACY_POLICY = """
# Política de Privacidad - YÁYA

Última actualización: Septiembre 2026

En YÁYA, operada por BH++ Team, nos tomamos muy en serio la seguridad de sus datos personales. Esta política detalla qué información recopilamos y cómo la protegemos bajo estándares de calidad industrial.

## 1. Información que Recopilamos
Al utilizar YÁYA, recopilamos la siguiente información:
* Identidad: Nombre completo, número de documento de identidad y fecha de nacimiento.
* Contacto: Correo electrónico, número de teléfono y municipio de residencia.
* Perfil: Foto de perfil (avatar) y descripciones de servicios (para prestadores).
* Transacciones: Historial de pedidos, acuerdos de precios y calificaciones.
* Comunicación: Mensajes enviados a través del chat interno de la aplicación.
* Técnica: Token de notificaciones push (FCM) e identificadores de dispositivo.

## 2. Uso de la Información
Utilizamos sus datos exclusivamente para:
* Gestionar su cuenta y perfil según su rol (Cliente o Prestador).
* Facilitar la conexión segura y negociación entre las partes.
* Mantener el sistema de reputación y seguridad (moderación de reportes).

## 3. Seguridad de los Datos
Implementamos estándares de seguridad de nivel industrial:
* Encriptación: Todas las comunicaciones se realizan vía HTTPS.
* Control de Acceso: Utilizamos Row Level Security (RLS) en nuestra base de datos PostgreSQL.
* Persistencia: Las sesiones son gestionadas de manera segura mediante Supabase Auth.

## 4. Terceros y Servicios Externos
YÁYA utiliza infraestructura de Supabase y Firebase (Google). No vendemos ni comercializamos su información personal a anunciantes externos.

## 5. Sus Derechos
Usted tiene derecho a acceder, rectificar o eliminar sus datos personales directamente desde la sección de ajustes de la aplicación.

## 6. Retención de Información
Conservaremos sus datos mientras su cuenta esté activa o sea necesario para cumplir con fines legales o de resolución de conflictos.

---
Comprometidos con la privacidad y la confianza digital.
    """
}
