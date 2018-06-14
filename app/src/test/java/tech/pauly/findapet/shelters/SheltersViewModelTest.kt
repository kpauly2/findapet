package tech.pauly.findapet.shelters

import android.location.Address
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.MapHelper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore

class SheltersViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val locationHelper: LocationHelper = mock()
    private val observableHelper: ObservableHelper = mock()
    private val mapHelper: MapHelper = mock()
    private val locationResponse: Address = mock {
        on { latitude }.thenReturn(10.0)
        on { longitude }.thenReturn(20.0)
    }

    private lateinit var subject: SheltersViewModel

    @Before
    fun setup() {
        whenever(locationHelper.fetchCurrentLocation(true)).thenReturn(Observable.just(locationResponse))
        whenever(mapHelper.mapReadyObservable).thenReturn(Observable.just(true))
        whenever(observableHelper.applyObservableSchedulers<Any>()).thenReturn(ObservableTransformer { it })
        subject = SheltersViewModel(dataStore, locationHelper, observableHelper, mapHelper)
    }

    @Test
    fun updateToolbarTitle_savesToolbarTitle() {
        subject.updateToolbarTitle()

        verify(dataStore).save(DiscoverToolbarTitleUseCase(R.string.menu_shelters))
    }

    @Test
    fun fetchDataAndMoveMap_whenOnlyMapReadyReturns_doNothing() {
        whenever(locationHelper.fetchCurrentLocation(true)).thenReturn(Observable.never())

        subject.fetchDataAndMoveMap()

        verify(mapHelper, never()).addMarker(any(), any())
    }

    @Test
    fun fetchDataAndMoveMap_whenOnlyCurrentLocationReturns_doNothing() {
        whenever(mapHelper.mapReadyObservable).thenReturn(Observable.never())

        subject.fetchDataAndMoveMap()
        locationHelper.fetchCurrentLocation(true).test().onNext(mock())

        verify(mapHelper, never()).addMarker(any(), any())
    }

    @Test
    fun fetchDataAndMoveMap_getLocationAndMapReady_addMarkerAndMoveCameraToLocation() {
        subject.fetchDataAndMoveMap()

        verify(mapHelper).addMarker(10.0, 20.0)
        verify(mapHelper).moveCamera(10.0, 20.0, 13f)
    }
}