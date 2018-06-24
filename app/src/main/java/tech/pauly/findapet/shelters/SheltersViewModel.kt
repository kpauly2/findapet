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
import javax.inject.Inject

class SheltersViewModel @Inject
constructor(private val dataStore: TransientDataStore,
            private val locationHelper: LocationHelper,
            private val observableHelper: ObservableHelper,
            private val mapWrapper: MapWrapper,
            private val shelterRepository: ShelterRepository) : BaseViewModel() {

    var shelterDetailsVisibility = ObservableBoolean(false)
    var selectedShelterName = ObservableField<String>("")
    var selectedShelterAddress = ObservableField<String>("")
    var shelterList: List<Shelter>? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbarTitle() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_shelters)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchDataAndMoveMap() {
        Observable.zip(locationHelper.fetchCurrentLocation(), mapWrapper.mapReadyObservable,
                BiFunction { showMyLocation: Address, _: Boolean -> showMyLocation })
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
                            ?.map { LatLng(it.latitude, it.longitude) }
                            ?.also(mapWrapper::zoomToFitPoints)
                            ?.forEach(mapWrapper::addShelterMarker)
                }, Throwable::printStackTrace)
                .onLifecycle()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
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
            selectedShelterAddress.set(formattedAddress())
            shelterDetailsVisibility.set(true)
        }
    }

    private fun hideShelterDetails(latLng: LatLng) {
        shelterDetailsVisibility.set(false)
    }

    private fun shelterForLatLng(latLng: LatLng): Shelter? {
        return shelterList?.find { it.latitude == latLng.latitude && it.longitude == latLng.longitude }
    }

    private fun Shelter.formattedAddress(): String {
        return "${address1?.let { "$it${address2?.let { " $it" } ?: ""}\n" } ?: ""}$city, $state $zip"
    }
}
