package com.example.poimapp.ui.poi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.PoiDetails
import com.example.poi.presentation.model.toIcon
import com.example.poimapp.ui.poi.model.PoiDetailsItem
import com.example.poimapp.ui.poi.model.PoiDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PoiDetailsViewModel(
    private val idList: List<String>,
    private val repository: PoiRepository
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow<PoiDetailsState>(PoiDetailsState.Loading)

    private var state: PoiDetailsState
        get() = mutableStateFlow.value
        set(value) {
            mutableStateFlow.value = value
        }

    val stateFlow: StateFlow<PoiDetailsState> = mutableStateFlow

    init {
        requestItems()
    }

    fun cleanSelected() {
        (state as? PoiDetailsState.Ready.Multiple)?.let {
            state = it.copy(selected = null)
        }
    }

    fun itemSelected(poiDetailsItem: PoiDetailsItem) {
        (state as? PoiDetailsState.Ready.Multiple)?.let {
            state = it.copy(selected = poiDetailsItem)
        }
    }

    private fun requestItems() {
        state = PoiDetailsState.Loading

        viewModelScope.launch {
            repository.getPoiDetails(idList = idList).fold(
                onSuccess = { list ->
                    list.forEach {
                        Log.d("DetailsVm", it.toString())
                    }
                    state = when (list.size) {
                        1 -> list.first().let {
                            PoiDetailsState.Ready.Single(it.toItem())
                        }

                        0 -> PoiDetailsState.Error("Marker not found")
                        else -> PoiDetailsState.Ready.Multiple(
                            list.map { it.toItem() },
                            null
                        )

                    }
                },
                onFailure = {
                    state = PoiDetailsState.Error("Unknown error")
                }
            )
        }
    }

    private fun PoiDetails.toItem() =
        PoiDetailsItem(
            id = id,
            name = name,
            typeIcon = vehicleType.toIcon(),
            provideName = provider.name,
            image = with(image) {
                url ?: mediumUrl ?: thumbUrl
            }
        )
}