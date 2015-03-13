package com.ros.smartrocket.location;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
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
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.utils.ChinaTransformLocation;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class MatrixLocationManager implements com.google.android.gms.location.LocationListener,
        android.location.LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, BDLocationListener {

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
    private AsyncQueryHandler handler;
    private LocationManager locationManager;

    /**
     * @param context - current context
     */
    public MatrixLocationManager(Context context) {
        L.d(TAG, "MatrixLocationManager init!");
        this.context = context;
        requested = new LinkedList<>();
        handler = new DbHandler(context.getContentResolver());

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
        option.setCoorType("wgs84");
        option.setScanSpan((int) Keys.UPDATE_INTERVAL);
        baiduLocationClient.setLocOption(option);
        baiduLocationClient.start();
        baiduLocationClient.requestLocation();

        this.isConnected = true;

        notifyAllRequestedLocation();
    }

    public void startGpsLocationClient() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Keys.UPDATE_INTERVAL, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Keys.UPDATE_INTERVAL, 0, this);

        isConnected = true;

        notifyAllRequestedLocation();
    }

    /**
     * GOOGLE location listeners
     */

    @Override
    public void onConnected(Bundle bundle) {
        L.i(TAG, "onConnected() [bundle = " + bundle + "]");
        isConnected = true;

        try {
            this.lastLocation = locationClient.getLastLocation();
            locationClient.requestLocationUpdates(locationRequest, this);
        } catch (Exception e) {
            L.e(TAG, "onConnected. locationClient error" + e.getMessage(), e);
        }
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


    /**
     * NOT GOOGLE location listeners
     */

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        L.i(TAG, "onStatusChanged: status=" + status + ", extras=" + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        L.i(TAG, "onProviderEnabled [provider = " + provider + "]");

        startLocationClient();
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
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (location != null) {
            L.i(TAG, "onLocationChanged() [ " + location.getLatitude() + ", " + location.getLongitude() + ", "
                    + "Provider: " + location.getProvider() + "]");

            TasksBL.getTasksFromDB(handler);
        }
    }


    /**
     * BAIDU location listeners
     */

    @Override
    public void onReceiveLocation(BDLocation baiduLocation) {
        if (baiduLocation != null) {
            L.i(TAG, "onReceiveLocation() Location from Baidu location services [ " + baiduLocation.getLatitude() + ", " + baiduLocation.getLongitude() + "]");
            Location location = convertBaiduLocationToLocation(baiduLocation);

            ChinaTransformLocation.transformToChinaLocation(location);
            L.i(TAG, "onReceiveLocation() China location [ " + location.getLatitude() + ", " + location.getLongitude() + "]");

            ChinaTransformLocation.transformToBaiduLocation(location);
            L.i(TAG, "onReceiveLocation() Baidu location [ " + location.getLatitude() + ", " + location.getLongitude() + "]");

            /*Location testLocation = new Location(LocationManager.NETWORK_PROVIDER);
            testLocation.setLongitude(113.319181);
            testLocation.setLatitude(23.109057);
            ChinaTransformLocation.transformToChinaLocation(testLocation);
            L.i(TAG, "testLocation() China location [ " + testLocation.getLatitude() + ", " + testLocation.getLongitude() + "]");

            ChinaTransformLocation.transformToBaiduLocation(testLocation);
            L.i(TAG, "testLocation() Baidu location [ " + testLocation.getLatitude() + ", " + testLocation.getLongitude() + "]");*/

            /*Location testLocation = new Location(LocationManager.NETWORK_PROVIDER);
            testLocation.setLongitude(113.3311329521378);
            testLocation.setLatitude(23.11220962419907);
            ChinaTransformLocation.transformFromBaiduToWorldLocation(testLocation);*/

            lastLocation = location;

            TasksBL.getTasksFromDB(handler);
        }
    }

    @Override
    public void onReceivePoi(BDLocation poiLocation) {
        L.i(TAG, "onReceivePoi() Location from Baidu location services [ " + poiLocation.getLatitude() + ", " + poiLocation.getLongitude() + "]");
    }


    /**
     * Get Last known location.
     *
     * @return null if not connected to Google Play Service or
     */
    public Location getLocation() {
        if (!Config.USE_BAIDU) {
            if (locationClient != null && locationClient.isConnected()) {
                this.lastLocation = locationClient.getLastLocation();
            }
            ChinaTransformLocation.transformToChinaLocation(lastLocation);

        } else {
            if (baiduLocationClient != null && baiduLocationClient.isStarted()) {
                this.lastLocation = convertBaiduLocationToLocation(baiduLocationClient.getLastKnownLocation());
            }
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

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Process data from local database
     */
    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    final Location currentLocation = lastLocation;
                    TasksBL.calculateTaskDistance(handler, currentLocation, cursor);
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_UPDATE:
                    boolean isLast = (Boolean) cookie;
                    if (isLast) {
                        notifyAllRequestedLocation();
                    }
                    break;
                default:
                    break;
            }
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
    public static void getCurrentLocation(final boolean force, final GetCurrentLocationListener currentLocationListener) {
        MatrixLocationManager lm = App.getInstance().getLocationManager();

        currentLocationListener.getLocationStart();

        Location location = lm.getLocation();
        if (location != null && !force) {
            if (Config.USE_BAIDU) {
                ChinaTransformLocation.transformFromBaiduToWorldLocation(location);
            }
            currentLocationListener.getLocationSuccess(location);
        } else {
            currentLocationListener.getLocationInProcess();
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    if (Config.USE_BAIDU) {
                        ChinaTransformLocation.transformFromBaiduToWorldLocation(location);
                    }
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


    public void setCurrentLocationUpdateListener(CurrentLocationUpdateListener currentLocationUpdateListener) {
        this.currentLocationUpdateListener = currentLocationUpdateListener;
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
