package com.example.poimapp.ui.poi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.poimapp.R
import com.example.poimapp.ui.poi.model.PoiDetailsItem
import com.example.poimapp.ui.poi.model.PoiDetailsState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoiDetails(
    modifier: Modifier,
    idList: List<String>,
) {
    val vm: PoiDetailsViewModel = koinViewModel(parameters = {
        parametersOf(idList)
    })
    val state: PoiDetailsState by vm.stateFlow.collectAsStateWithLifecycle()
    BackHandler(enabled = !state.backNavigationEnabled) {
        vm.cleanSelected()
    }

    Box(
        modifier = modifier
    ) {
        when (val currentState = state) {
            //TODO: Implement loading state
            PoiDetailsState.Loading -> Text(text = "Loading")
            is PoiDetailsState.Ready.Multiple ->
                if (currentState.selected == null) {
                    PoiItemList(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        currentState.listOfItems,
                        onItemClick = {
                            vm.itemSelected(it)
                        }
                    )
                } else {
                    PoiItemDetails(
                        Modifier.fillMaxSize(),
                        currentState.selected
                    )
                }

            is PoiDetailsState.Ready.Single -> PoiItemDetails(
                Modifier.fillMaxSize(),
                currentState.item
            )

            //TODO: Implement Error state
            is PoiDetailsState.Error -> Text(currentState.message)
        }
    }
}

@Composable
private fun PoiItemList(
    modifier: Modifier,
    items: List<PoiDetailsItem>,
    onItemClick: (PoiDetailsItem) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 25.dp)
    ) {
        items(items) {
            PoiItem(Modifier, it, onClick = { onItemClick(it) })
        }
    }
}

@Composable
private fun PoiItem(
    modifier: Modifier,
    item: PoiDetailsItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = item.image,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_image_placeholder_24),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(item.typeIcon),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "",
                )
            }


            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
            ) {
                Text(
                    text = "Provider:",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.provideName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PoiItemDetails(
    modifier: Modifier,
    item: PoiDetailsItem,
) {
    Column(
        modifier = modifier
    ) {

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(),
            model = item.image,
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(R.drawable.ic_image_placeholder_24),
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    maxLines = 1,
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
                Text(
                    text = "Provider:",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.provideName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}