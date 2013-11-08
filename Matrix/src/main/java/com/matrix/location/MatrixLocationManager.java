package com.matrix.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.matrix.db.entity.MatrixLocation;
import com.matrix.utils.L;
import com.matrix.utils.PreferencesManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class MatrixLocationManager implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = MatrixLocationManager.class.getSimpleName();
    private LocationClient locationClient;
    private boolean isConnected;
    private boolean updatesRequested = false;
    private Location lastLocation;
    private Queue<ILocationUpdate> requested;


    /**
     *
     * @param context
     */
    public MatrixLocationManager(Context context) {
        L.d(TAG, "MatrixLocationManager init!");
        requested = new LinkedList<ILocationUpdate>();
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            L.d(TAG, "Google Play services is available.");

            /*
             * Create a new location client, using the enclosing class to
             * handle callbacks.
             */
            locationClient = new LocationClient(context, this, this);
            // Connect the client.
            locationClient.connect();
        } else { // Google Play services was not available for some reason
            L.d(TAG, "Google Play services [ERROR=" + resultCode + "]");
        }
    }

    /**
     * Get Last known location.
     * @return null if not connected to Google Play Service or
     */
    public Location getLocation() {
        L.i(TAG, "getLocation() >>>");
        if (isConnected) {
            this.lastLocation = locationClient.getLastLocation();
        }
        if (lastLocation != null) {
            L.i(TAG, "getLocation[" + lastLocation + "]");
            L.i(TAG, "getLocation[time=" + new Date(lastLocation.getTime()) + "]");
        }
        L.i(TAG, "getLocation() <<<");
        return lastLocation;
    }

    /**
     * Add request to the Queue and wait for update
     * @param listenner
     */
    public void addRequest(ILocationUpdate listenner){
        requested.add(listenner);
    }

    private void notifyAllRequestedLocation() {
        if (lastLocation != null) {
            while (!requested.isEmpty()) {
                requested.poll().onUpdate(lastLocation);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (location != null) {
            L.i(TAG, "onLocationChanged() [ " + location.getLatitude() + ", " + location.getLongitude() + ", " +
                    "Provider: " + location.getProvider() + "]");
            notifyAllRequestedLocation();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        L.i(TAG, "onStatusChanged() [provider= " + provider + ", status=" + status + "]");
    }

    @Override
    public void onProviderEnabled(String provider) {
        L.d(TAG, "onProviderEnabled() [provider= " + provider + "]");
    }

    @Override
    public void onProviderDisabled(String provider) {
        L.d(TAG, "onProviderDisabled() [provider= " + provider + "]");
    }

    @Override
    public void onConnected(Bundle bundle) {
        L.i(TAG, "onConnected() [bundle = " + bundle + "]");
        isConnected = true;
        this.lastLocation = locationClient.getLastLocation();
        if (lastLocation != null) {
            L.i(TAG, "getLocation[" + lastLocation + "]");
            L.i(TAG, "getLocation[time=" + new Date(lastLocation.getTime()) + "]");
        } else {
            L.w(TAG, "location == null");
        }

        notifyAllRequestedLocation();
    }

    @Override
    public void onDisconnected() {
        L.i(TAG, "onDisconnected()");
        isConnected = false;
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

    public interface ILocationUpdate {
        public void onUpdate(Location location);
    }
}
