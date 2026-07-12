package com.bhplusplus.yaya.services

import android.util.Log
import com.bhplusplus.yaya.data.SupabaseManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * SERVICIO DE MENSAJERÍA YÁYA (FCM)
 * Gestiona la recepción de notificaciones push y la actualización de tokens de registro.
 */
class YayaMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Se llama cuando se genera un nuevo token (instalación nueva, limpieza de datos, etc).
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token generado: $token")
        saveTokenToSupabase(token)
    }

    /**
     * Se llama cuando llega un mensaje mientras la app está en primer plano o contiene datos.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "Mensaje recibido de: ${message.from}")
        
        // Aquí podríamos mostrar una notificación personalizada si la app está abierta
        message.notification?.let {
            Log.d("FCM", "Cuerpo de notificación: ${it.body}")
        }
    }

    /**
     * Vincula el token del dispositivo con el perfil del usuario en Supabase.
     */
    private fun saveTokenToSupabase(token: String) {
        serviceScope.launch {
            try {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                if (user != null) {
                    SupabaseManager.client.postgrest["profiles"].update({
                        set("fcm_token", token)
                    }) {
                        filter { eq("id", user.id) }
                    }
                    Log.d("FCM", "Token sincronizado con Supabase.")
                } else {
                    Log.d("FCM", "No hay usuario activo para sincronizar el token.")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error al guardar token en Supabase: ${e.message}")
            }
        }
    }
}
