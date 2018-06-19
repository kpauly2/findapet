package tech.pauly.findapet.shelters

import android.location.Address
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.MapWrapper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import java.util.concurrent.TimeUnit

class SheltersViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val locationHelper: LocationHelper = mock()
    private val observableHelper: ObservableHelper = mock()
    private val mapWrapper: MapWrapper = mock()
    private val locationResponse: Address = mock {
        on { latitude }.thenReturn(10.0)
        on { longitude }.thenReturn(20.0)
    }

    private lateinit var subject: SheltersViewModel

    @Before
    fun setup() {
        whenever(locationHelper.fetchCurrentLocation()).thenReturn(Observable.just(locationResponse))
        whenever(mapWrapper.mapReadyObservable).thenReturn(Observable.just(true))
        whenever(observableHelper.applyObservableSchedulers<Any>()).thenReturn(ObservableTransformer { it })
        subject = SheltersViewModel(dataStore, locationHelper, observableHelper, mapWrapper)
    }

    @Test
    fun updateToolbarTitle_savesToolbarTitle() {
        subject.updateToolbarTitle()

        verify(dataStore) += DiscoverToolbarTitleUseCase(R.string.menu_shelters)
    }

    @Test
    fun fetchDataAndMoveMap_whenOnlyMapReadyReturns_doNothing() {
        whenever(locationHelper.fetchCurrentLocation()).thenReturn(Observable.never())

        subject.fetchDataAndMoveMap()

        verify(mapWrapper, never()).addMarker(any(), any())
    }

    @Test
    fun fetchDataAndMoveMap_whenOnlyCurrentLocationReturns_doNothing() {
        whenever(mapWrapper.mapReadyObservable).thenReturn(Observable.never())

        subject.fetchDataAndMoveMap()
        locationHelper.fetchCurrentLocation().test().onNext(mock())

        verify(mapWrapper, never()).addMarker(any(), any())
    }

    @Test
    fun fetchDataAndMoveMap_getLocationAndMapReady_addMarkerAndMoveCameraToLocation() {
        subject.fetchDataAndMoveMap()

        verify(mapWrapper).addMarker(10.0, 20.0)
        verify(mapWrapper).moveCamera(10.0, 20.0, 13f)
    }
}