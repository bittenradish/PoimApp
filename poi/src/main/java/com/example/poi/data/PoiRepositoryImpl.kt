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

    override fun getPoiList(boundingBox: BoundingBox): Flow<Result<PoisChunk>> =
        poiApiAdapter.getPoiList(box = boundingBox.toData(), pageSize = 10).map { responseResult ->
            responseResult.map {
                if (it.data.isEmpty()) {
                    PoisChunk.Finished
                } else {
                    PoisChunk.PoiList(it.data.map { item -> item.toDomain() })
                }
            }
        }

    override suspend fun getPoiDetails(idList: List<String>): Result<List<PoiDetails>> =
        poiApiAdapter.getPoiDetails(idList).map { response ->
            response.data.map {
                it.toDomain()
            }
        }
}