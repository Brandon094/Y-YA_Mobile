package com.bhplusplus.yaya.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bhplusplus.yaya.MainActivity
import com.bhplusplus.yaya.R
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

    companion object {
        private const val CHANNEL_ID = "yaya_notifications_channel"
        private const val CHANNEL_NAME = "Notificaciones de YÁYA"
    }

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
        
        message.notification?.let {
            Log.d("FCM", "Cuerpo de notificación: ${it.body}")
            showNotification(it.title ?: "YÁYA", it.body ?: "")
        }
    }

    /**
     * Muestra una notificación local.
     */
    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        
        // Crear canal de notificación (Requerido para Android 8.0+)
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.notification_color))
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
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
