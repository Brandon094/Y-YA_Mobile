# 🗄️ Esquema DDL de Base de Datos - YÁYA Supabase PostgreSQL

**Versión:** 1.2.0  
**Motor DB:** PostgreSQL (Supabase)  
**Ubicación:** `docs/02-architecture/DATABASE_SCHEMA.md`  

---

## 1. 🔍 Resumen del Esquema Relacional

Este documento contiene la definición técnica exacta de creación de tablas (DDL - Data Definition Language) para la base de datos PostgreSQL de la plataforma **YÁYA**. Refleja la estructura de producción, restricciones de clave primaria (`PRIMARY KEY`), relaciones de clave foránea (`FOREIGN KEY`), restricciones de validación (`CHECK`) y valores por defecto (`DEFAULT`).

---

## 2. 📜 Script DDL de Creación de Tablas (PostgreSQL)

```sql
-- ====================================================================
-- ESQUEMA DDL COMPLETO DE BASE DE DATOS - YÁYA PLATFORM v1.2.0
-- ====================================================================

-- 1. TABLA PUBLIC.PROFILES (Identidad de Usuarios)
CREATE TABLE public.profiles (
  id uuid NOT NULL,
  full_name character varying NOT NULL,
  phone character varying,
  document_id character varying UNIQUE,
  birth_date date,
  address text,
  role character varying NOT NULL CHECK (role::text = ANY (ARRAY['client'::character varying, 'provider'::character varying, 'admin'::character varying]::text[])),
  avatar_url text,
  created_at timestamp with time zone DEFAULT now(),
  fcm_token text,
  municipality text DEFAULT 'La Plata'::text,
  is_suspended boolean DEFAULT false,
  CONSTRAINT profiles_pkey PRIMARY KEY (id),
  CONSTRAINT profiles_id_fkey FOREIGN KEY (id) REFERENCES auth.users(id)
);

-- 2. TABLA PUBLIC.CATEGORIES (Rubros de Servicios)
CREATE TABLE public.categories (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  name character varying NOT NULL UNIQUE,
  description text,
  icon_name character varying,
  CONSTRAINT categories_pkey PRIMARY KEY (id)
);

-- 3. TABLA PUBLIC.SERVICES (Catálogo de Servicios Publicados)
CREATE TABLE public.services (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  provider_id uuid NOT NULL,
  category_id uuid NOT NULL,
  title character varying NOT NULL,
  description text NOT NULL,
  price numeric NOT NULL,
  estimated_time character varying,
  materials_included boolean DEFAULT false,
  extra_cost numeric DEFAULT 0.00,
  status character varying DEFAULT 'pending_approval'::character varying CHECK (status::text = ANY (ARRAY['active'::text, 'inactive'::text, 'pending_approval'::text])),
  created_at timestamp with time zone DEFAULT now(),
  working_days ARRAY DEFAULT '{}'::integer[],
  start_time time without time zone DEFAULT '08:00:00'::time without time zone,
  end_time time without time zone DEFAULT '18:00:00'::time without time zone,
  municipality text DEFAULT 'La Plata'::text,
  CONSTRAINT services_pkey PRIMARY KEY (id),
  CONSTRAINT services_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.profiles(id),
  CONSTRAINT services_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id)
);

-- 4. TABLA PUBLIC.AVAILABILITY (Jornada Maestra del Prestador)
CREATE TABLE public.availability (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  provider_id uuid NOT NULL,
  day_of_week integer NOT NULL CHECK (day_of_week >= 1 AND day_of_week <= 7),
  start_time time without time zone NOT NULL,
  end_time time without time zone NOT NULL,
  CONSTRAINT availability_pkey PRIMARY KEY (id),
  CONSTRAINT availability_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.profiles(id)
);

-- 5. TABLA PUBLIC.REQUESTS (Solicitudes de Contratación)
CREATE TABLE public.requests (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  client_id uuid NOT NULL,
  service_id uuid NOT NULL,
  request_description text,
  service_address text NOT NULL,
  scheduled_date timestamp with time zone,
  status character varying DEFAULT 'pending'::character varying CHECK (status::text = ANY (ARRAY['pending'::character varying, 'accepted'::character varying, 'in_progress'::character varying, 'completed'::character varying, 'cancelled'::character varying]::text[])),
  created_at timestamp with time zone DEFAULT now(),
  final_price numeric DEFAULT 0.00,
  CONSTRAINT requests_pkey PRIMARY KEY (id),
  CONSTRAINT requests_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.profiles(id),
  CONSTRAINT requests_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.services(id)
);

-- 6. TABLA PUBLIC.RATINGS (Calificaciones y Reputación)
CREATE TABLE public.ratings (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  request_id uuid NOT NULL,
  client_id uuid NOT NULL,
  provider_id uuid NOT NULL,
  score integer NOT NULL CHECK (score >= 1 AND score <= 5),
  comment text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT ratings_pkey PRIMARY KEY (id),
  CONSTRAINT ratings_request_id_fkey FOREIGN KEY (request_id) REFERENCES public.requests(id),
  CONSTRAINT ratings_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.profiles(id),
  CONSTRAINT ratings_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.profiles(id)
);

-- 7. TABLA PUBLIC.MESSAGES (Mensajería de Chat en Tiempo Real)
CREATE TABLE public.messages (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  sender_id uuid NOT NULL,
  receiver_id uuid NOT NULL,
  content text NOT NULL,
  is_read boolean DEFAULT false,
  sent_at timestamp with time zone DEFAULT now(),
  CONSTRAINT messages_pkey PRIMARY KEY (id),
  CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.profiles(id),
  CONSTRAINT messages_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.profiles(id)
);

-- 8. TABLA PUBLIC.REPORTS (Denuncias y Moderación)
CREATE TABLE public.reports (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  reporter_id uuid NOT NULL,
  reported_user_id uuid NOT NULL,
  reason text NOT NULL,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT reports_pkey PRIMARY KEY (id),
  CONSTRAINT reports_reporter_id_fkey FOREIGN KEY (reporter_id) REFERENCES public.profiles(id),
  CONSTRAINT reports_reported_user_id_fkey FOREIGN KEY (reported_user_id) REFERENCES public.profiles(id)
);

-- 9. TABLA PUBLIC.SERVICE_IMAGES (Galería del Portafolio)
CREATE TABLE public.service_images (
  id uuid NOT NULL DEFAULT uuid_generate_v4(),
  service_id uuid,
  image_url text NOT NULL,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT service_images_pkey PRIMARY KEY (id),
  CONSTRAINT service_images_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.services(id)
);
```

---

## 3. 🔗 Mapa de Claves Foráneas (Foreign Keys Summary)

- `profiles.id` ➔ `auth.users.id`
- `services.provider_id` ➔ `profiles.id`
- `services.category_id` ➔ `categories.id`
- `availability.provider_id` ➔ `profiles.id`
- `requests.client_id` ➔ `profiles.id`
- `requests.service_id` ➔ `services.id`
- `ratings.request_id` ➔ `requests.id`
- `ratings.client_id` ➔ `profiles.id`
- `ratings.provider_id` ➔ `profiles.id`
- `messages.sender_id` ➔ `profiles.id`
- `messages.receiver_id` ➔ `profiles.id`
- `reports.reporter_id` ➔ `profiles.id`
- `reports.reported_user_id` ➔ `profiles.id`
- `service_images.service_id` ➔ `services.id`

---
*Esquema oficial de base de datos para la plataforma YÁYA - BH++ Team*
