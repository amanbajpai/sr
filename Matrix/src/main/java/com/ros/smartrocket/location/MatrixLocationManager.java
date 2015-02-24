package com.ros.smartrocket.location;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.utils.ChinaTransformLocation;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class MatrixLocationManager implements com.google.android.gms.location.LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, BDLocationListener {

    private static final String TAG = MatrixLocationManager.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private Context context;
    private LocationClient locationClient;
    private com.baidu.location.LocationClient baiduLocationClient;
    private boolean isConnected;
    private Location lastLocation;
    private Queue<ILocationUpdate> requested;
    private LocationRequest locationRequest;
    private CurrentLocationUpdateListener currentLocationUpdateListener;

    //private LocationManager locationManager;

    /**
     * @param context - current context
     */
    public MatrixLocationManager(Context context) {
        L.d(TAG, "MatrixLocationManager init!");
        this.context = context;
        requested = new LinkedList<>();

        startLocationClient();
    }

    public void startLocationClient() {
        if (!Config.USE_BAIDU) {
            startGoogleLocationClient();
        } else {
            startBaiduLocationClient();
        }
    }

    public void startGoogleLocationClient() {
        if (locationClient == null || (!locationClient.isConnecting() && !locationClient.isConnected())) {
            L.d(TAG, "startGoogleLocationClient");

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

        }
    }

    public void startBaiduLocationClient() {
        L.d(TAG, "startBaiduLocationClient");

        baiduLocationClient = new com.baidu.location.LocationClient(context);
        baiduLocationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09mc");
        option.setScanSpan((int) Keys.UPDATE_INTERVAL);
        baiduLocationClient.setLocOption(option);
        baiduLocationClient.start();
        baiduLocationClient.requestLocation();

        this.isConnected = true;

        notifyAllRequestedLocation();
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
            if (!Config.USE_BAIDU) {
                if (locationClient != null) {
                    this.lastLocation = locationClient.getLastLocation();
                }
            } else {
                this.lastLocation = convertBaiduLocationToLocation(baiduLocationClient.getLastKnownLocation());
            }
        }

        if (!Config.USE_BAIDU) {
            ChinaTransformLocation.transformFromChinaLocation(lastLocation);
        } else {
            ChinaTransformLocation.transformForBaiduLocation(lastLocation);
        }

        if (lastLocation != null) {
            L.i(TAG, "getLocation() [ " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude()
                    + ", time=" + new Date(lastLocation.getTime()) + "]");

            if (preferencesManager.getUseLocationServices() && lastLocation.getTime()
                    < new Date().getTime() - DateUtils.MINUTE_IN_MILLIS * 2
                    && ((!Config.USE_BAIDU && (locationClient == null || (locationClient != null && !locationClient.isConnected())))
                    || (Config.USE_BAIDU && (baiduLocationClient == null || (baiduLocationClient != null && !baiduLocationClient.isStarted()))))) {
                lastLocation = null;
                startLocationClient();
            }
        }

        return lastLocation;
    }

    /**
     * Send request to get Address from {@link Geocoder}
     *
     * @param location - location to check
     * @param callback - result callback
     */

    public void getAddress(Location location, IAddress callback) {
        (new GetAddressTask(this.context, callback)).execute(location);
    }

    /**
     * Add request to the Queue and wait for update
     *
     * @param listener - result callback
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
        if (!Config.USE_BAIDU) {
            if (locationClient != null) {
                locationClient.disconnect();
            }
        } else {
            if (isConnected) {
                isConnected = false;
                if (baiduLocationClient != null) {
                    baiduLocationClient.stop();
                }
            }
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
    public void onReceiveLocation(BDLocation location) {
        if (location != null) {
            Location tampLocation = convertBaiduLocationToLocation(location);
            ChinaTransformLocation.transformForBaiduLocation(tampLocation);

            lastLocation = tampLocation;

            L.i(TAG, "onReceiveLocation() [ " + tampLocation.getLatitude() + ", " + tampLocation.getLongitude() + "]");

            new RecalculateDistanceAsyncTask().execute(tampLocation);
        }
    }

    @Override
    public void onReceivePoi(BDLocation poiLocation) {
    }


    @Override
    public void onConnected(Bundle bundle) {
        L.i(TAG, "onConnected() [bundle = " + bundle + "]");
        isConnected = true;
        this.lastLocation = locationClient.getLastLocation();

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
     * @param force                   - if true than get location asynchronously, false - block Thread and wait update!
     * @param currentLocationListener - result callback
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
                if (!Config.USE_BAIDU) {
                    if (lm.locationClient.isConnected()) {
                        lm.locationClient.requestLocationUpdates(lm.locationRequest, lm);
                    } else {
                        lm.startGoogleLocationClient();
                    }
                } else {
                    if (lm.baiduLocationClient.isStarted()) {
                        lm.baiduLocationClient.requestLocation();
                    } else {
                        lm.startBaiduLocationClient();
                    }
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

    private static Location convertBaiduLocationToLocation(BDLocation location) {
        Location resultLocation = null;

        if (location != null) {
            resultLocation = new Location(LocationManager.NETWORK_PROVIDER);
            resultLocation.setLatitude(location.getLatitude());
            resultLocation.setLongitude(location.getLongitude());
        }

        return resultLocation;
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
