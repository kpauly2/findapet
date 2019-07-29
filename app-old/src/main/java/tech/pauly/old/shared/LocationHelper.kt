package tech.pauly.old.shared

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import tech.pauly.old.data.ObservableHelper
import tech.pauly.old.data.PetfinderException
import tech.pauly.old.data.models.Shelter
import tech.pauly.old.data.models.StatusCode
import tech.pauly.old.dependencyinjection.ForApplication
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val METERS_TO_MILES = 0.000621371192

@Singleton
open class LocationHelper @Inject
constructor(private val observableHelper: ObservableHelper,
            private val locationWrapper: LocationWrapper,
            private val localeWrapper: LocaleWrapper) {

    private val locationSubject = BehaviorSubject.create<Address>()
    var debugLocation = false

    open fun fetchCurrentLocation(): Observable<Address> {
        if (locationWrapper.isEmulator || debugLocation) {
            val address = Address(localeWrapper.getLocale()).apply {
                latitude = 42.465513
                longitude = -83.434321
                postalCode = "48335"
            }
            return Observable.just(address)
        }

        if (locationSubject.value == null) {
            locationWrapper.fetchNewLocation(Criteria.ACCURACY_FINE, ::updateForNewLocation)
        }
        return locationSubject
    }

    open fun getCurrentDistanceToContactInfo(contactInfo: Shelter?): Observable<Int> {
        return locationSubject.map { address -> calculateDistanceBetween(address, contactInfo) }
                .compose(observableHelper.applyObservableSchedulers())
    }

    open fun updateForNewLocation(location: Location) {
        try {
            locationSubject.onNext(locationWrapper.getAddressFromLocation(location))
        } catch (e: IOException) {
            locationSubject.onError(PetfinderException(StatusCode.ERR_FETCH_LOCATION))
        }
    }

    private fun calculateDistanceBetween(userAddress: Address, contactInfo: Shelter?): Int? {
        val distance = -1
        contactInfo ?: return distance
        return try {
            val petAddress = locationWrapper.getAddressFromName(contactInfo.geocodingAddress)
            locationWrapper.locationBetweenAddresses(userAddress, petAddress)
        } catch (e: IOException) {
            e.printStackTrace()
            distance
        }
    }
}

open class LocationWrapper @Inject
constructor(@ForApplication private val context: Context,
            private val localeWrapper: LocaleWrapper) {

    open val isEmulator: Boolean
        get() = isDebug()
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)

    @SuppressLint("MissingPermission")
    open fun fetchNewLocation(accuracy: Int, locationChangedListener: (location: Location) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener(locationChangedListener)
    }

    @Throws(IOException::class)
    open fun getAddressFromLocation(location: Location): Address {
        return Geocoder(context, localeWrapper.getLocale()).getFromLocation(location.latitude, location.longitude, 1)[0]
    }

    open fun getAddressFromName(contactAddressName: String?): Address {
        return Geocoder(context, localeWrapper.getLocale()).getFromLocationName(contactAddressName, 1)[0]
    }

    open fun locationBetweenAddresses(address1: Address, address2: Address): Int {
        val location1 = Location("user").apply { latitude = address1.latitude; longitude = address1.longitude }
        val location2 = Location("pet").apply { latitude = address2.latitude; longitude = address2.longitude }
        return (location1.distanceTo(location2) * METERS_TO_MILES).toInt()
    }
}