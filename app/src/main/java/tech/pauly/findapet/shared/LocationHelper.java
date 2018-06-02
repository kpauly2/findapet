package tech.pauly.findapet.shared;

import android.Manifest;
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
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.dependencyinjection.ForApplication;

@Singleton
public class LocationHelper {

    private Context context;
    private PublishSubject<String> locationSubject = PublishSubject.create();

    @Inject
    public LocationHelper(@ForApplication Context context) {
        this.context = context;
    }

    public Observable<String> getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Observable.error(new IllegalAccessException("Requesting permission without having granted permission to ACCESS_FINE_LOCATION"));
        }

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
        return locationSubject;
    }

    private void updateForNewLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationSubject.onNext(addresses.get(0).getPostalCode());
        } catch (IOException e) {
            if (isEmulator()) {
                locationSubject.onNext("48335");
            } else {
                locationSubject.onError(e);
            }
        }
    }

    public static boolean isEmulator() {
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
