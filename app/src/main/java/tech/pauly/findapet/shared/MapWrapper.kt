package tech.pauly.findapet.shared

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.DrawableRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
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
        map.uiSettings.isMapToolbarEnabled = false
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

    @SuppressLint("MissingPermission")
    open fun showMyLocation(latLng: LatLng) {
        map.addMarker(MarkerOptions()
                .position(latLng)
                .icon(context.getBitmapForDrawableId(R.drawable.ic_user_location)))
    }

    open fun zoomToFitPoints(points: List<LatLng>) {
        val latLngBounds = LatLngBounds.Builder().also { builder ->
            points.forEach { builder.include(it) }
        }.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 90))
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

private fun Context.getBitmapForDrawableId(@DrawableRes drawableId: Int): BitmapDescriptor {
    val drawable = this.getDrawable(drawableId)
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}