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
| `municipality` | text | DEFAULT 'La Plata' | Municipio de residencia del usuario (`ValidationUtils.HUILA_MUNICIPALITIES`). |
| `role` | varchar | CHECK (client, provider, admin) | Rol asignado en la plataforma. |
| `avatar_url` | text | | URL de la imagen de perfil. |
| `fcm_token` | text | | Token de Firebase para notificaciones push (Hito 4). |
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
| `working_days` | integer[] | DEFAULT '{}' | Lista de días (1=Lunes, 7=Domingo) en que se presta el servicio. |
| `start_time` | time | DEFAULT '08:00:00' | Hora de inicio de la jornada para este servicio. |
| `end_time` | time | DEFAULT '18:00:00' | Hora de finalización de la jornada para este servicio. |
| `municipality` | text | DEFAULT 'La Plata' | Municipio de cobertura del servicio (`ValidationUtils.HUILA_MUNICIPALITIES`). |
| `status` | varchar | CHECK (active, inactive, pending_approval) | Estado de visibilidad (Aprobación en Hito 5). |
| `created_at` | timestamptz | DEFAULT now() | Fecha de publicación del servicio. |

## 4. Tabla: `service_images` (Hito 3)
Galería de fotos de los servicios ofrecidos para el portafolio.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID de la imagen. |
| `service_id` | uuid | FK (services.id) | Referencia al servicio. |
| `image_url` | text | NOT NULL | URL pública en Supabase Storage. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de carga. |

## 5. Tabla: `availability`
Gestión de horarios globales de los prestadores.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID de registro. |
| `provider_id` | uuid | FK (profiles.id) | Prestador dueño del horario. |
| `day_of_week` | integer | CHECK (1-7) | 1=Lunes, 7=Domingo. |
| `start_time` | time | NOT NULL | Hora de inicio. |
| `end_time` | time | NOT NULL | Hora de finalización. |

## 6. Tabla: `requests`
Órdenes de servicio o contratos entre clientes y prestadores.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID de la solicitud. |
| `client_id` | uuid | FK (profiles.id) | Usuario que contrata. |
| `service_id` | uuid | FK (services.id) | Servicio contratado. |
| `final_price` | numeric | DEFAULT 0.00 | Precio final pactado (Post-negociación). |
| `request_description`| text | | Notas adicionales del cliente. |
| `service_address` | text | NOT NULL | Ubicación del servicio. |
| `scheduled_date` | timestamptz | | Fecha y hora programada. |
| `status` | varchar | CHECK (pending, accepted, in_progress, completed, cancelled) | Estado del ciclo de vida de la orden. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de solicitud inicial. |

## 7. Tabla: `ratings`
Feedback y calificaciones post-servicio para reputación.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID único. |
| `request_id` | uuid | FK (requests.id) | Referencia a la solicitud. |
| `client_id` | uuid | FK (profiles.id) | Usuario que califica. |
| `provider_id` | uuid | FK (profiles.id) | Usuario calificado. |
| `score` | integer | CHECK (1-5) | Puntaje en estrellas. |
| `comment` | text | | Comentario detallado de la experiencia. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de la calificación. |

## 8. Tabla: `messages`
Historial de chat interno en tiempo real.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK, DEFAULT gen_v4() | ID del mensaje. |
| `sender_id` | uuid | FK (profiles.id) | Remitente del mensaje. |
| `receiver_id` | uuid | FK (profiles.id) | Destinatario del mensaje. |
| `content` | text | NOT NULL | Contenido del mensaje de texto. |
| `is_read` | boolean | DEFAULT false | Estado de lectura (Visto). |
| `sent_at` | timestamptz | DEFAULT now() | Marca temporal de envío. |

## 9. Tabla: `reports` (Hito 5)
Registro de denuncias para moderación y calidad del ecosistema.

| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | uuid | PK | ID único del reporte. |
| `reporter_id` | uuid | FK (profiles.id) | Usuario denunciante. |
| `reported_user_id` | uuid | FK (profiles.id) | Usuario bajo revisión. |
| `reason` | text | NOT NULL | Motivo de la denuncia. |
| `created_at` | timestamptz | DEFAULT now() | Fecha de creación. |

---
*Documentación de Datos por BH++ Team - Ingeniería de Software*
