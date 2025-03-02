package com.example.poi.data

import com.example.poi.data.api.PoiApiAdapter
import com.example.poi.data.api.model.toData
import com.example.poi.data.api.model.toDomain
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.BoundingBox
import com.example.poi.domain.model.PoiDetails
import com.example.poi.domain.model.PoisChunk
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
    override suspend fun getPoiList(boundingBox: BoundingBox): Flow<Result<PoisChunk>> =
        poiApiAdapter.getPoiList(box = boundingBox.toData(), pageSize = 10).map { responseResult ->
            responseResult.map {
                if (it.data.isEmpty()) {
                    PoisChunk.Finished
                } else {
                    PoisChunk.PoiList(it.data.map { item -> item.toDomain() })
                }
            }
        }

    override suspend fun getPoiDetails(id: String): Result<PoiDetails?> =
        poiApiAdapter.getPoiDetails(id).map { it.data.firstOrNull()?.toDomain() }
}