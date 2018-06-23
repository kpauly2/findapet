package tech.pauly.findapet.shelters

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.data.ShelterRepository
import tech.pauly.findapet.data.models.Shelter
import tech.pauly.findapet.data.models.ShelterListResponse
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.MapWrapper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore

class SheltersViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val locationHelper: LocationHelper = mock()
    private val observableHelper: ObservableHelper = mock()
    private val mapWrapper: MapWrapper = mock()
    private val shelterRepository: ShelterRepository = mock()

    private val locationResponse: Address = mock {
        on { latitude }.thenReturn(10.0)
        on { longitude }.thenReturn(20.0)
        on { postalCode }.thenReturn("zipcode")
    }
    private val shelterListResponse: ShelterListResponse = mock()
    private lateinit var subject: SheltersViewModel

    @Before
    fun setup() {
        whenever(locationHelper.fetchCurrentLocation()).thenReturn(Observable.just(locationResponse))
        whenever(mapWrapper.mapReadyObservable).thenReturn(Observable.just(true))
        whenever(observableHelper.applyObservableSchedulers<Any>()).thenReturn(ObservableTransformer { it })
        whenever(shelterRepository.fetchShelters(any())).thenReturn(Observable.just(shelterListResponse))
        val shelter1: Shelter = mock {
            on { id }.thenReturn("id1")
            on { latitude }.thenReturn(10.1)
            on { longitude }.thenReturn(20.1)
        }
        val shelter2: Shelter = mock {
            on { id }.thenReturn("id2")
            on { latitude }.thenReturn(10.2)
            on { longitude }.thenReturn(20.2)
        }
        whenever(shelterListResponse.shelterList).thenReturn(listOf(shelter1, shelter2))
        subject = SheltersViewModel(dataStore, locationHelper, observableHelper, mapWrapper, shelterRepository)
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

        verify(mapWrapper, never()).showMyLocation(any())
    }

    @Test
    fun fetchDataAndMoveMap_whenOnlyCurrentLocationReturns_doNothing() {
        whenever(mapWrapper.mapReadyObservable).thenReturn(Observable.never())

        subject.fetchDataAndMoveMap()
        locationHelper.fetchCurrentLocation().test().onNext(mock())

        verify(mapWrapper, never()).showMyLocation(any())
    }

    @Test
    fun fetchDataAndMoveMap_getLocationAndMapReady_addMarkerAndMoveCameraToLocationAndFetchShelters() {
        subject.fetchDataAndMoveMap()

        verify(mapWrapper).showMyLocation(LatLng(10.0, 20.0))
        verify(mapWrapper).moveCamera(LatLng(10.0, 20.0), 13f)
        verify(shelterRepository).fetchShelters("zipcode")
    }

    @Test
    fun fetchDataAndMoveMap_getShelterList_addMarkersAndZoomToShelterPositions() {
        subject.fetchDataAndMoveMap()

        verify(mapWrapper).zoomToFitPoints(listOf(LatLng(10.1, 20.1), LatLng(10.2, 20.2)))
        verify(mapWrapper).addShelterMarker(LatLng(10.1, 20.1))
        verify(mapWrapper).addShelterMarker(LatLng(10.2, 20.2))
    }
}