package com.example.poimapp.di

import com.example.poimapp.ui.poi.PoiDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { (idList: List<String>) ->
        PoiDetailsViewModel(idList = idList, repository = get())
    }
}