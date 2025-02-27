package com.example.poi.data

import com.example.poi.data.api.PoiApi
import com.example.poi.data.api.model.toDomain
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PoiDetails

internal class PoiRepositoryImpl(
    private val poiApi: PoiApi,
) : PoiRepository {

    //TODO:
//{
    //    "error": "Failed casting id for type integer. Please check given value.",
//    "error_type": "typecast_failed"
//}
    override suspend fun getPoiList(): Result<List<Poi>> =
        runCatching {
            poiApi.getPoiList().data.map { it.toDomain() }
        }

    override suspend fun getPoiDetails(): PoiDetails? =
        poiApi.get("fdsf", listOf()).data.firstOrNull()?.toDomain()
}