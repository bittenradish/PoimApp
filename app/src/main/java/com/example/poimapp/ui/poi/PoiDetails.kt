package com.example.poimapp.ui.poi

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.poimapp.R
import com.example.poimapp.ui.poi.model.PoiDetailsItem
import com.example.poimapp.ui.poi.model.PoiDetailsState
import com.example.resources.card.ClientErrorCard
import com.example.resources.card.NoInternetCard
import com.example.resources.card.ServerErrorCard
import com.example.resources.card.UnknownErrorCard
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
    val screenOrientation = LocalConfiguration.current.orientation

    Box(
        modifier = modifier
    ) {
        when (val currentState = state) {
            PoiDetailsState.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(55.dp)
                        .align(Alignment.Center)
                        .displayCutoutPadding()
                        .navigationBarsPadding()
                        .statusBarsPadding(),
                    strokeWidth = 6.dp
                )

            is PoiDetailsState.Ready.Multiple ->
                if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    PortraitModeItemList(
                        currentState,
                        onItemClick = { vm.itemSelected(it) }
                    )
                } else {
                    LandscapeModeItemList(
                        currentState,
                        onItemClick = { vm.itemSelected(it) }
                    )
                }

            is PoiDetailsState.Ready.Single -> PoiItemDetails(
                modifier = Modifier
                    .fillMaxSize()
                    .displayCutoutPadding(),
                item = currentState.item,
                orientation = screenOrientation,
            )

            is PoiDetailsState.ErrorState -> ErrorStateComposable(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(25.dp),
                errorState = currentState
            )
        }
    }
}

@Composable
private fun ErrorStateComposable(
    modifier: Modifier,
    errorState: PoiDetailsState.ErrorState
) {
    when (errorState) {
        PoiDetailsState.ErrorState.Client -> ClientErrorCard(modifier)
        is PoiDetailsState.ErrorState.MarkerNotFound -> UnknownErrorCard(
            modifier,
            errorState.message
        )

        PoiDetailsState.ErrorState.NoConnection -> NoInternetCard(modifier)
        PoiDetailsState.ErrorState.Server -> ServerErrorCard(modifier)
        PoiDetailsState.ErrorState.Unknown -> UnknownErrorCard(modifier)
    }
}

@Composable
private fun LandscapeModeItemList(
    currentState: PoiDetailsState.Ready.Multiple,
    onItemClick: (PoiDetailsItem) -> Unit
) {
    val halfOfTheScreen = (LocalConfiguration.current.screenWidthDp / 2).dp
    Row(
        modifier = Modifier.displayCutoutPadding()
    ) {
        PoiItemList(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .sizeIn(
                    maxWidth = halfOfTheScreen,
                    minWidth = halfOfTheScreen
                )
                .padding(horizontal = 8.dp),
            currentState.listOfItems,
            onItemClick = onItemClick
        )
        AnimatedVisibility(
            currentState.selected != null,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            VerticalDivider(thickness = 1.dp)
            currentState.selected?.let {
                PoiItemDetails(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .sizeIn(
                            minWidth = halfOfTheScreen,
                            maxWidth = halfOfTheScreen,
                        ),
                    item = it,
                    orientation = Configuration.ORIENTATION_LANDSCAPE
                )
            }
        }
    }
}

@Composable
private fun PortraitModeItemList(
    currentState: PoiDetailsState.Ready.Multiple,
    onItemClick: (PoiDetailsItem) -> Unit,
) {
    if (currentState.selected == null) {
        PoiItemList(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            currentState.listOfItems,
            onItemClick = onItemClick
        )
    } else {
        PoiItemDetails(
            modifier = Modifier.fillMaxSize(),
            item = currentState.selected,
            orientation = Configuration.ORIENTATION_PORTRAIT,
        )
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
    orientation: Int,
) {
    Column(
        modifier = modifier
    ) {
        PoiItemDetailsImage(
            modifier = Modifier.let {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    it.padding(8.dp)
                } else {
                    it
                }
            },
            item = item,
            orientation = orientation
        )
        PoiItemDetailsDescription(modifier = Modifier.weight(1f), item = item)
    }
}

@Composable
private fun PoiItemDetailsImage(
    modifier: Modifier,
    item: PoiDetailsItem,
    orientation: Int
) {
    AsyncImage(
        modifier = modifier
            .let {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    it.height((LocalConfiguration.current.screenHeightDp / 2).dp)
                } else {
                    it.fillMaxWidth()
                }
            },
        model = item.image,
        contentScale = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ContentScale.FillHeight
        } else {
            ContentScale.FillWidth
        },
        placeholder = painterResource(R.drawable.ic_image_placeholder_24),
        contentDescription = null
    )
}

@Composable
private fun PoiItemDetailsDescription(
    modifier: Modifier,
    item: PoiDetailsItem,
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            modifier = Modifier,
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