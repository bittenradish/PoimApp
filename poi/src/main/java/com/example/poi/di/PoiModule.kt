package com.example.poi.di

import com.example.poi.BuildConfig.BASE_URL
import com.example.poi.data.PoiRepositoryImpl
import com.example.poi.data.api.PoiApi
import com.example.poi.data.api.PoiApiAdapter
import com.example.poi.domain.PoiRepository
import com.example.poi.presentation.PoiMapViewModel
import com.example.poi.presentation.model.MapStateReducer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val poiModule = module {
    single<PoiRepository> { PoiRepositoryImpl(poiApiAdapter = get()) }

    single { PoiApiAdapter(poiApi = get()) }

    single {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(PoiApi::class.java)
    }

    viewModel {
        PoiMapViewModel(poiRepository = get(), reducer = get())
    }

    factory {
        MapStateReducer()
    }
}