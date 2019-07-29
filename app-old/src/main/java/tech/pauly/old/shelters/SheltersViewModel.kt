package tech.pauly.old.shelters

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.databinding.ObservableBoolean
import android.location.Address
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import tech.pauly.old.R
import tech.pauly.old.data.ObservableHelper
import tech.pauly.old.data.ShelterRepository
import tech.pauly.old.data.models.Shelter
import tech.pauly.old.shared.BaseViewModel
import tech.pauly.old.shared.LocationHelper
import tech.pauly.old.shared.MapWrapper
import tech.pauly.old.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.OptionsMenuEvent
import tech.pauly.old.shared.events.OptionsMenuState
import tech.pauly.old.shared.events.ViewEventBus
import tech.pauly.old.utils.ObservableString
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
                .quickSubscribe { response ->
                    response.shelterList
                            ?.also { this.shelterList = it }
                            ?.map { LatLng(it.latitude!!, it.longitude!!) }
                            ?.also(mapWrapper::zoomToFitPoints)
                            ?.forEach(mapWrapper::addShelterMarker)
                }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeToMapEvents() {
        mapWrapper.shelterClickSubject.quickSubscribe(this::showShelterDetails)
        mapWrapper.mapClickSubject.quickSubscribe(this::hideShelterDetails)
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
