package com.example.poi.presentation.model

import com.example.poi.R
import com.example.poi.domain.model.Poi
import com.google.android.gms.maps.model.LatLng

internal fun Poi.toMarker() =
    PoiMarker(
        id = id,
        latLng = LatLng(latitude, longitude),
        markerImage = R.drawable.ic_car_24
    )