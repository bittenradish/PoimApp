package com.example.poi.presentation

import co.touchlab.stately.concurrency.AtomicInt
import com.example.core.NetworkExceptions
import com.example.poi.domain.PoiRepository
import com.example.poi.domain.model.AppRelation
import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PoisChunk
import com.example.poi.domain.model.PositionType
import com.example.poi.domain.model.VehicleType
import com.example.poi.presentation.model.MapStateReducer
import com.example.poi.presentation.model.PoiMarker
import com.example.resources.snackbar.SnackbarType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds


class PoiMapViewModelIntegrationTest {

    private lateinit var viewModel: PoiMapViewModel

    @MockK
    private var poiRepository: PoiRepository = mockk()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var reducer: MapStateReducer


    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        reducer = MapStateReducer()
        Dispatchers.setMain(testDispatcher)
        viewModel = PoiMapViewModel(poiRepository, reducer)
    }

    @Test
    fun `updateBoundingBox, success with PoiList, updates mapStateFlow and isLoadingFlow`() =
        runTest(testDispatcher) {
            val bounds = LatLngBounds(LatLng(0.0, 0.0), LatLng(1.0, 1.0))
            val poiList = listOf(
                Poi(
                    "1",
                    0.5,
                    0.5,
                    "POI 1",
                    PositionType.STANDALONE,
                    VehicleType.BIKE,
                    parkingId = null,
                    AppRelation.NATIVE
                )
            )
            val expectedMarkers =
                listOf(PoiMarker("1", LatLng(0.5, 0.5), 1))

            val mutableSharedFlow = MutableSharedFlow<Result<PoisChunk>>()

            coEvery {
                poiRepository.getPoiList(any())
            } coAnswers {
                mutableSharedFlow
            }

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.isLoadingFlow.collect {
                    println("Collected: $it")
                }

                viewModel.mapStateFlow.collect {}
            }

            assertEquals(
                false,
                viewModel.isLoadingFlow.value,
                "Initial loading state should be false"
            )

            viewModel.updateBoundingBox(bounds)


            advanceUntilIdle()
            assertEquals(
                true,
                viewModel.isLoadingFlow.value,
                "Updated loading state should be true"
            )

            mutableSharedFlow.emit(Result.success(PoisChunk.PoiList(poiList)))
            advanceUntilIdle()

            assertEquals(
                true,
                viewModel.isLoadingFlow.value,
                "Updated second loading state should be true"
            )


            mutableSharedFlow.emit(Result.success(PoisChunk.Finished))
            advanceUntilIdle()

            assertEquals(
                false,
                viewModel.isLoadingFlow.value,
                "Final loading state should be false"
            )

            assertTrue("mapStateFlow should emit correct markers") {
                val state = viewModel.mapStateFlow.value
                expectedMarkers.size == state.size
                        && expectedMarkers.first().latLng == state.first().latLng
                        && expectedMarkers.first().id == state.first().id
            }

            coVerify { poiRepository.getPoiList(any()) }
        }

    private suspend fun TestScope.testForGetPoiListFailure(
        exception: Exception,
        snackbarType: SnackbarType
    ) {
        val bounds = LatLngBounds(LatLng(0.0, 0.0), LatLng(1.0, 1.0))
        val sharedFlow = MutableSharedFlow<Result<PoisChunk>>()

        coEvery {
            poiRepository.getPoiList(any())
        } coAnswers {
            sharedFlow
        }

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.isLoadingFlow.collect {
                println("Collected: $it")
            }

            viewModel.mapStateFlow.collect {}
        }

        assertEquals(
            false,
            viewModel.isLoadingFlow.value,
            "Initial loading state should be false"
        )

        viewModel.updateBoundingBox(bounds)

        advanceUntilIdle()
        assertEquals(
            true,
            viewModel.isLoadingFlow.value,
            "Middle loading state should be true"
        )

        sharedFlow.emit(Result.failure(exception))
        advanceUntilIdle()

        assertEquals(
            false,
            viewModel.isLoadingFlow.value,
            "Final loading state should be false"
        )

        assertEquals(
            snackbarType,
            viewModel.uiEffectFlow.first(),
            "uiEffectFlow should emit correct error"
        )
        coVerify { (poiRepository).getPoiList(any()) }
    }

    @Test
    fun `updateBoundingBox, no Internet connection, updates isLoadingFlow and uiEffectFlow`() =
        runTest(testDispatcher) {
            testForGetPoiListFailure(
                NetworkExceptions.NoConnectivityException(),
                SnackbarType.NO_INTERNET_ERROR
            )
        }

    @Test
    fun `updateBoundingBox, server error, updates isLoadingFlow and uiEffectFlow`() =
        runTest(testDispatcher) {
            testForGetPoiListFailure(
                NetworkExceptions.ServerErrorException(500, ""),
                SnackbarType.SERVER_ERROR
            )
        }

    @Test
    fun `updateBoundingBox, client error, updates isLoadingFlow and uiEffectFlow`() =
        runTest(testDispatcher) {
            testForGetPoiListFailure(
                NetworkExceptions.ClientErrorException(404, ""),
                SnackbarType.CLIENT_ERROR
            )
        }

    @Test
    fun `updateBoundingBox, unknown error, updates isLoadingFlow and uiEffectFlow`() =
        runTest(testDispatcher) {
            testForGetPoiListFailure(NetworkExceptions.UnknownError(), SnackbarType.UNKNOWN_ERROR)
            testForGetPoiListFailure(Exception(), SnackbarType.UNKNOWN_ERROR)
        }

    @Test
    fun `updateBoundingBox, multiple calls, cancels previous job`() = runTest(testDispatcher) {
        val bounds1 = LatLngBounds(LatLng(0.0, 0.0), LatLng(1.0, 1.0))
        val bounds2 = LatLngBounds(LatLng(2.0, 2.0), LatLng(3.0, 3.0))
        val poiList1 = listOf(
            Poi(
                "1",
                0.5,
                0.5,
                "POI 1",
                PositionType.STANDALONE,
                VehicleType.BIKE,
                parkingId = null,
                AppRelation.NATIVE
            )
        )

        val wasCancelled = AtomicInt(0)

        val repoFlow1 = flow {
            emit(Result.success(PoisChunk.PoiList(poiList1)))
            delay(1.seconds)
            emit(Result.success(PoisChunk.PoiList(poiList1)))
            delay(1.seconds)
            emit(Result.success(PoisChunk.PoiList(poiList1)))
            throw Exception("Flow is Not cancelled")
        }.onCompletion {
            if (it?.cause is CancellationException) {
                wasCancelled.set(1)
            }
        }

        val repoFlow2 = flow {
            emit(Result.success(PoisChunk.PoiList(poiList1)))
            delay(1.seconds)
            emit(Result.success(PoisChunk.PoiList(poiList1)))
            delay(1.seconds)
            emit(Result.success(PoisChunk.PoiList(poiList1)))
        }.catch {
            assertTrue { it is CancellationException }
        }

        coEvery {
            poiRepository.getPoiList(any())
        } coAnswers {
            repoFlow1
        } coAndThen {
            repoFlow2
        }


        viewModel.updateBoundingBox(bounds1)
        advanceTimeBy(1.seconds)
        viewModel.updateBoundingBox(bounds2)

        advanceUntilIdle()
        assertTrue(wasCancelled.get() == 1, "Expected first flow to be cancelled")
        coVerify(exactly = 2) { poiRepository.getPoiList(any()) }
    }

}