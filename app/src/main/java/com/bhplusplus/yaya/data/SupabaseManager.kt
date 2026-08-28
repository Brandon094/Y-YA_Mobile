package com.bhplusplus.yaya.data

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.engine.okhttp.OkHttp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * GESTOR DE CONEXIÓN CON SUPABASE
 * Este objeto centraliza la configuración del cliente de Supabase para toda la app.
 * Ahora incluye soporte para PERSISTENCIA DE SESIÓN.
 */
object SupabaseManager {
    private const val SUPABASE_URL = "https://dxgjqkjippxbijailirv.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_cTduebDerIbIqNp5mOKFSQ_g31Q5TMA"

    private var _client: SupabaseClient? = null

    /**
     * Instancia del cliente de Supabase.
     * Debe ser inicializada llamando a `initialize(context)` en la MainActivity.
     */
    val client: SupabaseClient
        get() = _client ?: throw IllegalStateException("SupabaseManager no ha sido inicializado. Llama a initialize(context) en tu MainActivity.")

    /**
     * Inicializa el cliente de Supabase con persistencia de sesión.
     */
    fun initialize(context: Context) {
        if (_client != null) return

        // Configuramos el gestor de sesiones usando SharedPreferences para que persista al cerrar la app
        val sharedPreferences = context.getSharedPreferences("yaya_prefs", Context.MODE_PRIVATE)
        val settings = SharedPreferencesSettings(sharedPreferences)

        _client = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            httpEngine = OkHttp.create()

            install(Auth) {
                // Indicamos a Supabase que use Settings para guardar la sesión
                sessionManager = SettingsSessionManager(settings)
            }
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
    }

    /**
     * Recupera el token actual de FCM y lo sincroniza con el perfil en Supabase.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun syncFcmToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                val token = task.result
                val user = client.auth.currentUserOrNull()

                if (user != null && token != null) {
                    GlobalScope.launch {
                        try {
                            client.postgrest["profiles"].update({
                                set("fcm_token", token)
                            }) {
                                filter { eq("id", user.id) }
                            }
                        } catch (e: Exception) {
                            // Error silencioso en background
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Firebase no inicializado o error de SDK
        }
    }

    /**
     * Sube una imagen al bucket especificado y devuelve su URL pública.
     */
    suspend fun uploadImage(bucketName: String, fileName: String, byteArray: ByteArray): String {
        val bucket = client.storage.from(bucketName)
        bucket.upload(fileName, byteArray) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }
}
