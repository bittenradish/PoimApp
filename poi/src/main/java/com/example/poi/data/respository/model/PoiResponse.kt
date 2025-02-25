package com.example.poi.data.respository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoiResponse(
    @SerialName("id") val id: String,
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double,
    @SerialName("name") val name: String,
    @SerialName("position_type") val positionType: String,
    @SerialName("vehicle_type") val vehicleType: String?,
    @SerialName("latest_parking_id") val parkingId: ULong?,
    @SerialName("app_relation") val relation: String,
//    TODO: Add later if needed, for now it is irrelevant
//    @SerialName("distance") val distance
)
