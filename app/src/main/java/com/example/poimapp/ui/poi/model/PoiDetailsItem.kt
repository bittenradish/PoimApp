package com.example.poimapp.ui.poi.model

import androidx.annotation.DrawableRes

sealed interface PoiDetailsState {
    data class PoiDetailsItem(
        val id: String,
        val name: String,
        @DrawableRes val typeIcon: Int,
        val provideName: String,
        val image: String?
    ) : PoiDetailsState

    data object Loading : PoiDetailsState

    data class Error(val message: String) : PoiDetailsState
}
