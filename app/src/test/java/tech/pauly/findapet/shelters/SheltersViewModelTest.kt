package tech.pauly.findapet.shelters

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
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
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus

class SheltersViewModelTest {

    private val dataStore: TransientDataStore = mock()
    private val locationHelper: LocationHelper = mock()
    private val observableHelper: ObservableHelper = mock()
    private val mapWrapper: MapWrapper = mock()
    private val shelterRepository: ShelterRepository = mock()
    private val eventBus: ViewEventBus = mock()

    private val locationResponse: Address = mock {
        on { latitude }.thenReturn(10.0)
        on { longitude }.thenReturn(20.0)
        on { postalCode }.thenReturn("zipcode")
    }
    private val shelter1: Shelter = mock {
        on { id }.thenReturn("id1")
        on { name }.thenReturn("Shelter 1")
        on { latitude }.thenReturn(10.1)
        on { longitude }.thenReturn(20.1)
    }
    private val shelter2: Shelter = mock {
        on { id }.thenReturn("id2")
        on { name }.thenReturn("Shelter 2")
        on { latitude }.thenReturn(10.2)
        on { longitude }.thenReturn(20.2)
    }
    private val shelterListResponse: ShelterListResponse = mock()
    private val shelterClickSubject = PublishSubject.create<LatLng>()
    private val mapClickSubject = PublishSubject.create<LatLng>()
    private lateinit var subject: SheltersViewModel

    @Before
    fun setup() {
        whenever(locationHelper.fetchCurrentLocation()).thenReturn(Observable.just(locationResponse))
        whenever(mapWrapper.mapReadyObservable).thenReturn(Observable.just(true))
        whenever(observableHelper.applyObservableSchedulers<Any>()).thenReturn(ObservableTransformer { it })
        whenever(shelterRepository.fetchShelters(any())).thenReturn(Observable.just(shelterListResponse))

        whenever(shelterListResponse.shelterList).thenReturn(listOf(shelter1, shelter2))
        whenever(mapWrapper.shelterClickSubject).thenReturn(shelterClickSubject)
        whenever(mapWrapper.mapClickSubject).thenReturn(mapClickSubject)

        subject = SheltersViewModel(dataStore, locationHelper, observableHelper, mapWrapper, shelterRepository, eventBus)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify(dataStore) += DiscoverToolbarTitleUseCase(R.string.menu_shelters)
        verify(eventBus) += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
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

    @Test
    fun shelterClicked_invalidShelter_doNotShowShelterDetails() {
        subject.fetchDataAndMoveMap()
        subject.subscribeToMapEvents()

        shelterClickSubject.onNext(LatLng(0.0, 0.0))

        assertThat(subject.shelterDetailsVisibility.get()).isFalse()
    }

    @Test
    fun shelterClicked_validShelter_showShelterDetails() {
        subject.fetchDataAndMoveMap()
        subject.subscribeToMapEvents()

        shelterClickSubject.onNext(LatLng(10.1, 20.1))

        assertThat(subject.shelterDetailsVisibility.get()).isTrue()
        assertThat(subject.selectedShelterName.get()).isEqualTo("Shelter 1")
    }

    @Test
    fun clickMap_hidesShelterDetails() {
        subject.fetchDataAndMoveMap()
        subject.subscribeToMapEvents()

        mapClickSubject.onNext(LatLng(10.1, 20.1))

        assertThat(subject.shelterDetailsVisibility.get()).isFalse()
    }
}