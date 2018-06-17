package tech.pauly.findapet.shared

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import tech.pauly.findapet.R
import tech.pauly.findapet.dependencyinjection.ForApplication
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class MapWrapper @Inject constructor(@ForApplication val context: Context) : OnMapReadyCallback, LifecycleObserver {

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    private val mapReadySubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    open val mapReadyObservable: Observable<Boolean>
        get() = mapReadySubject.delay(100, TimeUnit.MILLISECONDS)

    fun setupMap(mapView: MapView) {
        this.mapView = mapView
        mapView.getMapAsync(this)
        mapView.onCreate(null)
        mapView.onResume()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_maps_style))
        mapReadySubject.onNext(true)
    }

    open fun moveCamera(latitude: Double, longitude: Double, zoomLevel: Float?) {
        val latLng = LatLng(latitude, longitude)
        map.apply {
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            if (zoomLevel != null) {
                moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
            }
        }
    }

    open fun addMarker(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        map.addMarker(MarkerOptions().position(latLng))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        mapView.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mapView.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mapView.onDestroy()
    }
}