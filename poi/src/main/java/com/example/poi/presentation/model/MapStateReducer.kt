package com.example.poi.presentation.model

import com.example.poi.domain.model.Poi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.max
import kotlin.math.min

class MapStateReducer {
    fun reduceLoading(oldState: MapState, isLoading: Boolean, box: LatLngBounds) =
        oldState.copy(isLoading = isLoading, cameraBoundingBox = box)


    fun reducePoiList(oldState: MapState, poiList: List<Poi>): MapState =
        oldState.copy(
            poiMap = oldState.poiMap.toMutableMap().apply {
                putAll(
                    poiList.associate {
                        it.id to it.toMarker()
                    }
                )
            }.filter {
                it.value.latLng.isInBox(oldState.cameraBoundingBox)
            }
        )

    private fun LatLng.isInBox(latLngBounds: LatLngBounds?): Boolean = latLngBounds?.let {
        val deadZoneCoeff = 1.3

        val minLat = min(it.southwest.latitude, it.northeast.latitude) / deadZoneCoeff
        val minLng = min(it.southwest.longitude, it.northeast.longitude) / deadZoneCoeff

        val maxLat = max(it.southwest.latitude, it.northeast.latitude) * deadZoneCoeff
        val maxLng = max(it.southwest.longitude, it.northeast.longitude) * deadZoneCoeff

        return (latitude in minLat..maxLat) && (longitude in minLng..maxLng)
    } ?: true
}