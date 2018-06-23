package tech.pauly.findapet.shelters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.location.Address
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import tech.pauly.findapet.R
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.data.ShelterRepository
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
                            ?.map { LatLng(it.latitude, it.longitude) }
                            ?.also(mapWrapper::zoomToFitPoints)
                            ?.forEach(mapWrapper::addShelterMarker)
                }, Throwable::printStackTrace)
                .onLifecycle()
    }
}
