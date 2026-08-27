package com.bhplusplus.yaya.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * UTILIDADES DE FORMATEO (Principio DRY)
 * Centraliza el manejo de moneda, fechas y horas para toda la aplicación.
 */
object FormatterUtils {

    private val colombianLocale = Locale.forLanguageTag("es-CO")
    private val currencyFormatter = NumberFormat.getCurrencyInstance(colombianLocale)

    /**
     * Formatea un valor numérico a moneda colombiana (ej: $ 50.000).
     * Elimina los decimales innecesarios para una UI más limpia.
     */
    fun formatCurrency(amount: Double?): String {
        if (amount == null) return "$ 0"
        return currencyFormatter.format(amount).replace(",00", "")
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
