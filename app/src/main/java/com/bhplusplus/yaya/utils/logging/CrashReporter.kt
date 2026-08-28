package com.bhplusplus.yaya.utils.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * UTILERÍA CENTRALIZADA PARA LOGS Y REPORTES DE ERRORES
 * Permite manejar logs locales y reportes remotos a Crashlytics en un solo lugar.
 */
object CrashReporter {

    private const val TAG = "YAYA_LOGGER"

    /**
     * Reporta una excepción capturada (No crítica) a Crashlytics.
     */
    fun logException(e: Throwable, message: String? = null) {
        // Log local
        Log.e(TAG, message ?: "Captured Exception", e)
        
        // Reporte remoto
        message?.let { FirebaseCrashlytics.getInstance().log(it) }
        FirebaseCrashlytics.getInstance().recordException(e)
    }

    /**
     * Añade información de contexto personalizada al próximo reporte de error.
     */
    fun setCustomKey(key: String, value: String) {
        FirebaseCrashlytics.getInstance().setCustomKey(key, value)
    }

    /**
     * Registra un evento informativo en el log de Crashlytics.
     */
    fun log(message: String) {
        Log.d(TAG, message)
        FirebaseCrashlytics.getInstance().log(message)
    }
}
