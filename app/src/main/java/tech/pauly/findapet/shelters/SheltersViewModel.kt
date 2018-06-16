package tech.pauly.findapet.shelters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.location.Address
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import tech.pauly.findapet.R
import tech.pauly.findapet.data.ObservableHelper
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.MapWrapper
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SheltersViewModel @Inject
constructor(private val dataStore: TransientDataStore,
            private val locationHelper: LocationHelper,
            private val observableHelper: ObservableHelper,
            private val mapWrapper: MapWrapper) : BaseViewModel() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbarTitle() {
        dataStore.save(DiscoverToolbarTitleUseCase(R.string.menu_shelters))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchDataAndMoveMap() {
        subscribeOnLifecycle(Observable.zip(locationHelper.fetchCurrentLocation(), mapWrapper.mapReadyObservable,
                BiFunction { location: Address, _: Boolean -> location })
                .compose(observableHelper.applyObservableSchedulers())
                .subscribe({
                    mapWrapper.addMarker(it.latitude, it.longitude)
                    mapWrapper.moveCamera(it.latitude, it.longitude, 13f)
                }, Throwable::printStackTrace))
    }
}
