package com.example.poi.presentation.model

import com.example.poi.R
import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.VehicleType
import com.google.android.gms.maps.model.LatLng

internal fun Poi.toMarker() =
    PoiMarker(
        id = id,
        latLng = LatLng(latitude, longitude),
        markerImage = vehicleType.toIcon()
    )

fun VehicleType.toIcon(): Int = when (this) {
    VehicleType.CAR -> R.drawable.ic_car_24
    VehicleType.BIKE -> R.drawable.ic_bike_24
    VehicleType.SCOOTER -> R.drawable.ic_scooter_24
    else -> R.drawable.ic_question_mark_24
}