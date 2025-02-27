package com.example.poimapp.di

import com.example.poimapp.utils.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

var networkClientModule = module {
    single {
        OkHttpClient
            .Builder()
            .addInterceptor(NetworkConnectionInterceptor(androidContext()))
            .build()
    }
}