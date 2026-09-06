# 🛡️ Políticas RLS (Row Level Security) y Permisos Administrativos - YÁYA Supabase

**Versión:** 1.2.0  
**Motor DB:** Supabase PostgreSQL  
**Ubicación:** `docs/02-architecture/SUPABASE_RLS_POLICIES.md`  

---

## 1. 🔍 Resumen Ejecutivo de Seguridad y RLS

En la plataforma **YÁYA**, la seguridad de los datos se gestiona directamente en la capa de la base de datos PostgreSQL mediante **Row Level Security (RLS)**. RLS garantiza que cada usuario solo pueda modificar o eliminar sus propios datos, previniendo accesos no autorizados a través de la API REST / Postgrest.

Para permitir que las cuentas con rol `'admin'` gestionen y modifiquen la plataforma directamente desde la App móvil (sin requerir acceso manual a la consola de Supabase), se establecen políticas RLS especiales para administradores sobre todas las tablas del esquema relacional (`profiles`, `services`, `availability`, `requests`, `messages`, `reports`, `ratings`, `service_images`).

### **Garantía de Borrado Atómico en Cascada sin Violación de Claves Foráneas (`Code 23503`)**
Para evitar la excepción PostgreSQL `Code 23503` (`requests_client_id_fkey`) y eliminar servicios u objetos huérfanos, el método `AdminViewModel.deleteUserAccount` ejecuta una purga atómica en 8 fases respetando el orden de dependencias relacionales:
1. `ratings` (Calificaciones/Reseñas emitidas o recibidas)
2. `requests` (Solicitudes creadas como cliente o recibidas por servicios del prestador)
3. `messages` (Mensajes de chat)
4. `reports` (Denuncias y reportes)
5. `service_images` (Galería de imágenes de los servicios del prestador)
6. `services` (Servicios y talentos del prestador)
7. `availability` (Jornada maestra y horarios de atención)
8. `profiles` (Perfil principal del usuario)

---

## 2. 📊 Matriz Completa de Políticas RLS por Tabla

### **1. Tabla `public.profiles`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Los perfiles son visibles para todos` | `SELECT` | `authenticated` | `true` |
| `Los usuarios pueden editar su propio perfil` | `UPDATE` | `authenticated` | `auth.uid() = id` |
| `Permitir inserción de perfiles propios` | `INSERT` | `authenticated` | `auth.uid() = id` |
| **`Admins pueden eliminar perfiles`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |
| **`Admins pueden actualizar perfiles`** | **`UPDATE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |
| **`Admins control total sobre perfiles`** | **`ALL`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **2. Tabla `public.services`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Servicios visibles para todos` | `SELECT` | `authenticated` | `true` |
| `Usuarios crean sus servicios` | `INSERT` | `authenticated` | `auth.uid() = provider_id` |
| `Dueños editan sus servicios` | `UPDATE` | `authenticated` | `auth.uid() = provider_id` |
| **`Admins pueden eliminar servicios`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |
| `Admins control total sobre servicios` | `ALL` | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **3. Tabla `public.availability`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Disponibilidad visible para todos` | `SELECT` | `authenticated` | `true` |
| `Prestadores manejan su propia disponibilidad` | `ALL` | `authenticated` | `auth.uid() = provider_id` |
| **`Admins pueden eliminar disponibilidad`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **4. Tabla `public.messages`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Usuarios ven sus propios mensajes` | `SELECT` | `authenticated` | `auth.uid() = sender_id OR auth.uid() = receiver_id` |
| `Usuarios pueden enviar mensajes` | `INSERT` | `authenticated` | `auth.uid() = sender_id` |
| `Permitir marcar mensajes como leídos` | `UPDATE` | `authenticated` | `auth.uid() = receiver_id` |
| **`Admins pueden eliminar mensajes`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **5. Tabla `public.requests`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Participantes ven sus solicitudes` | `SELECT` | `authenticated` | `auth.uid() = client_id OR auth.uid() = provider_id` |
| `Usuarios pueden crear solicitudes` | `INSERT` | `authenticated` | `auth.uid() = client_id` |
| `Usuarios pueden actualizar sus solicitudes` | `UPDATE` | `authenticated` | `auth.uid() = client_id OR auth.uid() = provider_id` |
| **`Admins pueden eliminar solicitudes`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **6. Tabla `public.reports`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Reportes visibles por autor y admins` | `SELECT` | `authenticated` | `auth.uid() = reporter_id OR (SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |
| `Usuarios pueden denunciar comportamiento` | `INSERT` | `authenticated` | `auth.uid() = reporter_id` |
| **`Admins pueden eliminar reportes`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **7. Tabla `public.ratings`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Calificaciones visibles para todos` | `SELECT` | `authenticated` | `true` |
| `Clientes pueden calificar servicios` | `INSERT` | `authenticated` | `auth.uid() = client_id` |
| **`Admins pueden eliminar calificaciones`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin'` |

---

### **8. Tabla `public.categories`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Categorías visibles para todos` | `SELECT` | `authenticated` | `true` |

---

### **9. Tabla `public.service_images`**
| Nombre de Política | Comando SQL | Aplica a | Expresión USING / WITH CHECK |
| :--- | :--- | :--- | :--- |
| `Imagenes de servicio visibles para todos` | `SELECT` | `authenticated` | `true` |
| `Solo dueños suben imagenes de servicio` | `INSERT` | `authenticated` | `EXISTS (SELECT 1 FROM public.services WHERE id = service_id AND provider_id = auth.uid())` |
| **`Admins y dueños pueden eliminar imagenes de servicio`** | **`DELETE`** | `authenticated` | `(SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin' OR EXISTS (SELECT 1 FROM public.services WHERE id = service_id AND provider_id = auth.uid())` |

---

## 3. 🚀 Script SQL de Habilitación para Administradores (Ejecutar en Supabase)

Copia y ejecuta el siguiente script en el **SQL Editor** de Supabase para otorgar permisos de eliminación y gestión en masa a los usuarios con rol `'admin'`:

```sql
-- ====================================================================
-- HABILITACIÓN DE PERMISOS RLS Y ESQUEMA PARA ADMINISTRADORES EN YÁYA
-- ====================================================================

-- 0. ADICIÓN DE COLUMNA IS_SUSPENDED EN PUBLIC.PROFILES
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS is_suspended BOOLEAN DEFAULT false;

-- 1. PERMITIR A LOS ADMINISTRADORES ELIMINAR Y ACTUALIZAR PERFILES EN PUBLIC.PROFILES
DROP POLICY IF EXISTS "Admins pueden eliminar perfiles" ON public.profiles;
CREATE POLICY "Admins pueden eliminar perfiles" ON public.profiles
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

DROP POLICY IF EXISTS "Admins pueden actualizar perfiles" ON public.profiles;
CREATE POLICY "Admins pueden actualizar perfiles" ON public.profiles
FOR UPDATE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin')
WITH CHECK ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 2. PERMITIR A LOS ADMINISTRADORES ELIMINAR SERVICIOS
DROP POLICY IF EXISTS "Admins pueden eliminar servicios" ON public.services;
CREATE POLICY "Admins pueden eliminar servicios" ON public.services
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 3. PERMITIR A LOS ADMINISTRADORES ELIMINAR DISPONIBILIDAD DE PRESTADORES
DROP POLICY IF EXISTS "Admins pueden eliminar disponibilidad" ON public.availability;
CREATE POLICY "Admins pueden eliminar disponibilidad" ON public.availability
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 4. PERMITIR A LOS ADMINISTRADORES ELIMINAR SOLICITUDES DE CONTRATACIÓN
DROP POLICY IF EXISTS "Admins pueden eliminar solicitudes" ON public.requests;
CREATE POLICY "Admins pueden eliminar solicitudes" ON public.requests
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 5. PERMITIR A LOS ADMINISTRADORES ELIMINAR MENSAJES DE CHAT
DROP POLICY IF EXISTS "Admins pueden eliminar mensajes" ON public.messages;
CREATE POLICY "Admins pueden eliminar mensajes" ON public.messages
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 6. PERMITIR A LOS ADMINISTRADORES ELIMINAR REPORTES / DENUNCIAS
DROP POLICY IF EXISTS "Admins pueden eliminar reportes" ON public.reports;
CREATE POLICY "Admins pueden eliminar reportes" ON public.reports
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 7. PERMITIR A LOS ADMINISTRADORES ELIMINAR CALIFICACIONES / RESEÑAS
DROP POLICY IF EXISTS "Admins pueden eliminar calificaciones" ON public.ratings;
CREATE POLICY "Admins pueden eliminar calificaciones" ON public.ratings
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin');

-- 8. PERMITIR A LOS ADMINISTRADORES ELIMINAR IMÁGENES DE SERVICIO
DROP POLICY IF EXISTS "Admins y dueños pueden eliminar imagenes de servicio" ON public.service_images;
CREATE POLICY "Admins y dueños pueden eliminar imagenes de servicio" ON public.service_images
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin' OR EXISTS (SELECT 1 FROM public.services WHERE id = service_id AND provider_id = auth.uid()));

-- ====================================================================
-- FUNCIÓN RPC ATÓMICA DE ELIMINACIÓN DE USUARIOS (SECURITY DEFINER)
-- ====================================================================
CREATE OR REPLACE FUNCTION admin_delete_user_account(target_user_id UUID)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
  -- 1. Eliminar calificaciones
  DELETE FROM public.ratings WHERE client_id = target_user_id OR provider_id = target_user_id;
  
  -- 2. Eliminar solicitudes de contratación
  DELETE FROM public.requests WHERE client_id = target_user_id OR service_id IN (SELECT id FROM public.services WHERE provider_id = target_user_id);
  
  -- 3. Eliminar mensajes de chat
  DELETE FROM public.messages WHERE sender_id = target_user_id OR receiver_id = target_user_id;
  
  -- 4. Eliminar reportes / denuncias
  DELETE FROM public.reports WHERE reporter_id = target_user_id OR reported_user_id = target_user_id;
  
  -- 5. Eliminar imágenes de servicios
  DELETE FROM public.service_images WHERE service_id IN (SELECT id FROM public.services WHERE provider_id = target_user_id);
  
  -- 6. Eliminar servicios del prestador
  DELETE FROM public.services WHERE provider_id = target_user_id;
  
  -- 7. Eliminar disponibilidad maestra
  DELETE FROM public.availability WHERE provider_id = target_user_id;
  
  -- 8. Eliminar perfil público de la cuenta
  DELETE FROM public.profiles WHERE id = target_user_id;
END;
$$;

-- 8. PERMITIR A LOS ADMINISTRADORES E IMÁGENES DE SERVICIO ELIMINAR
DROP POLICY IF EXISTS "Admins pueden eliminar imagenes de servicio" ON public.service_images;
CREATE POLICY "Admins pueden eliminar imagenes de servicio" ON public.service_images
FOR DELETE TO authenticated
USING ((SELECT role FROM public.profiles WHERE id = auth.uid()) = 'admin' OR EXISTS (SELECT 1 FROM public.services WHERE id = service_id AND provider_id = auth.uid()));

-- 9. VERIFICACIÓN DE ESTADO DE POLÍTICAS
SELECT 
    schemaname, 
    tablename, 
    policyname, 
    permissive, 
    roles, 
    cmd 
FROM pg_policies 
WHERE tablename IN ('profiles', 'services', 'availability', 'requests', 'messages', 'reports', 'ratings', 'service_images')
ORDER BY tablename, cmd;
```

---

## 4. 📌 Instrucciones de Ejecución Paso a Paso

1. Ingresa a la consola web de tu proyecto en Supabase: [https://supabase.com/dashboard](https://supabase.com/dashboard).
2. En el menú lateral izquierdo, haz clic en **SQL Editor** (`>_`).
3. Haz clic en **New query** (Nuevo Script).
4. Pega el bloque de código SQL de la Sección 3.
5. Haz clic en el botón verde **RUN** (o presiona `Ctrl + Enter`).
6. Verás la confirmación `Success. No rows returned` y la tabla de verificación mostrando las nuevas políticas activas.

---

*Documento técnico generado automáticamente y mantenido por el equipo de arquitectura de YÁYA.*
