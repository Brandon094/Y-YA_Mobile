package com.bhplusplus.yaya.utils.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface para observar el estado de la conectividad de red.
 */
interface ConnectivityObserver {

    /**
     * Emite el estado actual y futuros cambios en la conexión.
     */
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
