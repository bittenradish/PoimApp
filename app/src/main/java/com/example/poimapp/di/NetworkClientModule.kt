package com.example.poimapp.di

import com.example.poimapp.utils.NetworkConnectionInterceptor
import com.example.poimapp.utils.NetworkErrorMappingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

var networkClientModule = module {
    single {
        OkHttpClient
            .Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(NetworkConnectionInterceptor(androidContext()))
            .addInterceptor(NetworkErrorMappingInterceptor())
            .build()
    }

    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }
}