package com.example.poi.domain

import com.example.poi.domain.model.BoundingBox
import com.example.poi.domain.model.PoiDetails
import com.example.poi.domain.model.PoisChunk
import kotlinx.coroutines.flow.Flow

interface PoiRepository {
    fun getPoiList(boundingBox: BoundingBox): Flow<Result<PoisChunk>>

    suspend fun getPoiDetails(idList: List<String>): Result<List<PoiDetails>>
}