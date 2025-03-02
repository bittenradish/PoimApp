package com.example.poi.domain.model

sealed interface PoisChunk {
    data object Finished : PoisChunk

    data class PoiList(val pois: List<Poi>) : PoisChunk
}