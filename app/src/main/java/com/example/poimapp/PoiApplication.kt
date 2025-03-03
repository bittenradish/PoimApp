package com.example.poimapp

import android.app.Application
import com.example.poimapp.di.appModule
import com.example.poimapp.di.featureModules
import com.example.poimapp.di.networkClientModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PoiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin(){
        startKoin {
            androidLogger()
            androidContext(this@PoiApplication)
            modules(
                listOf(
                    *featureModules,
                    appModule,
                    networkClientModule
                )
            )
        }
    }
}