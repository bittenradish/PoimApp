package com.example.poimapp.ui.poi.model

import androidx.annotation.DrawableRes

sealed class PoiDetailsState {

    abstract val backNavigationEnabled: Boolean

    sealed class Ready : PoiDetailsState() {
        data class Multiple(
            val listOfItems: List<PoiDetailsItem>,
            val selected: PoiDetailsItem?,
        ) : Ready() {
            override val backNavigationEnabled: Boolean = selected == null
        }

        data class Single(val item: PoiDetailsItem) : Ready() {
            override val backNavigationEnabled: Boolean = true
        }
    }

    data object Loading : PoiDetailsState() {
        override val backNavigationEnabled: Boolean = true
    }

    sealed class ErrorState : PoiDetailsState() {
        override val backNavigationEnabled: Boolean = true

        data object Client : ErrorState()

        data object Server : ErrorState()

        data object NoConnection : ErrorState()

        data object Unknown : ErrorState()

        data class MarkerNotFound(val message: String) : ErrorState()
    }
}

data class PoiDetailsItem(
    val id: String,
    val name: String,
    @DrawableRes val typeIcon: Int,
    val provideName: String,
    val image: String?
)