package com.bhplusplus.yaya.utils

import android.content.Context
import android.net.Uri

object ImageUtils {
    /**
     * Convierte una Uri de imagen en un ByteArray para subir a Supabase.
     */
    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            null
        }
    }
}
