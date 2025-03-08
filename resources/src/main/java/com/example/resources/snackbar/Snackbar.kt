package com.example.resources.snackbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.resources.R


@Composable
fun NoInternetSnackbar() {
    CustomSnackbar(
        drawableRes = R.drawable.ic_tired_pokemon,
        message = stringResource(R.string.no_internet_msg),
        containerColor = MaterialTheme.colorScheme.error,
        iconTint = MaterialTheme.colorScheme.onError,
        textColor = MaterialTheme.colorScheme.onError,
    )
}


@Composable
fun ClientErrorSnackbar() {
    CustomSnackbar(
        drawableRes = R.drawable.ic_thinking_pokemon,
        message = stringResource(R.string.client_error_msg),
        containerColor = MaterialTheme.colorScheme.error,
        iconTint = MaterialTheme.colorScheme.onError,
        textColor = MaterialTheme.colorScheme.onError,
    )
}

@Composable
fun ServerErrorSnackbar() {
    CustomSnackbar(
        drawableRes = R.drawable.ic_sleeping_pokemon,
        message = stringResource(R.string.internal_server_error_msg),
        containerColor = MaterialTheme.colorScheme.error,
        iconTint = MaterialTheme.colorScheme.onError,
        textColor = MaterialTheme.colorScheme.onError,
    )
}

@Composable
fun UnknownErrorSnackbar() {
    CustomSnackbar(
        drawableRes = R.drawable.ic_paniking_pokemon,
        message = stringResource(R.string.unknown_error_msg),
        containerColor = MaterialTheme.colorScheme.error,
        iconTint = MaterialTheme.colorScheme.onError,
        textColor = MaterialTheme.colorScheme.onError,
    )
}

@Composable
fun CustomSnackbar(
    @DrawableRes drawableRes: Int,
    message: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            painter = painterResource(id = drawableRes),
            tint = iconTint,
            contentDescription = null
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = message,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}