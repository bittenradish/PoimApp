package com.example.poi.domain.model

data class BoundingBox(
    val northEastLatitude: Double,
    val northEastLongitude: Double,
    val southWestLatitude: Double,
    val southWestLongitude: Double,
)