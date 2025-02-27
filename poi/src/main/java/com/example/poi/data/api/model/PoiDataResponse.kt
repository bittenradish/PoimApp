package com.example.poi.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PoiDataResponse<T>(
    @SerialName("data") val data: List<T>
)