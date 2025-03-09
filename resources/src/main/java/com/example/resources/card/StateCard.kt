package com.example.resources.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.resources.R

@Composable
fun UnknownErrorCard(
    modifier: Modifier,
    text: String = stringResource(R.string.unknown_error_msg),
) {
    StateCard(
        modifier,
        text = text,
        painter = painterResource(R.drawable.ic_paniking_pokemon)
    )
}

@Composable
fun NoInternetCard(
    modifier: Modifier
) {
    StateCard(
        modifier,
        text = stringResource(R.string.no_internet_msg),
        painter = painterResource(R.drawable.ic_tired_pokemon)
    )
}

@Composable
fun ClientErrorCard(
    modifier: Modifier
) {
    StateCard(
        modifier,
        text = stringResource(R.string.client_error_msg),
        painter = painterResource(R.drawable.ic_thinking_pokemon)
    )
}

@Composable
fun ServerErrorCard(
    modifier: Modifier
) {
    StateCard(
        modifier,
        text = stringResource(R.string.internal_server_error_msg),
        painter = painterResource(R.drawable.ic_sleeping_pokemon)
    )
}

@Composable
fun StateCard(
    modifier: Modifier,
    text: String,
    painter: Painter,
    tint: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier,
            painter = painter,
            contentDescription = null,
            tint = tint
        )
        Text(
            text = text,
            color = tint,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}