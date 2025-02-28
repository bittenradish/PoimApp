package com.example.poi.data

import com.example.poi.data.api.PoiApiAdapter
import com.example.poi.data.api.model.toDomain
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PoiDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PoiRepositoryImpl(
    private val poiApiAdapter: PoiApiAdapter,
) : PoiRepository {
    //TODO:
//{
    //    "error": "Failed casting id for type integer. Please check given value.",
//    "error_type": "typecast_failed"
//}
    override suspend fun getPoiList(): Flow<Result<List<Poi>>> =
        poiApiAdapter.getPoiList(box = null, pageSize = 10).map { responseResult ->
            responseResult.map { it.data.map { item -> item.toDomain() } }
        }

    override suspend fun getPoiDetails(id: String): Result<PoiDetails?> =
        poiApiAdapter.getPoiDetails(id).map { it.data.firstOrNull()?.toDomain() }
}