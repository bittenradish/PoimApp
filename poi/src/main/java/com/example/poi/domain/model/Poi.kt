package com.example.poi.domain.model

data class Poi(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val positionType: PositionType,
    val vehicleType: VehicleType,
    val parkingId: ULong?,
    val relation: AppRelation,
)

enum class PositionType {
    PARKING,
    STANDALONE,
    UNKNOWN,
}

enum class VehicleType {
    CAR,
    BIKE,
    SCOOTER,
    OTHER,
    UNKNOWN,
}

enum class AppRelation {
    FOREIGN,
    NATIVE,
    UNKNOWN,
}