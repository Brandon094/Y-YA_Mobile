package com.example.myapplication.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseManager {
    private const val SUPABASE_URL = "https://dxgjqkjippxbijailirv.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_cTduebDerIbIqNp5mOKFSQ_g31Q5TMA"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        // Forzamos el uso del motor OkHttp para evitar crashes en Android
        httpEngine = OkHttp.create()

        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
