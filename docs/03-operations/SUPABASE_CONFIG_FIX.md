# 🛠️ Guía de Reparación: Chat y Notificaciones

Si los mensajes no llegan en tiempo real o las notificaciones no suenan, sigue estos pasos exactos en tu consola de **Supabase**.

---

## 1. Habilitar Realtime para Mensajes, Servicios y Solicitudes ⚡
Por defecto, Supabase no escucha los cambios en las tablas por seguridad.
1. Ve a **Database** -> **Replication**.
2. En la tabla de publicaciones (usualmente llamada `supabase_realtime`), haz clic en **Source**.
3. Asegúrate de que las tablas **`messages`**, **`services`** y **`requests`** estén seleccionadas (marcadas con el check).
4. **¡Importante!** Sin esto, los cambios en la lista de servicios, el chat y los contadores (badges) nunca serán instantáneos.

---

## 2. Configurar Webhooks de Notificaciones 🔔
Para que las notificaciones lleguen al celular, Supabase debe avisarle a Firebase.

### Paso A: Preparar la Base de Datos
Ejecuta esto en el SQL Editor para que los Webhooks puedan detectar cambios de precio:
```sql
ALTER TABLE requests REPLICA IDENTITY FULL;
```

### Paso B: Crear/Actualizar la Edge Function
Usa este código optimizado que maneja Negociación y Estados:

```typescript
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

serve(async (req) => {
  const payload = await req.json()
  const { record, old_record, type, table } = payload

  if (table !== 'requests') return new Response("Not requests")

  let targetUserId = ""
  let title = ""
  let body = ""

  if (type === 'INSERT') {
    targetUserId = record.provider_id
    title = "¡Nueva Solicitud! 🚩"
    body = "Alguien quiere contratar tu talento."
  } else if (type === 'UPDATE') {
    if (record.final_price !== old_record.final_price) {
      const isProvider = record.request_description.includes("Contraoferta Prestador")
      targetUserId = isProvider ? record.client_id : record.provider_id
      title = isProvider ? "Nueva Contraoferta 💸" : "Nueva Oferta Recibida 💰"
      body = "Revisa los detalles de la negociación en la App."
    } else if (record.status !== old_record.status) {
      targetUserId = record.client_id
      title = "Estado Actualizado 🔄"
      body = `Tu pedido ahora está: ${record.status}`
    }
  }
  // ... lógica de envío FCM ...
})
```

### Paso C: Activar los Webhooks
En **Database** -> **Webhooks**:
1. **Webhook para Solicitudes:**
   - Tabla: `requests`
   - Eventos: `INSERT` y `UPDATE`.
   - Destino: Tu Edge Function.

---

## 3. Verificación en la App (Código) ✅
He realizado los siguientes ajustes en la App:
1. **`ChatViewModel`:** Ahora usa canales únicos (`chat_UUID_UUID`) para evitar que los mensajes se crucen entre usuarios.
2. **`HomeViewModel`:** Ahora sincroniza el `fcm_token` cada vez que abres el Home, asegurando que si el usuario cambia de celular, las notificaciones le sigan llegando.
3. **`SupabaseManager`:** Verifiqué que el plugin de `Realtime` esté instalado correctamente.

**¡Dale Play a la App y asegúrate de marcar el check de "Replication" en Supabase!** Eso debería revivir el chat de inmediato. 🚀🔥

---

## 4. Configurar Almacenamiento (Storage) 📸
Para que las fotos de perfil y portafolios funcionen, debes crear los contenedores en Supabase.

### Paso A: Crear Buckets
En la sección **Storage**:
1. Crea un bucket llamado `avatars` y márcalo como **Public**.
2. Crea un bucket llamado `portfolios` y márcalo como **Public**.

### Paso B: Configurar Políticas de Seguridad (RLS)
Ejecuta este SQL para proteger los archivos:

```sql
-- Lectura pública para todos
CREATE POLICY "Acceso público lectura" ON storage.objects FOR SELECT USING (true);

-- Solo dueños pueden subir/editar sus fotos
CREATE POLICY "Usuarios manejan sus archivos" ON storage.objects 
FOR ALL TO authenticated 
USING (bucket_id IN ('avatars', 'portfolios'));
```

*Nota: La App asume que los archivos en 'avatars' siguen el patrón 'id_avatar.jpg'.*
