# Diccionario de Datos - YÁYA

Este documento detalla la definición técnica de cada tabla y columna en la base de datos de YÁYA (Supabase/PostgreSQL).

## 1. Tabla: `profiles`
Información de identidad de los usuarios, vinculada a `auth.users`.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, FK (auth.users) | Identificador único del usuario. |
| `full_name` | varchar | NOT NULL | Nombre completo del usuario. |
| `phone` | varchar | | Número de contacto telefónico. |
| `document_id` | varchar | UNIQUE | DNI, Cédula o ID oficial. |
| `birth_date` | date | | Fecha de nacimiento. |
| `address` | text | | Dirección física. |
| `role` | varchar | CHECK (client, provider, admin) | Rol asignado en la plataforma. |
| `avatar_url` | text | | URL de la imagen de perfil. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de creación del perfil. |

## 2. Tabla: `categories`
Clasificación de los servicios ofrecidos.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID único de categoría. |
| `name` | varchar | UNIQUE, NOT NULL | Nombre (ej. "Limpieza", "Fontanería"). |
| `description` | text | | Descripción de la categoría. |
| `icon_name` | varchar | | Identificador del icono para el frontend. |

## 3. Tabla: `services`
Ofertas específicas publicadas por los prestadores.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID único del servicio. |
| `provider_id` | uuid | FK (profiles.id) | ID del prestador que ofrece el servicio. |
| `category_id` | uuid | FK (categories.id) | Categoría a la que pertenece. |
| `title` | varchar | NOT NULL | Título del anuncio. |
| `description` | text | NOT NULL | Descripción detallada. |
| `price` | numeric | NOT NULL | Precio base. |
| `estimated_time`| varchar | | Tiempo estimado de duración. |
| `materials_included`| boolean | DEFAULT false | Indica si incluye materiales. |
| `extra_cost` | numeric | DEFAULT 0.00 | Costos adicionales comunes. |
| `status` | varchar | CHECK (active, inactive) | Estado de visibilidad. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de publicación del servicio. |

## 4. Tabla: `availability`
Gestión de horarios de los prestadores.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID de registro. |
| `provider_id` | uuid | FK (profiles.id) | Prestador dueño del horario. |
| `day_of_week` | integer | CHECK (1-7) | 1=Lunes, 7=Domingo. |
| `start_time` | time | NOT NULL | Hora de inicio. |
| `end_time` | time | NOT NULL | Hora de finalización. |

## 5. Tabla: `requests`
Órdenes de servicio o contratos entre clientes y prestadores.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID de la solicitud. |
| `client_id` | uuid | FK (profiles.id) | Usuario que contrata. |
| `service_id` | uuid | FK (services.id) | Servicio contratado. |
| `final_price` | numeric | DEFAULT 0.00 | Precio final pactado. |
| `request_description`| text | | Notas adicionales del cliente. |
| `service_address` | text | NOT NULL | Ubicación del servicio. |
| `scheduled_date` | timestamptz | | Fecha programada. |
| `status` | varchar | CHECK (pending, accepted, ...) | Estado de la orden. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de solicitud inicial. |

## 6. Tabla: `ratings`
Feedback y calificaciones post-servicio.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID único. |
| `request_id` | uuid | FK (requests.id) | Referencia a la solicitud. |
| `client_id` | uuid | FK (profiles.id) | Usuario que califica. |
| `provider_id` | uuid | FK (profiles.id) | Usuario calificado. |
| `score` | integer | CHECK (1-5) | Puntaje. |
| `comment` | text | | Comentario detallado. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de la calificación. |

## 7. Tabla: `messages`
Historial de chat interno.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID del mensaje. |
| `sender_id` | uuid | FK (profiles.id) | Quien envía. |
| `receiver_id` | uuid | FK (profiles.id) | Quien recibe. |
| `content` | text | NOT NULL | Texto del mensaje. |
| `is_read` | boolean | DEFAULT false | Estado de lectura. |
| `sent_at` | timestamptz | DEFAULT now() | Fecha y hora de envío. |

---
*Documentación de Datos por BH++*
