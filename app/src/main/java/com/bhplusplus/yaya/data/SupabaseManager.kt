package com.bhplusplus.yaya.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp

/**
 * GESTOR DE CONEXIÓN CON SUPABASE
 * Este objeto centraliza la configuración del cliente de Supabase para toda la app.
 */
object SupabaseManager {
    // Credenciales del proyecto en Supabase (URL y Clave Pública)
    private const val SUPABASE_URL = "https://dxgjqkjippxbijailirv.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_cTduebDerIbIqNp5mOKFSQ_g31Q5TMA"

    /**
     * Instancia del cliente de Supabase.
     * Se configura con los módulos necesarios: Auth (Usuarios), Postgrest (Base de datos) y Realtime.
     */
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        // Usamos el motor OkHttp para asegurar compatibilidad y estabilidad en Android
        httpEngine = OkHttp.create()

        // Instalación de módulos
        install(Auth)      // Para manejo de usuarios, login y registro
        install(Postgrest) // Para realizar consultas a la base de datos (SELECT, INSERT, UPDATE)
        install(Realtime)  // Para recibir actualizaciones de la BD en tiempo real (si se requiere)
    }
}
