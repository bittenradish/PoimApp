package com.example.poi.data.api

import com.example.poi.data.api.model.PoiDataResponse
import com.example.poi.data.api.model.PoiDetailsResponse
import com.example.poi.data.api.model.PoiResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PoiApi {

    @GET("/api/graph/discovery/pois")
    suspend fun getPoiList(
// TODO:        @Query("filter")filter:
    ): PoiDataResponse<PoiResponse>

    @GET("api/graph/discovery/pois")
    suspend fun get(
        @Query("filter[id]") id: String,
        @Query("filter[pois]") pois: List<String>,
    ): PoiDataResponse<PoiDetailsResponse>
}