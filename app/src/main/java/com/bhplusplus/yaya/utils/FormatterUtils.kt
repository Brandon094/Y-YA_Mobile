package com.bhplusplus.yaya.utils

import java.util.Locale

/**
 * UTILIDADES DE FORMATEO (Principio DRY)
 * Centraliza el manejo de moneda, fechas y horas para toda la aplicación.
 */
object FormatterUtils {

    private val colombianLocale = Locale.forLanguageTag("es-CO")

    /**
     * Formatea un valor numérico a moneda colombiana compacta (ej: $ 50k).
     * Ideal para interfaces móviles con espacio reducido y un look más profesional.
     */
    fun formatCurrency(amount: Double?): String {
        if (amount == null) return "$ 0"
        return when {
            amount >= 1_000_000 -> {
                val millions = amount / 1_000_000
                if (millions % 1 == 0.0) "$ ${millions.toInt()}M" 
                else "$ ${String.format(colombianLocale, "%.1f", millions)}M"
            }
            amount >= 1_000 -> {
                val thousands = amount / 1_000
                if (thousands % 1 == 0.0) "$ ${thousands.toInt()}k" 
                else "$ ${String.format(colombianLocale, "%.1f", thousands)}k"
            }
            else -> "$ ${amount.toInt()}"
        }
    }

    /**
     * Extrae únicamente la fecha (YYYY-MM-DD) de un string ISO de Supabase.
     */
    fun formatDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "Fecha no definida"
        return isoString.take(10)
    }

    /**
     * Extrae únicamente la hora (HH:mm) de un string ISO o formato de tiempo.
     */
    fun formatTime(timeString: String?): String {
        if (timeString.isNullOrBlank()) return "00:00"
        return if (timeString.contains("T")) {
            timeString.substringAfter("T").take(5)
        } else {
            timeString.take(5)
        }
    }

    /**
     * Entrega fecha y hora juntas (YYYY-MM-DD HH:mm).
     */
    fun formatDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "Sin fecha"
        val date = formatDate(isoString)
        val time = formatTime(isoString)
        return "$date $time"
    }
}
