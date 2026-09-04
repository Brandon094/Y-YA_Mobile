package com.bhplusplus.yaya.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * GESTOR DE PERSISTENCIA DE TUTORIALES (DRY)
 * Controla qué tutoriales in-app ha visto el usuario para garantizar
 * que cada recorrido de aprendizaje se muestre UNA SOLA VEZ (ShowOnce).
 */
object TutorialManager {

    private const val PREFS_NAME = "yaya_tutorials_prefs"

    // Claves estandarizadas para cada sección
    const val TUTORIAL_HOME_MUNICIPIO = "tutorial_home_municipio_v1"
    const val TUTORIAL_CREATE_SERVICE_SCHEDULE = "tutorial_create_service_schedule_v1"
    const val TUTORIAL_CONTRATACION_HANDSHAKE = "tutorial_contratacion_handshake_v1"
    const val TUTORIAL_PROFILE_REPUTATION = "tutorial_profile_reputation_v1"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Verifica si el usuario ya vio un tutorial específico.
     */
    fun hasSeenTutorial(context: Context, key: String): Boolean {
        return getPrefs(context).getBoolean(key, false)
    }

    /**
     * Marca un tutorial como visto para no volverlo a mostrar.
     */
    fun markTutorialAsSeen(context: Context, key: String) {
        getPrefs(context).edit().putBoolean(key, true).apply()
    }

    /**
     * Reinicia todos los tutoriales (útil para pruebas o reseteo en configuración).
     */
    fun resetAllTutorials(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
