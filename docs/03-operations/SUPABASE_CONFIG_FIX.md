# 🛠️ Guía de Reparación: Chat y Notificaciones

Si los mensajes no llegan en tiempo real o las notificaciones no suenan, sigue estos pasos exactos en tu consola de **Supabase**.

---

## 1. Habilitar Realtime para Mensajes ⚡
Por defecto, Supabase no escucha los cambios en las tablas por seguridad.
1. Ve a **Database** -> **Replication**.
2. En la tabla de publicaciones (usualmente llamada `supabase_realtime`), haz clic en **Source**.
3. Asegúrate de que la tabla `messages` esté seleccionada (marcada con el check).
4. **¡Importante!** Sin esto, el chat nunca será instantáneo.

---

## 2. Configurar Webhooks de Notificaciones 🔔
Para que las notificaciones lleguen al celular, Supabase debe avisarle a Firebase.

### Paso A: Crear la Edge Function
Crea una función llamada `push-notifications` en Supabase con este código (Deno/TypeScript):

```typescript
import { serve } from "https://deno.land/std@0.168.0/http/server.ts"

serve(async (req) => {
  const payload = await req.json()
  const { record, table, type } = payload

  let targetUserId = ""
  let title = ""
  let body = ""

  if (table === 'messages' && type === 'INSERT') {
    targetUserId = record.receiver_id
    title = "Nuevo mensaje en YÁYA"
    body = record.content
  } else if (table === 'requests' && type === 'INSERT') {
    targetUserId = record.provider_id // Para el prestador
    title = "¡Nueva Solicitud de Servicio!"
    body = "Alguien quiere contratar tu talento."
  }

  // 1. Obtener el FCM Token del usuario destino desde la tabla 'profiles'
  // Aquí deberías hacer un fetch a tu API de Supabase para traer el fcm_token del targetUserId

  // 2. Enviar a Firebase (Requiere SERVICE_ACCOUNT_JSON en los secrets de Supabase)
  // ... lógica de envío via Firebase V1 API ...

  return new Response(JSON.stringify({ status: "ok" }), { headers: { "Content-Type": "application/json" } })
})
```

### Paso B: Activar los Webhooks
En **Database** -> **Webhooks**:
1. **Webhook para Mensajes:**
   - Tabla: `messages`
   - Eventos: `INSERT`
   - Destino: Tu Edge Function `push-notifications`.
2. **Webhook para Solicitudes:**
   - Tabla: `requests`
   - Eventos: `INSERT`
   - Destino: Tu Edge Function `push-notifications`.

---

## 3. Verificación en la App (Código) ✅
He realizado los siguientes ajustes en la App:
1. **`ChatViewModel`:** Ahora usa canales únicos (`chat_UUID_UUID`) para evitar que los mensajes se crucen entre usuarios.
2. **`HomeViewModel`:** Ahora sincroniza el `fcm_token` cada vez que abres el Home, asegurando que si el usuario cambia de celular, las notificaciones le sigan llegando.
3. **`SupabaseManager`:** Verifiqué que el plugin de `Realtime` esté instalado correctamente.

**¡Dale Play a la App y asegúrate de marcar el check de "Replication" en Supabase!** Eso debería revivir el chat de inmediato. 🚀🔥
