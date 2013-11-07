package com.matrix.location;

import com.matrix.Config;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.matrix.db.entity.MatrixLocation;
import com.matrix.utils.L;
import com.matrix.utils.PreferencesManager;

public class LocationService extends Service implements LocationListener {
    private static final String TAG = LocationService.class.getSimpleName();
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        saveLastKnownLocation();
        
        String provider = getBestProvider(locationManager);
        locationManager.requestLocationUpdates(provider, Config.REFRESH_LOCATION_TIME, Config.REFRESH_LOCATION_DISTANCE, this);
        
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            L.i(TAG, "onLocationChanged() [ " + location.getLatitude() + ", " + location.getLongitude() + ", " +
                    "Provider: " + location.getProvider() + "]");

            /* Save actual location here */
            PreferencesManager.getInstance().setCurrentLocation(new MatrixLocation(location));
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {
        locationManager.requestLocationUpdates(provider, Config.REFRESH_LOCATION_TIME, Config.REFRESH_LOCATION_DISTANCE, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void saveLastKnownLocation() {
        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long gpsTime = 0;
        if (gpsLocation != null) {
            gpsTime = gpsLocation.getTime();
        }

        long networkTime = 0;
        if (networkLocation != null) {
            networkTime = networkLocation.getTime();
        }

        /* Select last get location and save it */
        if ((gpsTime - networkTime) > 0) {
            PreferencesManager.getInstance().setCurrentLocation(new MatrixLocation(gpsLocation));
        } else {
            PreferencesManager.getInstance().setCurrentLocation(new MatrixLocation(networkLocation));
        }
    }

    private String getBestProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // criteria.setCostAllowed(false);
        return locationManager.getBestProvider(criteria, true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationService.this);
        }
        super.onDestroy();
    }
}
