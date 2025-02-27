package com.example.poi.domain

import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PoiDetails

interface PoiRepository {
    suspend fun getPoiList(): Result<List<Poi>>

    suspend fun getPoiDetails(): PoiDetails?
}