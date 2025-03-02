package com.example.poi.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.poi.presentation.model.PoiMarker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun PoiMap(
    modifier: Modifier,
    poiList: List<PoiMarker>,
    isLoading: Boolean,
    cameraPositionChanged: (LatLngBounds) -> Unit
) {
    val germany = LatLng(50.775555, 6.083611)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(germany, 15f)
    }

    LaunchedEffect(cameraPositionState.isMoving) { // Trigger when cameraPositionState changes
        Log.d("map", "called launchedEffect")
        if (!cameraPositionState.isMoving) {
            snapshotFlow { cameraPositionState.projection?.visibleRegion }
                .distinctUntilChanged() // Only collect distinct changes
                .collect { visibleRegion ->
                    visibleRegion?.let {

                        cameraPositionChanged(it.latLngBounds)
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
//            poiList.forEach {
//                Marker(
//                    state = rememberUpdatedMarkerState(it)
//                )
//            }

            Clustering(
                items = poiList,
                clusterItemContent = {
                    SingeMarker(it.markerImage)
                }
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
    }
}