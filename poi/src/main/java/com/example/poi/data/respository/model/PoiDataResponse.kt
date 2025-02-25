package com.example.poi.data.respository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoiListResponse(
    @SerialName("data") val data: List<PoiResponse>
)