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

enum class PositionType(val type: String){
    PARKING("parking"),
    STANDALONE("standalone"),
}

enum class VehicleType(val type: String){
    CAR("car"),
    BIKE("bike"),
    SCOOTER("scooter"),
    OTHER("other"),
    UNKNOWN("unknown"),
}

enum class AppRelation(val relation: String){
    FOREIGN("foreign"),
    NATIVE("native"),
}