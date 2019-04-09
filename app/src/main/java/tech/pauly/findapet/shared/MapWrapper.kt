package tech.pauly.findapet.shared

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import tech.pauly.findapet.R
import tech.pauly.findapet.dependencyinjection.ForApplication
import tech.pauly.findapet.utils.getBitmapForDrawableId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class MapWrapper @Inject
constructor(@ForApplication val context: Context) : OnMapReadyCallback, LifecycleObserver {

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    open val shelterClickSubject = PublishSubject.create<LatLng>()
    open val mapClickSubject = PublishSubject.create<LatLng>()
    open val mapReadyObservable: Observable<Boolean>
        get() = mapReadySubject.delay(100, TimeUnit.MILLISECONDS)

    private val mapReadySubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private lateinit var selectedShelterStandIn: Marker
    private var selectedShelter: Marker? = null
        set(value) {
            field?.isVisible = true
            value?.also {
                it.isVisible = false
                selectedShelterStandIn.position = it.position
                selectedShelterStandIn.isVisible = true
            }
            field = value
        }

    fun setupMap(mapView: MapView) {
        this.mapView = mapView
        mapView.getMapAsync(this)
        mapView.onCreate(null)
        mapView.onResume()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_maps_style))
        map.uiSettings.isMapToolbarEnabled = false
        map.setOnMarkerClickListener(this::onMarkerClick)
        map.setOnMapClickListener(this::onMapClick)
        selectedShelterStandIn = map.addMarker(MarkerOptions()
                .title("Selected Marker Stand-In")
                .visible(false)
                .position(LatLng(0.0, 0.0))
                .icon(context.getBitmapForDrawableId(R.drawable.ic_shelter_selected)))
        mapReadySubject.onNext(true)
    }

    open fun moveCamera(latLng: LatLng, zoomLevel: Float?) {
        map.apply {
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            if (zoomLevel != null) {
                moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
            }
        }
    }

    open fun addShelterMarker(latLng: LatLng) {
        map.addMarker(MarkerOptions()
                .position(latLng)
                .icon(context.getBitmapForDrawableId(R.drawable.ic_shelter)))
    }

    open fun showMyLocation(latLng: LatLng) {
        map.addMarker(MarkerOptions()
                .position(latLng)
                .title("My Location")
                .icon(context.getBitmapForDrawableId(R.drawable.ic_user_location)))
    }

    open fun zoomToFitPoints(points: List<LatLng>) {
        val latLngBounds = LatLngBounds.Builder().also { builder ->
            points.forEach { builder.include(it) }
        }.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 90))
    }

    private fun onMarkerClick(marker: Marker): Boolean {
        return if (marker.title != "My Location" && marker.title != "Selected Marker Stand-In") {
            selectedShelter = marker
            shelterClickSubject.onNext(marker.position)
            false
        } else {
            true
        }
    }

    private fun onMapClick(latLng: LatLng) {
        mapClickSubject.onNext(latLng)
        selectedShelter = null
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