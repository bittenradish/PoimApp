package com.example.poi.presentation.model

import com.example.poi.domain.model.Poi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class MapStateReducer {
    fun reduceLoading(oldState: MapState, isLoading: Boolean, box: LatLngBounds) =
        oldState.copy(isLoading = isLoading, cameraBoundingBox = box)


    fun reducePoiList(oldState: MapState, poiList: List<Poi>): MapState =
        oldState.copy(
            poiList = oldState.poiList.toMutableMap().apply {
                this.putAll(
                    poiList.associate {
                        it.id to it.toMarker()
                    }
                )
            }.filter {
                it.value.latLng.isInBox(oldState.cameraBoundingBox)
            }
        )

    //TODO: Implement logic to keep or delete marker from the map
    private fun LatLng.isInBox(latLngBounds: LatLngBounds?) = true
}