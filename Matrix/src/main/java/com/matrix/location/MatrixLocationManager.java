package com.matrix.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.matrix.Keys;
import com.matrix.utils.L;

import java.io.IOException;
import java.util.*;

public class MatrixLocationManager implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = MatrixLocationManager.class.getSimpleName();
    private Context context;
    private LocationClient locationClient;
    private boolean isConnected;
    private Location lastLocation;
    private Queue<ILocationUpdate> requested;
    private LocationRequest locationRequest;


    /**
     * @param context
     */
    public MatrixLocationManager(Context context) {
        L.d(TAG, "MatrixLocationManager init!");
        this.context = context;
        requested = new LinkedList<ILocationUpdate>();
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            L.d(TAG, "Google Play services is available.");

            // Create the LocationRequest object
            locationRequest = LocationRequest.create();
            // Use high accuracy
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            // Set the update interval to 10 seconds
            locationRequest.setInterval(Keys.UPDATE_INTERVAL);
            // Set the fastest update interval to 1 second
            locationRequest.setFastestInterval(Keys.FASTEST_INTERVAL);

            /*
             * Create a new location client, using the enclosing class to
             * handle callbacks.
             */
            locationClient = new LocationClient(context, this, this);
            // Connect the client.
            locationClient.connect();
        } else { // Google Play services was not available for some reason
            L.d(TAG, "Google Play services [ERROR=" + resultCode + "]");
            //TODO Implement handle logic when Google PLay Services not instaled
        }
    }

    /**
     * Get Last known location.
     *
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
     * Send request to get Address from {@link Geocoder}
     *
     * @param location
     * @param callback
     */
    public void getAddress(Location location, IAddress callback) {
        (new GetAddressTask(this.context, callback)).execute(location);
    }

    /**
     * Add request to the Queue and wait for update
     *
     * @param listener
     */
    public void getLocationAsync(ILocationUpdate listener) {
        requested.add(listener);
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
            L.i(TAG, "onLocationChanged() [ " + location.getLatitude() + ", " + location.getLongitude() + ", Provider: " + location.getProvider() + "]");
            notifyAllRequestedLocation();
        }
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

        locationClient.requestLocationUpdates(locationRequest, this);

        notifyAllRequestedLocation();
    }

    @Override
    public void onDisconnected() {
        L.i(TAG, "onDisconnected()");
        if (isConnected) {
            isConnected = false;
            locationClient.removeLocationUpdates(this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
//        if (connectionResult.hasResolution()) {
//
//        } else {
//            /*
//             * If no resolution is available, display a dialog to the
//             * user with the error.
//             */
//            // TODO: showErrorDialog(connectionResult.getErrorCode());
//        }
    }

    /**
     * A subclass of AsyncTask that calls getFromLocation() in the
     * background. The class definition has these generic types:
     * Location - A Location object containing
     * the current location.
     * Void     - indicates that progress units are not used
     * String   - An address passed to onPostExecute()
     */
    public class GetAddressTask extends AsyncTask<Location, Void, Address> {
        private Context сontext;
        private IAddress callback;

        public GetAddressTask(Context context, IAddress callback) {
            super();
            this.сontext = context;
            this.callback = callback;
        }


        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         * @params params One or more Location objects
         */
        @Override
        protected Address doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(сontext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e1) {
                L.e(TAG, "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return null;
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " + Double.toString(loc.getLatitude()) +
                        " , " + Double.toString(loc.getLongitude()) + " passed to address service";
                L.e(TAG, errorString);
                e2.printStackTrace();
                return null;
            }
            Address address = null;
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                address = addresses.get(0);
            }
            return address;
        }

        @Override
        protected void onPostExecute(Address address) {
            this.callback.onUpdate(address);
        }
    }

    public interface ILocationUpdate {
        public void onUpdate(Location location);
    }

    public interface IAddress {
        public void onUpdate(Address address);
    }
}
