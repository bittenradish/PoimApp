package com.example.poimapp.ui.poi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.poimapp.R
import com.example.poimapp.ui.poi.model.PoiDetailsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoiDetails(
    modifier: Modifier,
    item: PoiDetailsState.PoiDetailsItem,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier.wrapContentHeight(),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 25.dp)
        ) {
            item.image?.let {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    model = item.image,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null
                )
            } ?: run {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(R.drawable.ic_image_placeholder_24),
                    contentDescription = "image placeholder"
                )
            }
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.name,
                        style = MaterialTheme.typography.headlineMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        modifier = Modifier.size(44.dp),
                        painter = painterResource(item.typeIcon),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "",
                    )
                }


                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
                ) {
                    Text(text = "Provider:", style = MaterialTheme.typography.titleMedium)
                    Text(text = item.provideName, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

    }
}