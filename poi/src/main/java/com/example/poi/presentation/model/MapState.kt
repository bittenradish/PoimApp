package com.example.poi.presentation.model

import androidx.annotation.DrawableRes
import com.example.poi.domain.model.Poi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterItem


sealed interface MapScreenStateEvents {
    data class MapStateEvent(
        val poiList: List<Poi> = emptyList(),
        val cameraBoundingBox: LatLngBounds? = null
    ) : MapScreenStateEvents


    data class LoadingStateEvent(val isLoadingState: Boolean) : MapScreenStateEvents
}

data class MapState(
    val poiMap: Map<String, PoiMarker> = mapOf(),
    val cameraBoundingBox: LatLngBounds? = null
)


data class PoiMarker(
    val id: String,
    val latLng: LatLng,
    @DrawableRes val markerImage: Int,
    val itemZ: Float = 0f
) : ClusterItem {
    override fun getPosition(): LatLng = latLng

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float = itemZ
}
