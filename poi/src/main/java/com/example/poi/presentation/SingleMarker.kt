package com.example.poi.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SingeMarker(
    @DrawableRes image: Int,
) {
    val strokeColor = MaterialTheme.colorScheme.primary
    Icon(
        tint = MaterialTheme.colorScheme.onPrimary,
        painter = painterResource(id = image),
        contentDescription = "",
        modifier = Modifier
            .size(36.dp)
            .padding(1.dp)
            .drawBehind {
                drawCircle(color = strokeColor)
            }
            .padding(4.dp)
    )
}