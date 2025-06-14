package com.example.poi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.NetworkExceptions
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.BoundingBox
import com.example.poi.domain.model.PoisChunk
import com.example.poi.presentation.model.MapState
import com.example.poi.presentation.model.MapStateReducer
import com.example.poi.presentation.model.PoiMarker
import com.example.resources.snackbar.SnackbarType
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PoiMapViewModel(
    private val poiRepository: PoiRepository,
    private val reducer: MapStateReducer,
) : ViewModel() {

    //TODO: Prepare to delegate
    private val mutableMapStateFlow = MutableStateFlow(MapState())

    private val uiEffectChannel: Channel<SnackbarType> = Channel(Channel.CONFLATED)

    private var job: Job? = null

    private var mapState: MapState
        get() = mutableMapStateFlow.value
        set(value) {
            mutableMapStateFlow.value = value
        }

    val uiEffectFlow: Flow<SnackbarType> = uiEffectChannel.receiveAsFlow()

    val mapStateFlow: StateFlow<List<PoiMarker>> =
        mutableMapStateFlow.map { it.poiMap.values.toList() }.distinctUntilChanged().stateIn(
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
                        mapState = reducer.reduceLoading(mapState, isLoading = false, box = box)
                        uiEffectChannel.trySend(
                            when(it){
                                is NetworkExceptions.NoConnectivityException -> SnackbarType.NO_INTERNET_ERROR
                                is NetworkExceptions.ServerErrorException -> SnackbarType.SERVER_ERROR
                                is NetworkExceptions.ClientErrorException -> SnackbarType.CLIENT_ERROR
                                else -> SnackbarType.UNKNOWN_ERROR
                            }
                        )
                    }
                )
            }
        }
    }
}