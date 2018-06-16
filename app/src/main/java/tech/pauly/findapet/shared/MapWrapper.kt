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
import javax.inject.Inject
import javax.inject.Singleton

// United States zoom and corners
private const val defaultZoom = 2f
private val defaultNeLatLng = LatLng(49.38, -66.94)
private val defaultSwLatLng = LatLng(25.82, -124.39)

@Singleton
open class MapWrapper @Inject constructor(@ForApplication val context: Context) : OnMapReadyCallback, LifecycleObserver {
    private lateinit var mapView: MapView

    private val defaultLatLngBounds = LatLngBounds(defaultSwLatLng, defaultNeLatLng)

    private lateinit var map: GoogleMap

    private val mapReadySubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    open val mapReadyObservable: Observable<Boolean>
        get() = mapReadySubject

    fun setupMap(mapView: MapView) {
        this.mapView = mapView
        mapView.getMapAsync(this)
        mapView.onCreate(null)
        mapView.onResume()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_maps_style))
            moveCamera(CameraUpdateFactory.newLatLngBounds(defaultLatLngBounds, 0))
            moveCamera(CameraUpdateFactory.zoomTo(defaultZoom))
        }
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