package com.example.core

import java.io.IOException

sealed class NetworkExceptions : IOException() {
    data class ClientErrorException(
        val code: Int,
        override val message: String = "Client Error: $code",
    ) : NetworkExceptions()

    data class ServerErrorException(
        val code: Int,
        override val message: String = "Server Error: $code"
    ) : NetworkExceptions()

    data class UnknownError(
        override val message: String = "Unknown error"
    ) : NetworkExceptions()

    data class NoConnectivityException(
        override val message: String = "No internet connection"
    ) : NetworkExceptions()
}