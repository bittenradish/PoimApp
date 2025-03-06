package com.example.poi.data.api

import com.example.poi.data.api.model.PoiDataResponse
import com.example.poi.data.api.model.PoiDetailsResponse
import com.example.poi.data.api.model.PoiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

internal interface PoiApi {

    //TODO: replace with generic

    @GET("/api/graph/discovery/pois")
    suspend fun getPoiList(
        @QueryMap queries: Map<String, String> = mapOf()
    ): PoiDataResponse<PoiResponse>

    @GET("api/graph/discovery/pois")
    suspend fun getPoiDetails(
        @Query("filter[id]") idList: String,
        @Query("extra_fields[pois]") extraFields: String,
    ): PoiDataResponse<PoiDetailsResponse>
}