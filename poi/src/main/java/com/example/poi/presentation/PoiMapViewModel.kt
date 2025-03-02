package com.example.poi.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.BoundingBox
import com.example.poi.domain.model.PoisChunk
import com.example.poi.presentation.model.MapState
import com.example.poi.presentation.model.MapStateReducer
import com.example.poi.presentation.model.PoiMarker
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PoiMapViewModel(
    private val poiRepository: PoiRepository,
    private val reducer: MapStateReducer,
) : ViewModel() {

    //TODO: make it private and prepare to delegate
    private val mutableMapStateFlow = MutableStateFlow(MapState())

    private var job: Job? = null

    private var mapState: MapState
        get() = mutableMapStateFlow.value
        set(value) {
            mutableMapStateFlow.value = value
        }

    val mapStateFlow: StateFlow<List<PoiMarker>> =
        mutableMapStateFlow.map { it.poiList.values.toList() }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    val isLoadingFlow: StateFlow<Boolean> =
        mutableMapStateFlow.map { it.isLoading }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )


    fun updateBoundingBox(box: LatLngBounds) {
        loadMapPoints(box)
    }

    private fun loadMapPoints(box: LatLngBounds) {
        Log.d("map", "loadingViewmodel")
        mapState = reducer.reduceLoading(mapState, isLoading = true, box)
        job?.cancel()

        job = viewModelScope.launch {
            poiRepository.getPoiList(
                with(box) {
                    BoundingBox(
                        northEastLatitude = northeast.latitude,
                        northEastLongitude = northeast.longitude,
                        southWestLatitude = southwest.latitude,
                        southWestLongitude = southwest.longitude
                    )
                }
            ).collect { poiList ->
                poiList.fold(
                    onSuccess = { poisChunk ->
                        when (poisChunk) {
                            PoisChunk.Finished -> mapState = reducer.reduceLoading(
                                mapState,
                                isLoading = false,
                                box = box
                            )

                            is PoisChunk.PoiList -> mapState =
                                reducer.reducePoiList(oldState = mapState, poiList = poisChunk.pois)
                        }
                    },
                    onFailure = {
                        reducer.reduceLoading(mapState, isLoading = false, box = box)
                    }
                )
            }
        }
    }
}