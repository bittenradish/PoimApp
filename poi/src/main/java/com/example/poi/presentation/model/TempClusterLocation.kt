package com.example.poi.presentation.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class TempClusterLocation(
    val latLng: LatLng,
    val itemZ: Float = 0f
) : ClusterItem {
    override fun getPosition(): LatLng = latLng

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float = itemZ

}