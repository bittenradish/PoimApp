package com.example.poi.data.api.model

import com.example.poi.domain.model.BoundingBox

internal fun BoundingBox.toData() =
    "{\"ne_lat\":${this.northEastLatitude},\"ne_lng\":${northEastLongitude},\"sw_lat\":${southWestLatitude},\"sw_lng\":${southWestLongitude}}"