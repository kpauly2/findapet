package tech.pauly.findapet.shared;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.BuildConfig;
import tech.pauly.findapet.data.ObservableHelper;
import tech.pauly.findapet.data.models.Contact;
import tech.pauly.findapet.dependencyinjection.ForApplication;

@Singleton
public class LocationHelper {

    private final double METERS_TO_MILES = 0.000621371192;
    private static final String RESET = "RESET";

    private Context context;
    private ObservableHelper observableHelper;
    private BehaviorSubject<String> locationSubject = BehaviorSubject.create();
    private Location currentLocation;


    @Inject
    public LocationHelper(@ForApplication Context context, ObservableHelper observableHelper) {
        this.context = context;
        this.observableHelper = observableHelper;
    }

    public Single<Integer> getCurrentDistanceToContactInfo(Contact contactInfo) {
        return Single.fromCallable(() -> calculateCurrentDistanceToContactInfo(contactInfo)).compose(observableHelper.applySingleSchedulers());
    }

    public Observable<String> fetchCurrentLocation(boolean resetLocation) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Observable.error(new IllegalAccessException("Requesting location without having granted permission to ACCESS_FINE_LOCATION"));
        }

        if (BuildConfig.DEBUG && isEmulator()) {
            Location location = new Location("mocked emulator location");
            location.setLatitude(42.459532);
            location.setLongitude(-83.416082);
            currentLocation = location;
            return Observable.just("48335");
        }

        if (resetLocation) {
            fetchNewLocation();
        }
        return locationSubject.filter(s -> !s.equals(RESET));
    }

    @SuppressLint("MissingPermission")
    private void fetchNewLocation() {
        locationSubject.onNext(RESET);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        if (locationManager != null) {
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateForNewLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) { }
            }, null);
        }
    }

    private void updateForNewLocation(Location location) {
        try {
            currentLocation = location;
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationSubject.onNext(addresses.get(0).getPostalCode());
        } catch (IOException e) {
            locationSubject.onError(e);
        }
    }

    private Integer calculateCurrentDistanceToContactInfo(Contact contactInfo) {
        int distance = -1;
        if (currentLocation != null && contactInfo != null) {
            String contactAddressName = "";
            if (contactInfo.getCity() != null && contactInfo.getState() != null) {
                if (contactInfo.getAddress1() != null) {
                    contactAddressName = contactInfo.getAddress1() + " " + contactInfo.getCity() + ", " + contactInfo.getState();
                } else {
                    contactAddressName = contactInfo.getCity() + ", " + contactInfo.getState();
                }
            } else if (contactInfo.getZip() != null) {
                contactAddressName = contactInfo.getZip();
            }
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(contactAddressName, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    Location contactLocation = new Location("contact");
                    contactLocation.setLatitude(address.getLatitude());
                    contactLocation.setLongitude(address.getLongitude());

                    return (int) (currentLocation.distanceTo(contactLocation) * METERS_TO_MILES);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return distance;
            }
        }
        return distance;
    }

    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
               || Build.FINGERPRINT.startsWith("unknown")
               || Build.MODEL.contains("google_sdk")
               || Build.MODEL.contains("Emulator")
               || Build.MODEL.contains("Android SDK built for x86")
               || Build.MANUFACTURER.contains("Genymotion")
               || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
               || "google_sdk".equals(Build.PRODUCT);
    }
}