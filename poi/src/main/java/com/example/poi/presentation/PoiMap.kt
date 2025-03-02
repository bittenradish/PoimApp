package com.example.poi.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.example.poi.presentation.model.TempClusterLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun PoiMap(poiList: List<LatLng>, cameraPositionChanged: (LatLngBounds) -> Unit) {
    Box {
        val germany = LatLng(50.775555, 6.083611)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(germany, 15f)
        }
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
                items = poiList.map { TempClusterLocation(it) }
            )
        }
        LaunchedEffect(cameraPositionState.isMoving) { // Trigger when cameraPositionState changes
            Log.d("map", "called launchedEffect")
            if (cameraPositionState.isMoving) {
                snapshotFlow { cameraPositionState.projection?.visibleRegion }
                    .distinctUntilChanged() // Only collect distinct changes
                    .collect { visibleRegion ->
                        visibleRegion?.let {

                            cameraPositionChanged(it.latLngBounds)
                        }
                    }
            }
        }
    }
}