package com.example.poi.presentation.model

import com.example.poi.domain.model.AppRelation
import com.example.poi.domain.model.Poi
import com.example.poi.domain.model.PositionType
import com.example.poi.domain.model.VehicleType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapStateReducerTest {

    private val reducer = MapStateReducer()

    @Test
    fun `reduceLoading updates isLoading and cameraBoundingBox`() {
        val oldState = MapState()
        val newBox = LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
        val newState = reducer.reduceLoading(oldState, isLoading = true, box = newBox)

        assertEquals(true, newState.isLoading, "isLoading should be true")
        assertEquals(newBox, newState.cameraBoundingBox, "cameraBoundingBox should be updated")
        assertEquals(oldState.poiList, newState.poiList, "poiList should remain unchanged")
    }

    @Test
    fun `reduceLoading updates to false`() {
        val oldState = MapState(isLoading = true)
        val newBox = LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
        val newState = reducer.reduceLoading(oldState, isLoading = false, box = newBox)
        assertEquals(false, newState.isLoading)
    }

    @Test
    fun `reducePoiList adds new POIs and filters by bounding box - with MockK example`() {
        val oldState = MapState(
            cameraBoundingBox = LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
        )
        val poiList = listOf(
            Poi(
                "1",
                1.5,
                1.5,
                "Name1",
                PositionType.PARKING,
                VehicleType.CAR,
                null,
                AppRelation.FOREIGN
            ),
            Poi(
                "2",
                3.0,
                3.0,
                "Name2",
                PositionType.STANDALONE,
                VehicleType.BIKE,
                1231UL,
                AppRelation.UNKNOWN
            )
        )

        val newState = reducer.reducePoiList(oldState, poiList)

        assertEquals(1, newState.poiList.size, "Only one POI should be in the list")
        assertTrue(newState.poiList.containsKey("1"), "POI 1 should be present")
        assertFalse(newState.poiList.containsKey("2"), "POI 2 should be filtered out")
        assertEquals(
            LatLng(1.5, 1.5),
            newState.poiList["1"]?.latLng,
            "POI 1 should have correct coordinates"
        )
    }

    @Test
    fun `reducePoiList adds new POIs without filter`() {
        val oldState = MapState(
            cameraBoundingBox = LatLngBounds(LatLng(1.0, 1.0), LatLng(5.0, 5.0))
        )
        val poiList = listOf(
            Poi(
                "1",
                1.5,
                1.5,
                "Name1",
                PositionType.PARKING,
                VehicleType.CAR,
                null,
                AppRelation.FOREIGN
            ),
            Poi(
                "2",
                3.0,
                3.0,
                "Name2",
                PositionType.STANDALONE,
                VehicleType.BIKE,
                1231UL,
                AppRelation.UNKNOWN
            )
        )

        val newState = reducer.reducePoiList(oldState, poiList)

        assertEquals(2, newState.poiList.size)
        assertTrue(newState.poiList.containsKey("1"))
        assertTrue(newState.poiList.containsKey("2"))
    }

    @Test
    fun `reducePoiList adds to the current List`() {
        val oldState = MapState(
            cameraBoundingBox = LatLngBounds(LatLng(1.0, 1.0), LatLng(4.0, 4.0)),
            poiList = mapOf(
                "1" to PoiMarker("1", LatLng(1.5, 1.5), 1)
            )
        )

        val poiList = listOf(
            Poi(
                "2",
                3.0,
                3.0,
                "Name2",
                PositionType.STANDALONE,
                VehicleType.BIKE,
                1231UL,
                AppRelation.UNKNOWN
            )
        )

        val newState = reducer.reducePoiList(oldState, poiList)
        assertEquals(2, newState.poiList.size)
        assertTrue(newState.poiList.containsKey("1"))
        assertTrue(newState.poiList.containsKey("2"))
        assertEquals(LatLng(3.0, 3.0), newState.poiList["2"]?.latLng)
    }

    @Test
    fun `reducePoiList adds to the current List and updated existing POI`() {
        val oldState = MapState(
            cameraBoundingBox = LatLngBounds(LatLng(1.0, 1.0), LatLng(4.0, 4.0)),
            poiList = mapOf(
                "1" to PoiMarker("1", LatLng(1.5, 1.5), 1)
            )
        )

        val poiList = listOf(
            Poi(
                "1",
                1.2,
                1.2,
                "New name",
                PositionType.STANDALONE,
                VehicleType.BIKE,
                1231UL,
                AppRelation.UNKNOWN
            ),
            Poi(
                "2",
                3.0,
                3.0,
                "Name2",
                PositionType.STANDALONE,
                VehicleType.BIKE,
                1231UL,
                AppRelation.UNKNOWN
            )
        )

        val newState = reducer.reducePoiList(oldState, poiList)
        assertEquals(2, newState.poiList.size)
        assertTrue(newState.poiList.containsKey("1"))
        assertTrue(newState.poiList.containsKey("2"))
        assertEquals(LatLng(3.0, 3.0), newState.poiList["2"]?.latLng)
        assertEquals(LatLng(1.2, 1.2), newState.poiList["1"]?.latLng)
    }

    @Test
    fun `reducePoiList with empty POI list returns same POI list`() {
        val oldState = MapState(
            poiList = mapOf("1" to PoiMarker("1", LatLng(1.0, 1.0), 1))
        )
        val newState = reducer.reducePoiList(oldState, emptyList())
        assertEquals(oldState.poiList, newState.poiList)
    }


    @Test
    fun `isInBox returns true when point is inside the box`() {
        val bounds = LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
        val pointInside = LatLng(1.5, 1.5)
        val method = MapStateReducer::class.java.getDeclaredMethod(
            "isInBox",
            LatLng::class.java,
            LatLngBounds::class.java
        )
        method.isAccessible = true
        assertTrue(method.invoke(reducer, pointInside, bounds) as Boolean)

    }

    @Test
    fun `isInBox returns false when point is outside the box`() {
        val bounds = LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
        val pointOutside = LatLng(3.0, 3.0)
        val method = MapStateReducer::class.java.getDeclaredMethod(
            "isInBox",
            LatLng::class.java,
            LatLngBounds::class.java
        )
        method.isAccessible = true
        assertFalse(method.invoke(reducer, pointOutside, bounds) as Boolean)
    }

    @Test
    fun `isInBox returns true when bounds are null`() {
        val point = LatLng(1.5, 1.5)
        val method = MapStateReducer::class.java.getDeclaredMethod(
            "isInBox",
            LatLng::class.java,
            LatLngBounds::class.java
        )
        method.isAccessible = true
        assertTrue(method.invoke(reducer, point, null) as Boolean)
    }

    @Test
    fun `isInBox returns true when point is on the edge`() {
        val bounds = LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))
        val point = LatLng(1.0, 1.0)
        val point2 = LatLng(2.0, 2.0)
        val method = MapStateReducer::class.java.getDeclaredMethod(
            "isInBox",
            LatLng::class.java,
            LatLngBounds::class.java
        )
        method.isAccessible = true
        assertTrue(method.invoke(reducer, point, bounds) as Boolean)
        assertTrue(method.invoke(reducer, point2, bounds) as Boolean)
    }

}