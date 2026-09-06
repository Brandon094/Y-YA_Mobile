package com.bhplusplus.yaya.utils

import android.util.Patterns

/**
 * MOTOR DE VALIDACIÓN CENTRALIZADO (DRY)
 * Contiene reglas de validación reusables para datos de usuario en toda la plataforma YÁYA.
 * Sigue las reglas de negocio para garantizar la calidad e integridad de los datos ingresados.
 */
object ValidationUtils {

    /**
     * Lista estandarizada de municipios de cobertura (Huila / Colombia)
     * para evitar errores de tipeo en formularios de la aplicación.
     */
    val HUILA_MUNICIPALITIES = listOf(
        "La Plata",
        "Nátaga",
        "Paicol",
        "Tesalia",
        "Garzón",
        "Gigante",
        "Neiva",
        "Pitalito",
        "Apepe",
        "Agrado",
        "Inzá",
        "Páez (Belalcázar)",
        "San Agustín",
        "Silvia"
    )

    /**
     * Valida nombres y apellidos.
     * Debe contener solo letras (incluyendo acentos, ñ, ü), espacios y guiones/apóstrofes.
     * Mínimo 2 caracteres. Sin números ni símbolos especiales.
     */
    fun isValidName(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.length < 2) return false
        val nameRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$".toRegex()
        return trimmed.matches(nameRegex)
    }

    /**
     * Valida el número de identificación (Documento DNI / CC).
     * Debe contener únicamente dígitos numéricos (entre 6 y 12 caracteres).
     */
    fun isValidDocumentId(documentId: String): Boolean {
        val trimmed = documentId.trim()
        val docRegex = "^\\d{6,12}$".toRegex()
        return trimmed.matches(docRegex)
    }

    /**
     * Valida el número telefónico.
     * Debe contener SOLO números y ser EXACTAMENTE de 10 dígitos (estándar móvil).
     */
    fun isValidPhone(phone: String): Boolean {
        val trimmed = phone.trim()
        val phoneRegex = "^\\d{10}$".toRegex()
        return trimmed.matches(phoneRegex)
    }

    /**
     * Valida la dirección de correo electrónico según patrones estándar.
     */
    fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return false
        return Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    }

    /**
     * Valida la fortaleza de la contraseña.
     * Reglas de seguridad:
     * - Mínimo 8 caracteres de longitud.
     * - Al menos una letra mayúscula.
     * - Al menos una letra minúscula.
     * - Al menos un número o carácter especial.
     */
    fun isSecurePassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigitOrSpecial = password.any { it.isDigit() || !it.isLetterOrDigit() }
        return hasUppercase && hasLowercase && hasDigitOrSpecial
    }

    /**
     * Valida la fecha de nacimiento.
     * La persona debe tener al menos 15 años cumplidos de edad y no superar los 120 años.
     */
    fun isValidBirthDate(birthDate: String): Boolean {
        if (birthDate.isBlank()) return false
        return try {
            val date = java.time.LocalDate.parse(birthDate.trim())
            val today = java.time.LocalDate.now()
            val minAgeDate = today.minusYears(15)
            !date.isAfter(minAgeDate) && date.isAfter(today.minusYears(120))
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Valida una dirección de atención.
     * Debe tener al menos 5 caracteres.
     */
    fun isValidAddress(address: String): Boolean {
        return address.trim().length >= 5
    }

    /**
     * Compara dos rangos de horas ("HH:mm" o "HH:mm:ss") para verificar si existe traslape.
     * Retorna true si hay colisión horaria.
     */
    fun isTimeRangeOverlapping(
        startA: String, endA: String,
        startB: String, endB: String
    ): Boolean {
        return try {
            val parseTime = { t: String ->
                val shortStr = if (t.length >= 5) t.substring(0, 5) else t
                java.time.LocalTime.parse(shortStr)
            }
            val sA = parseTime(startA)
            val eA = parseTime(endA)
            val sB = parseTime(startB)
            val eB = parseTime(endB)

            sA < eB && eA > sB
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Valida que una fecha programada sea hoy o futura (no del pasado).
     */
    fun isValidFutureDate(dateString: String): Boolean {
        if (dateString.isBlank()) return false
        return try {
            val selectedDate = java.time.LocalDate.parse(dateString.trim())
            val today = java.time.LocalDate.now()
            !selectedDate.isBefore(today)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Valida la hora programada.
     * Si la fecha seleccionada es hoy, la hora debe ser posterior a la hora actual.
     */
    fun isValidScheduleTime(dateString: String, timeString: String): Boolean {
        if (dateString.isBlank() || timeString.isBlank()) return false
        return try {
            val selectedDate = java.time.LocalDate.parse(dateString.trim())
            val selectedTime = java.time.LocalTime.parse(timeString.trim())
            val today = java.time.LocalDate.now()
            val nowTime = java.time.LocalTime.now()

            if (selectedDate.isEqual(today)) {
                selectedTime.isAfter(nowTime)
            } else {
                selectedDate.isAfter(today)
            }
        } catch (_: Exception) {
            false
        }
    }

    // --- MÉTODOS DE MENSAJE DE ERROR PARA RETORNO DIRECTO ---

    fun getNameError(name: String): String? {
        if (name.isBlank()) return "El nombre es obligatorio"
        if (!isValidName(name)) return "Ingresa nombres válidos sin números ni símbolos"
        return null
    }

    fun getDocumentIdError(documentId: String): String? {
        if (documentId.isBlank()) return "El documento es obligatorio"
        if (!isValidDocumentId(documentId)) return "El documento debe tener entre 6 y 12 dígitos numéricos"
        return null
    }

    fun getPhoneError(phone: String): String? {
        if (phone.isBlank()) return "El teléfono es obligatorio"
        if (!isValidPhone(phone)) return "El teléfono debe contener exactamente 10 números"
        return null
    }

    fun getEmailError(email: String): String? {
        if (email.isBlank()) return "El correo es obligatorio"
        if (!isValidEmail(email)) return "Ingresa un correo electrónico válido (ej. usuario@dominio.com)"
        return null
    }

    fun getPasswordError(password: String): String? {
        if (password.isBlank()) return "La contraseña es obligatoria"
        if (!isSecurePassword(password)) return "La contraseña debe tener mín. 8 caracteres, con mayúscula, minúscula y número/símbolo"
        return null
    }

    fun getBirthDateError(birthDate: String): String? {
        if (birthDate.isBlank()) return "La fecha de nacimiento es obligatoria"
        if (!isValidBirthDate(birthDate)) return "Debes tener al menos 15 años de edad para registrarte en YÁYA"
        return null
    }

    fun getAddressError(address: String): String? {
        if (address.isBlank()) return "La dirección de atención es obligatoria"
        if (!isValidAddress(address)) return "Ingresa una dirección válida (mínimo 5 caracteres)"
        return null
    }

    fun getFutureDateError(dateString: String): String? {
        if (dateString.isBlank()) return "La fecha de la cita es obligatoria"
        if (!isValidFutureDate(dateString)) return "La fecha debe ser hoy o futura"
        return null
    }

    fun getScheduleTimeError(dateString: String, timeString: String): String? {
        if (timeString.isBlank()) return "La hora es obligatoria"
        if (!isValidScheduleTime(dateString, timeString)) return "La hora seleccionada ya transcurrió el día de hoy"
        return null
    }
}
