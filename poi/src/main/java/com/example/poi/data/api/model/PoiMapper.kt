package com.example.poi.data.api.model

import com.example.poi.domain.model.AppRelation
import com.example.poi.domain.model.AppRelation.FOREIGN
import com.example.poi.domain.model.AppRelation.NATIVE
import com.example.poi.domain.model.DetailImage
import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PoiDetails
import com.example.poi.domain.model.PoiProvider
import com.example.poi.domain.model.PositionType
import com.example.poi.domain.model.PositionType.PARKING
import com.example.poi.domain.model.PositionType.STANDALONE
import com.example.poi.domain.model.PositionType.UNKNOWN
import com.example.poi.domain.model.VehicleType
import com.example.poi.domain.model.VehicleType.BIKE
import com.example.poi.domain.model.VehicleType.CAR
import com.example.poi.domain.model.VehicleType.OTHER
import com.example.poi.domain.model.VehicleType.SCOOTER

internal fun PoiResponse.toDomain() =
    Poi(
        id = id,
        latitude = latitude,
        longitude = longitude,
        name = name,
        positionType = mapPositionType(positionType),
        vehicleType = mapVehicleType(vehicleType),
        relation = mapRelation(relation),
        parkingId = parkingId
    )

internal fun PoiDetailsResponse.toDomain() =
    PoiDetails(
        id = id,
        latitude = latitude,
        longitude = longitude,
        name = name,
        positionType = mapPositionType(positionType),
        vehicleType = mapVehicleType(vehicleType),
        parkingId = parkingId,
        relation = mapRelation(relation),
        image = image.toDomain(),
        provider = provider.toDomain(),
    )

internal fun DetailImageResponse.toDomain() =
    DetailImage(
        thumbUrl = thumbUrl,
        mediumUrl = mediumUrl,
        url = url,
    )

internal fun PoiProviderResponse.toDomain() =
    PoiProvider(
        name = name,
        image = image.toDomain()
    )

internal fun mapRelation(value: String?): AppRelation =
    when (value?.lowercase()) {
        "foreign" -> FOREIGN
        "native" -> NATIVE
        else -> AppRelation.UNKNOWN
    }


internal fun mapPositionType(value: String?): PositionType =
    when (value?.lowercase()) {
        "parking" -> PARKING
        "standalone" -> STANDALONE
        else -> UNKNOWN
    }

internal fun mapVehicleType(value: String?): VehicleType =
    when (value?.lowercase()) {
        "car" -> CAR
        "bike" -> BIKE
        "scooter" -> SCOOTER
        "other" -> OTHER
        else -> VehicleType.UNKNOWN
    }