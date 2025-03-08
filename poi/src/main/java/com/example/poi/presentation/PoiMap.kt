package com.example.poi.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.poi.presentation.model.PoiMarker
import com.example.resources.snackbar.ErrorSnackbarMapper
import com.example.resources.snackbar.SnackbarType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel


@Composable
fun PoiMap(
    modifier: Modifier,
    vm: PoiMapViewModel = koinViewModel<PoiMapViewModel>(),
    onItemsClicked: (List<PoiMarker>) -> Unit
) {
    val poiList by vm.mapStateFlow.collectAsStateWithLifecycle()
    val isLoading by vm.isLoadingFlow.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            vm.uiEffectFlow.collect { uiEffect ->
                snackState.showSnackbar(message = Json.encodeToString(uiEffect))
            }
        }
    }


    val aachen = LatLng(50.775555, 6.083611)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(aachen, 15f)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        Log.d("map", "called launchedEffect")
        if (!cameraPositionState.isMoving) {
            snapshotFlow { cameraPositionState.projection?.visibleRegion }
                .distinctUntilChanged()
                .collect { visibleRegion ->
                    visibleRegion?.let {
                        vm.updateBoundingBox(it.latLngBounds)
                    }
                }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                Log.d("Map", "Loaded")
            }
        ) {
            Clustering(
                items = poiList,
                onClusterClick = {
                    if (cameraPositionState.position.zoom <= 17) {
                        cameraPositionState.move(update = CameraUpdateFactory.zoomIn())
                    } else {
                        onItemsClicked(it.items.toList())
                    }
                    false
                },
                onClusterItemClick = {
                    onItemsClicked(listOf(it))
                    false
                },
                clusterItemContent = {
                    SingeMarker(it.markerImage)
                },
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = isLoading,
            exit = fadeOut(),
            enter = fadeIn(),
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .align(Alignment.BottomCenter)
            )
        }
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
                .statusBarsPadding(),
            hostState = snackState
        ) { it ->
            ErrorSnackbarMapper(Json.decodeFromString<SnackbarType>(it.visuals.message))
        }
    }
}