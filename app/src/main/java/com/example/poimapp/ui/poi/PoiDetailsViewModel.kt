package com.example.poimapp.ui.poi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poi.domain.PoiRepository
import com.example.poi.presentation.model.toIcon
import com.example.poimapp.ui.poi.model.PoiDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PoiDetailsViewModel(
    private val repository: PoiRepository
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow<PoiDetailsState>(PoiDetailsState.Loading)

    val stateFlow: StateFlow<PoiDetailsState> = mutableStateFlow

    fun requestItem(id: String) {
        mutableStateFlow.value = PoiDetailsState.Loading

        viewModelScope.launch {
            repository.getPoiDetails(id).fold(
                onSuccess = {
                    mutableStateFlow.value = it?.let {
                        PoiDetailsState.PoiDetailsItem(
                            id = it.id,
                            name = it.name,
                            typeIcon = it.vehicleType.toIcon(),
                            provideName = it.provider.name,
                            image = with(it.image) {
                                url ?: mediumUrl ?: thumbUrl
                            }
                        )
                    } ?: let {
                        PoiDetailsState.Error("Marker not found")
                    }
                },
                onFailure = {
                    mutableStateFlow.value = PoiDetailsState.Error("Unknown error")
                }
            )
        }
    }
}