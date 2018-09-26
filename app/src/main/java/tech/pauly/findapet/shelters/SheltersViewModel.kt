package tech.pauly.findapet.shelters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.location.Address
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import tech.pauly.findapet.R
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.data.ShelterRepository
import tech.pauly.findapet.data.models.Shelter
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.MapWrapper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.utils.ObservableString
import javax.inject.Inject

class SheltersViewModel @Inject
constructor(private val dataStore: TransientDataStore,
            private val locationHelper: LocationHelper,
            private val observableHelper: ObservableHelper,
            private val mapWrapper: MapWrapper,
            private val shelterRepository: ShelterRepository,
            private val eventBus: ViewEventBus) : BaseViewModel() {

    var shelterDetailsVisibility = ObservableBoolean(false)
    var selectedShelterName = ObservableString("")
    var selectedShelterAddress = ObservableString("")
    private var shelterList: List<Shelter>? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbar() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_shelters)
        eventBus += OptionsMenuEvent(this, OptionsMenuState.EMPTY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchDataAndMoveMap() {
        Observable.zip(locationHelper.fetchCurrentLocation(), mapWrapper.mapReadyObservable,
                BiFunction { location: Address, _: Boolean -> location })
                .compose(observableHelper.applyObservableSchedulers())
                .flatMap {
                    val latLng = LatLng(it.latitude, it.longitude)
                    mapWrapper.showMyLocation(latLng)
                    mapWrapper.moveCamera(latLng, 13f)
                    shelterRepository.fetchShelters(it.postalCode)
                }
                .subscribe({
                    it.shelterList
                            ?.also { this.shelterList = it }
                            ?.map { LatLng(it.latitude!!, it.longitude!!) }
                            ?.also(mapWrapper::zoomToFitPoints)
                            ?.forEach(mapWrapper::addShelterMarker)
                }, Throwable::printStackTrace)
                .onLifecycle()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeToMapEvents() {
        mapWrapper.shelterClickSubject
                .subscribe(this::showShelterDetails, Throwable::printStackTrace)
                .onLifecycle()
        mapWrapper.mapClickSubject
                .subscribe(this::hideShelterDetails, Throwable::printStackTrace)
                .onLifecycle()
    }

    private fun showShelterDetails(latLng: LatLng) {
        shelterForLatLng(latLng)?.run {
            selectedShelterName.set(name)
            selectedShelterAddress.set(formattedAddress)
            shelterDetailsVisibility.set(true)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun hideShelterDetails(latLng: LatLng) {
        shelterDetailsVisibility.set(false)
    }

    private fun shelterForLatLng(latLng: LatLng): Shelter? {
        return shelterList?.find { it.latitude == latLng.latitude && it.longitude == latLng.longitude }
    }
}
