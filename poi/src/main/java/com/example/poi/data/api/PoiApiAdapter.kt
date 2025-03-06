package com.example.poi.data.api

import com.example.poi.data.api.model.PoiDataResponse
import com.example.poi.data.api.model.PoiResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class PoiApiAdapter(private val poiApi: PoiApi) {

    private val boxFiler = "filter[bounding_box]"
    private val pageSizeFilter = "page[size]"
    private val pageNumberFiler = "page[number]"
    private val extraFields = "image,provider"

    fun getPoiList(
        box: String?,
        pageSize: Int?,
    ): Flow<Result<PoiDataResponse<PoiResponse>>> = flow {
        var pageNumber = 1

        val filterMap = mutableMapOf<String, String>().apply {
            box?.let { this[boxFiler] = it }
            pageSize?.let { this[pageSizeFilter] = it.toString() }
        }

        do {
            filterMap[pageNumberFiler] = pageNumber.toString()

            val result = runCatching { poiApi.getPoiList(filterMap) }

            emit(result)
            delay(100)
            pageNumber += 1
        } while (result.getOrNull()?.data?.isNotEmpty() == true)
    }

    suspend fun getPoiDetails(idList: List<String>) = runCatching {
        poiApi.getPoiDetails(idList.joinToString(","), extraFields)
    }

}