package com.example.poi.domain

import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PoiDetails
import kotlinx.coroutines.flow.Flow

interface PoiRepository {
    suspend fun getPoiList(): Flow<Result<List<Poi>>>

    suspend fun getPoiDetails(id: String): Result<PoiDetails?>
}