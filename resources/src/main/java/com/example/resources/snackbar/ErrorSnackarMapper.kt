package com.example.resources.snackbar

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
enum class SnackbarType {
    NO_INTERNET_ERROR,
    CLIENT_ERROR,
    SERVER_ERROR,
    UNKNOWN_ERROR;
}

@Composable
fun ErrorSnackbarMapper(
    snackbarType: SnackbarType
) {

    when (snackbarType) {
        SnackbarType.NO_INTERNET_ERROR -> NoInternetSnackbar()
        SnackbarType.CLIENT_ERROR -> ClientErrorSnackbar()
        SnackbarType.SERVER_ERROR -> ServerErrorSnackbar()
        else -> UnknownErrorSnackbar()
    }
}