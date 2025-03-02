package com.example.poi.presentation.model

import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterItem

data class MapState(
    val isLoading: Boolean = false,
    val poiList: Map<String, PoiMarker> = mapOf(),
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
