package com.example.poi.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class PoiDetailsResponse(
    @SerialName("id") val id: String,
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double,
    @SerialName("name") val name: String,
    @SerialName("position_type") val positionType: String,
    @SerialName("vehicle_type") val vehicleType: String?,
    @SerialName("latest_parking_id") val parkingId: ULong?,
    @SerialName("app_relation") val relation: String,
    @SerialName("image") val image: DetailImageResponse,
    @SerialName("provider") val provider: PoiProviderResponse,
)

@Serializable
internal data class DetailImageResponse(
    @SerialName("thumb_url") val thumbUrl: String?,
    @SerialName("medium_url") val mediumUrl: String?,
    @SerialName("url") val url: String?,
)


@Serializable
internal data class PoiProviderResponse(
    @SerialName("image") val image: DetailImageResponse,
    @SerialName("name") val name: String,
)
