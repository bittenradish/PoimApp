package com.example.poi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.NetworkExceptions
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.BoundingBox
import com.example.poi.domain.model.PoisChunk
import com.example.poi.presentation.model.MapScreenStateEvents
import com.example.poi.presentation.model.MapState
import com.example.poi.presentation.model.MapStateReducer
import com.example.poi.presentation.model.PoiMarker
import com.example.resources.snackbar.SnackbarType
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class PoiMapViewModel(
    private val poiRepository: PoiRepository,
    private val reducer: MapStateReducer,
) : ViewModel() {

    private val mutableStateLatLngBounds = MutableStateFlow<LatLngBounds?>(null)

    private val stateEventFlow: Flow<MapScreenStateEvents> =
        mutableStateLatLngBounds.flatMapLatest { box ->
            box?.let {
                flow {
                    emit(MapScreenStateEvents.LoadingStateEvent(true))
                    loadMapPoints(it).collect { result ->
                        result.fold(
                            onSuccess = { poisChunk ->
                                when (poisChunk) {
                                    PoisChunk.Finished ->
                                        emit(MapScreenStateEvents.LoadingStateEvent(false))

                                    is PoisChunk.PoiList -> emit(
                                        MapScreenStateEvents.MapStateEvent(
                                            poiList = poisChunk.pois,
                                            cameraBoundingBox = box
                                        )
                                    )
                                }
                            },
                            onFailure = {
                                uiEffectChannel.trySend(
                                    when (it) {
                                        is NetworkExceptions.NoConnectivityException -> SnackbarType.NO_INTERNET_ERROR
                                        is NetworkExceptions.ServerErrorException -> SnackbarType.SERVER_ERROR
                                        is NetworkExceptions.ClientErrorException -> SnackbarType.CLIENT_ERROR
                                        else -> SnackbarType.UNKNOWN_ERROR
                                    }
                                )
                                emit(MapScreenStateEvents.LoadingStateEvent(false))
                            }
                        )
                    }
                }
            } ?: flow {
                emit(MapScreenStateEvents.MapStateEvent())
            }
        }

    private fun loadMapPoints(box: LatLngBounds): Flow<Result<PoisChunk>> = flow {
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
            emit(poiList)
        }

    }

    private val uiEffectChannel: Channel<SnackbarType> = Channel(Channel.CONFLATED)

    val uiEffectFlow: Flow<SnackbarType> = uiEffectChannel.receiveAsFlow()

    val mapStateFlow: StateFlow<List<PoiMarker>> =
        stateEventFlow.filterIsInstance<MapScreenStateEvents.MapStateEvent>()
            .scan(MapState()) { initial, current ->
                reducer.reducePoiList(initial, current.poiList)
            }.map {
                it.poiMap.values.toList()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    val isLoadingFlow: StateFlow<Boolean> =
        stateEventFlow.filterIsInstance<MapScreenStateEvents.LoadingStateEvent>()
            .map {
                it.isLoadingState
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )


    fun updateBoundingBox(box: LatLngBounds) {
        mutableStateLatLngBounds.value = box
    }
}