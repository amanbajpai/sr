package com.matrix.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.matrix.Config;
import com.matrix.db.entity.MatrixLocation;
import com.matrix.utils.L;
import com.matrix.utils.PreferencesManager;

public class LocationService extends Service implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private LocationManager locationManager;
    private LocationClient locationClient;

    // Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    public void onCreate() {
        L.i(TAG, "onCreate()");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            L.d(TAG, "Google Play services is available.");

            /*
             * Create a new location client, using the enclosing class to
             * handle callbacks.
             */
            //locationClient = new LocationClient(getBaseContext(), this, this);
        } else { // Google Play services was not available for some reason
            L.d(TAG, "Google Play services [ERROR=" + resultCode + "]");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        saveLastKnownLocation();
        
        String provider = getBestProvider(locationManager);
        locationManager.requestLocationUpdates(provider, Config.REFRESH_LOCATION_TIME, Config.REFRESH_LOCATION_DISTANCE, this);
        L.i(TAG, "onStartCommand() [refresh time = " + Config.REFRESH_LOCATION_TIME + "]");
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

    @Override
    public void onConnected(Bundle bundle) {
        L.i(TAG, "onConnected() [bundle = " + bundle + "]");
    }

    @Override
    public void onDisconnected() {
        L.i(TAG, "onDisconnected()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {

        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            // TODO: showErrorDialog(connectionResult.getErrorCode());
        }
    }
}
