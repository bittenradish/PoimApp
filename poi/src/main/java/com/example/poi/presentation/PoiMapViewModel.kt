package com.example.poi.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.BoundingBox
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PoiMapViewModel(private val poiRepository: PoiRepository) : ViewModel() {

    //TODO: make it private and prepare to delegate
    private val mutableMapStateFlow = MutableStateFlow<Map<String, LatLng>>(mapOf())

    private var job: Job? = null

    private var mapState: Map<String, LatLng>
        get() = mutableMapStateFlow.value
        set(value) {
            mutableMapStateFlow.value = value
        }

    val mapStateFlow: StateFlow<List<LatLng>> =
        mutableMapStateFlow.map { it.values.toList() }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )


    fun updateBoundingBox(box: LatLngBounds) {
        loadMapPoints(box)
    }

    private fun loadMapPoints(box: LatLngBounds) {
        Log.d("map", "loadingViewmodel")
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
            ).collect { list ->
                list.fold(
                    onSuccess = { pois ->
                        mapState = mapState.toMutableMap().apply {
                            this.putAll(pois.associate {
                                it.id to LatLng(
                                    it.latitude,
                                    it.longitude
                                )
                            })
                        }
                    },
                    onFailure = {
                        Log.d("ViewModel", it.stackTraceToString())
                    }
                )
            }
        }
    }
}