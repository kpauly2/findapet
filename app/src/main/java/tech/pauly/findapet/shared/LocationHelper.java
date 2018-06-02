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
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.dependencyinjection.ForApplication;

@Singleton
public class LocationHelper {

    private static final String RESET = "RESET";

    private Context context;
    private BehaviorSubject<String> locationSubject = BehaviorSubject.create();

    @Inject
    public LocationHelper(@ForApplication Context context) {
        this.context = context;
    }

    public Single<String> getCurrentLocation(boolean resetLocation) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Single.error(new IllegalAccessException("Requesting permission without having granted permission to ACCESS_FINE_LOCATION"));
        }

        if (isEmulator()) {
            return Single.just("48335");
        }

        if (resetLocation) {
            fetchNewLocation();
        }
        return locationSubject.filter(s -> !s.equals(RESET)).singleOrError();
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
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationSubject.onNext(addresses.get(0).getPostalCode());
        } catch (IOException e) {
            locationSubject.onError(e);
        }
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