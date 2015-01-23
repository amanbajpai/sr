package com.ros.smartrocket.location;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.utils.ChinaTransformLocation;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class MatrixLocationManager implements com.google.android.gms.location.LocationListener, android.location.LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = MatrixLocationManager.class.getSimpleName();
    private Context context;
    private LocationClient locationClient;
    private boolean isConnected;
    private Location lastLocation;
    private Queue<ILocationUpdate> requested;
    private LocationRequest locationRequest;
    private CurrentLocationUpdateListener currentLocationUpdateListener;

    private LocationManager locationManager;

    /**
     * @param context - current context
     */
    public MatrixLocationManager(Context context) {
        L.d(TAG, "MatrixLocationManager init!");
        this.context = context;
        requested = new LinkedList<ILocationUpdate>();

        // If Google Play services is available
        if (isGooglePlayServicesAvailable() && !Config.USE_BAIDU) {
            L.d(TAG, "Google Play services is available.");

            // Create the LocationRequest object
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(Keys.UPDATE_INTERVAL);
            locationRequest.setFastestInterval(Keys.FASTEST_INTERVAL);

            /*
             * Create a new location client, using the enclosing class to
             * handle callbacks.
             */
            locationClient = new LocationClient(context, this, this);
            // Connect the client.
            locationClient.connect();
        } else { // Google Play services was not available for some reason
            L.d(TAG, "Google Play services ERROR");

            startLocationManager();
        }
    }

    public boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return ConnectionResult.SUCCESS == resultCode;
        //return false;
    }

    public void startLocationManager() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        isConnected = true;

        this.lastLocation = getLastLocation();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Keys.UPDATE_INTERVAL, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Keys.UPDATE_INTERVAL, 0, this);

        if (lastLocation == null) {
            Calendar calendar = Calendar.getInstance();
            long startTime = calendar.getTimeInMillis();
            long currentTime = calendar.getTimeInMillis();
            while (lastLocation == null && currentTime - startTime < 3000) {
                this.lastLocation = getLastLocation();
                currentTime = calendar.getTimeInMillis();
            }
        }

        if (lastLocation != null) {
            L.i(TAG, "getLocation[" + lastLocation + "]");
            L.i(TAG, "getLocation[time=" + new Date(lastLocation.getTime()) + "]");
        } else {
            L.w(TAG, "location == null");
        }

        notifyAllRequestedLocation();
    }

    public Location getLastLocation() {
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation == null) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return lastLocation;
    }

    public void setCurrentLocationUpdateListener(CurrentLocationUpdateListener currentLocationUpdateListener) {
        this.currentLocationUpdateListener = currentLocationUpdateListener;
    }

    /**
     * Get Last known location.
     *
     * @return null if not connected to Google Play Service or
     */
    public Location getLocation() {
        if (isConnected) {
            if (isGooglePlayServicesAvailable() && !Config.USE_BAIDU) {
                if (locationClient != null) {
                    this.lastLocation = locationClient.getLastLocation();
                }
            } else {
                this.lastLocation = getLastLocation();
            }
        }
        if (lastLocation != null) {
            L.i(TAG, "getLocation[" + lastLocation + ", time=" + new Date(lastLocation.getTime()) + "]");
        }

        if (!Config.USE_BAIDU) {
            ChinaTransformLocation.transformFromChinaLocation(lastLocation);
        }
        return lastLocation;
    }

    /**
     * Send request to get Address from {@link Geocoder}
     *
     * @param location
     * @param callback
     */
    public void getAddress(Location location, IAddress callback) {
        //if (!Config.USE_BAIDU) {
        (new GetAddressTask(this.context, callback)).execute(location);
        /*} else {
            callback.onUpdate(null);
        }*/
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
            if (currentLocationUpdateListener != null) {
                currentLocationUpdateListener.onUpdate(lastLocation);
            }
            while (!requested.isEmpty()) {
                requested.poll().onUpdate(lastLocation);
            }
        }
    }

    public void disconnect() {
        if (locationClient != null) {
            locationClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (location != null) {
            L.i(TAG, "onLocationChanged() [ " + location.getLatitude() + ", " + location.getLongitude() + ", "
                    + "Provider: " + location.getProvider() + "]");

            new RecalculateDistanceAsyncTask().execute(location);
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
            try {
                locationClient.removeLocationUpdates(this);
            } catch (Exception e) {
                L.e(TAG, "RemoveLocationUpdates error" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        L.i(TAG, "onProviderDisabled");

        if (isConnected) {
            isConnected = false;
            try {
                if (locationManager != null) {
                    locationManager.removeUpdates(this);
                }
            } catch (Exception e) {
                L.e(TAG, "RemoveLocationUpdates error" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        L.i(TAG, "onProviderEnabled [provider = " + provider + "]");

        startLocationManager();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        L.i(TAG, "onStatusChanged");
    }

    public boolean isConnected() {
        return isConnected;
    }

    public class RecalculateDistanceAsyncTask extends AsyncTask<Location, Void, Void> {

        @Override
        protected Void doInBackground(Location... params) {
            Location location = params[0];
            TasksBL.recalculateTasksDistance(location);
            return null;
        }

        @Override
        protected void onPostExecute(Void noResult) {
            notifyAllRequestedLocation();
        }
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

        @Override
        protected Address doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(сontext, Locale.ENGLISH);
            Location loc = params[0];

            return geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude());
        }

        @Override
        protected void onPostExecute(Address address) {
            /*if (address != null && "Hong Kong".equals(address.getLocality())
                    && ChinaTransformLocation.outOfChina(address.getLatitude(), address.getLongitude())) {
                address.setCountryName("Hong Kong");
            }*/
            this.callback.onUpdate(address);
        }
    }

    /**
     * Retrieve current location from {@link MatrixLocationManager}
     *
     * @param force                   If true than get location asynchronously, false - block Thread and wait update!
     * @param currentLocationListener
     */
    public static void getCurrentLocation(final boolean force, final GetCurrentLocationListener
            currentLocationListener) {
        MatrixLocationManager lm = App.getInstance().getLocationManager();

        currentLocationListener.getLocationStart();

        Location location = lm.getLocation();
        if (location != null && !force) {
            currentLocationListener.getLocationSuccess(location);
        } else {
            currentLocationListener.getLocationInProcess();
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    currentLocationListener.getLocationSuccess(location);
                }
            });
            if (lm.isConnected()) {
                if (lm.isGooglePlayServicesAvailable() && !Config.USE_BAIDU) {
                    lm.locationClient.requestLocationUpdates(lm.locationRequest, lm);
                } else {
                    lm.startLocationManager();
                }
            }

            lm.notifyAllRequestedLocation();
        }
    }

    public static void getAddressByCurrentLocation(final boolean force, final GetAddressListener getAddressListener) {
        MatrixLocationManager.getCurrentLocation(force, new MatrixLocationManager.GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
            }

            @Override
            public void getLocationInProcess() {
            }

            @Override
            public void getLocationSuccess(Location location) {
                getAddressByLocation(location, getAddressListener);
            }

            @Override
            public void getLocationFail(String errorText) {
                UIUtils.showSimpleToast(App.getInstance(), errorText);
            }
        });
    }

    public static void getAddressByLocation(final Location location, final GetAddressListener getAddressListener) {
        MatrixLocationManager lm = App.getInstance().getLocationManager();
        lm.getAddress(location, new MatrixLocationManager.IAddress() {
            @Override
            public void onUpdate(Address address) {
                String countryName = "";
                String cityName = "";
                String districtName = "";

                if (address != null) {
                    countryName = !TextUtils.isEmpty(address.getCountryName()) ? address.getCountryName() : "";
                    cityName = !TextUtils.isEmpty(address.getLocality()) ? address.getLocality() : "";
                    districtName = !TextUtils.isEmpty(address.getSubLocality()) ? address.getSubLocality() : "";
                }

                getAddressListener.onGetAddressSuccess(location, countryName, cityName, districtName);
            }
        });
    }

    public interface ILocationUpdate {
        void onUpdate(Location location);
    }

    public interface CurrentLocationUpdateListener {
        void onUpdate(Location location);
    }

    public interface IAddress {
        void onUpdate(Address address);
    }

    public interface GetAddressListener {
        void onGetAddressSuccess(Location location, String countryName, String cityName, String districtName);
    }

    public interface GetCurrentLocationListener {
        void getLocationStart();

        void getLocationInProcess();

        void getLocationSuccess(Location location);

        void getLocationFail(String errorText);
    }
}
