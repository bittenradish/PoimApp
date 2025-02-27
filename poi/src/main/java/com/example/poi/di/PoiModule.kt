package com.example.poi.di

import com.example.poi.data.PoiRepositoryImpl
import com.example.poi.domain.PoiRepository
import org.koin.dsl.module

val poiModule = module {
    single<PoiRepository> { PoiRepositoryImpl() }
}