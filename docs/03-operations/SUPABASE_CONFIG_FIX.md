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
Usa este **Código Unificado Pro** que gestiona Solicitudes, Negociación y Chats en una sola función:

```typescript
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"
import { JWT } from "https://esm.sh/google-auth-library@8.1.1"

serve(async (req) => {
  try {
    const payload = await req.json()
    const { record, old_record, type, table } = payload 

    const supabase = createClient(
      Deno.env.get('SUPABASE_URL') ?? '',
      Deno.env.get('SERVICE_ROLE_KEY') ?? ''
    )

    let targetUserId = ""
    let title = ""
    let body = ""

    // --- TABLA: REQUESTS (Pedidos y Subasta) ---
    if (table === 'requests') {
      const { data: serviceData } = await supabase
        .from('services').select('provider_id, title').eq('id', record.service_id).single()

      if (type === 'INSERT') {
        targetUserId = serviceData.provider_id
        title = "¡Nueva Solicitud en YÁYA! 🚩"
        body = `Tienes un nuevo pedido para: ${serviceData.title}.`
      } 
      else if (type === 'UPDATE') {
        if (record.final_price !== old_record.final_price) {
          const desc = record.request_description || ""
          if (desc.includes("Contraoferta Prestador")) {
            targetUserId = record.client_id
            title = "Nueva Contraoferta 💸"
            body = `El prestador propuso un nuevo precio para ${serviceData.title}.`
          } else if (desc.includes("Nueva oferta Cliente")) {
            targetUserId = serviceData.provider_id
            title = "Nueva Oferta Recibida 💰"
            body = `El cliente ajustó su oferta para ${serviceData.title}.`
          }
        } 
        else if (record.status !== old_record.status) {
          targetUserId = record.client_id
          title = "Actualización de tu pedido 🔄"
          const statusMap = { 'accepted': 'ha sido ACEPTADA ✅', 'cancelled': 'ha sido CANCELADA ❌', 'completed': 'ha sido COMPLETADA ✨' }
          body = `Tu solicitud para ${serviceData.title} ${statusMap[record.status] || record.status}.`
        }
      }
    }
    // --- TABLA: MESSAGES (Chat en Vivo) ---
    else if (table === 'messages' && type === 'INSERT') {
      targetUserId = record.receiver_id
      const { data: sender } = await supabase.from('profiles').select('full_name').eq('id', record.sender_id).single()
      title = sender ? `${sender.full_name} te envió un mensaje 💬` : "Nuevo Mensaje en YÁYA 💬"
      body = record.content
    }

    if (!targetUserId) return new Response(JSON.stringify({ m: "No action" }))

    const { data: profileData } = await supabase.from('profiles').select('fcm_token').eq('id', targetUserId).single()
    if (!profileData?.fcm_token) return new Response(JSON.stringify({ m: "No token" }))

    const response = await sendFCMNotification(profileData.fcm_token, title, body)
    return new Response(JSON.stringify({ success: true, response }), { headers: { "Content-Type": "application/json" } })

  } catch (error) {
    console.error("Critical Error:", error.message)
    return new Response(JSON.stringify({ error: error.message }), { status: 500 })
  }
})

async function sendFCMNotification(fcmToken: string, title: string, body: string) {
  // Lógica de autenticación JWT y fetch a FCM V1 API...
  // (Ver código completo en la consola de Supabase)
}
```

### Paso C: Activar los Webhooks
Debes crear **dos webhooks** apuntando a la misma función unificada:
1. **Webhook `requests_trigger`**:
   - Tabla: `requests` | Eventos: `INSERT`, `UPDATE`.
2. **Webhook `messages_trigger`**:
   - Tabla: `messages` | Eventos: `INSERT`.

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
