package com.bhplusplus.yaya.data

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp

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
        }
    }
}
