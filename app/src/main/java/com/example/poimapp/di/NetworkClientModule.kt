package com.example.poimapp.di

import okhttp3.OkHttpClient
import org.koin.dsl.module

var networkClientModule = module {
    single {
        OkHttpClient
            .Builder()
            .build()
    }
}