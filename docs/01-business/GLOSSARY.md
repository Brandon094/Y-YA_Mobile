# Glosario de Términos - YÁYA

Este documento define los términos clave utilizados en el ecosistema de YÁYA para asegurar una comunicación clara entre los equipos de desarrollo, diseño y negocio.

## 1. Roles de Usuario
- **Cuenta Universal:** Concepto central de YÁYA donde un identificador único (ID) permite al usuario alternar entre ser Cliente o Prestador de forma transparente, eliminando la "doble fricción" de crear múltiples cuentas.
- **Cliente:** Persona que utiliza la plataforma para buscar, solicitar y contratar servicios.
- **Prestador (o Proveedor):** Profesional o técnico que ofrece sus servicios.
- **Administrador:** Usuario con privilegios elevados para gestionar categorías, moderar reseñas y supervisar la actividad de la plataforma.

## 2. Conceptos de Negocio
- **Servicio:** La unidad mínima de oferta. Representa una labor específica (ej. "Reparación de fugas de agua") con un precio base y descripción.
- **Categoría:** Clasificación lógica que agrupa servicios similares (ej. "Hogar", "Belleza", "Mecánica") para facilitar la búsqueda.
- **Solicitud (Request):** El flujo transaccional que inicia cuando un cliente pide un servicio y termina con la finalización o cancelación del mismo.
- **Reserva (Booking):** El acuerdo sobre una fecha y hora específica (`scheduled_date`) para realizar el servicio solicitado.
- **Disponibilidad:** El horario semanal definido por el prestador en el que está habilitado para recibir solicitudes.
- **Calificación (Rating):** Evaluación numérica (1 a 5 estrellas) otorgada por el cliente al finalizar un servicio.
- **Reseña (Review):** Comentario textual que acompaña a la calificación para dar feedback detallado sobre la experiencia.

## 3. Estados del Servicio
- **Pendiente (Pending):** La solicitud ha sido enviada por el cliente pero aún no ha sido revisada por el prestador.
- **Aceptado (Accepted):** El prestador ha confirmado su interés y compromiso con la solicitud.
- **En Progreso (In Progress):** El servicio se está llevando a cabo en el momento actual.
- **Completado (Completed):** El servicio ha finalizado satisfactoriamente y está listo para ser calificado.
- **Cancelado (Cancelled):** La solicitud ha sido anulada por cualquiera de las partes antes de su finalización.

## 4. Términos Técnicos
- **Supabase:** Plataforma Backend-as-a-Service (BaaS) utilizada para la base de datos (PostgreSQL), autenticación y almacenamiento.
- **Jetpack Compose:** Framework moderno de Android utilizado para construir la interfaz de usuario de forma declarativa.
- **RLS (Row Level Security):** Políticas de seguridad a nivel de base de datos que aseguran que los usuarios solo puedan acceder a sus propios datos.
- **UUID:** Identificador Universal Único utilizado para las llaves primarias de todas las tablas.
- **Spotlight Effect:** Técnica de UI que oscurece la pantalla excepto por un área específica (recorte) para resaltar un componente durante el tutorial.
- **Pre-flight Check:** Validación previa que se ejecuta antes de una acción crítica (como el registro) para asegurar que los datos sean únicos y válidos.
- **Wizard:** Patrón de diseño que divide un proceso complejo en varios pasos secuenciales y sencillos.
- **RPC (Remote Procedure Call):** Función ejecutada en el servidor (Postgres) invocada desde el cliente para realizar tareas complejas como el borrado atómico.
- **Cascading Delete:** Mecanismo que asegura que al eliminar un registro principal, todos sus datos relacionados en otras tablas se borren automáticamente para mantener la integridad.

---
*Mantenimiento de Glosario por BH++*
