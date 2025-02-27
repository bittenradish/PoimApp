package com.example.poi.domain.model

data class PoiDetails(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val positionType: PositionType,
    val vehicleType: VehicleType,
    val parkingId: ULong?,
    val relation: AppRelation,
    val image: DetailImage,
    val provider: PoiProvider,
)

data class PoiProvider(
    val image: DetailImage,
    val name: String,
)

data class DetailImage(
    val thumbUrl: String?,
    val mediumUrl: String?,
    val url: String?,
)