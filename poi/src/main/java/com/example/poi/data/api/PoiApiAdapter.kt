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

        while (true) {
            filterMap[pageNumberFiler] = pageNumber.toString()

            val result = runCatching { poiApi.getPoiList(filterMap) }

            if (result.getOrNull()?.data?.isEmpty() == true) {
                break
            }

            emit(result)
            delay(100)
            pageNumber += 1

            if (result.isFailure) {
                break
            }
        }
    }

    suspend fun getPoiDetails(id: String) = runCatching {
        poiApi.getPoiDetails(id, extraFields)
    }

}