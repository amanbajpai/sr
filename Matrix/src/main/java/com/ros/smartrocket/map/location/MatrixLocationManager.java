package com.ros.smartrocket.map.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.interfaces.DistancesUpdateListener;
import com.ros.smartrocket.utils.ChinaTransformLocation;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public final class MatrixLocationManager implements com.google.android.gms.location.LocationListener,
        android.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BDLocationListener {

    private static final String TAG = MatrixLocationManager.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private Context context;
    private com.baidu.location.LocationClient baiduLocationClient;
    private boolean isConnected;
    private Location lastLocation;
    private Queue<ILocationUpdate> requested;
    private LocationRequest locationRequest;
    private CurrentLocationUpdateListener currentLocationUpdateListener;
    private AsyncQueryHandler handler;
    private LocationManager locationManager;
    private DistancesUpdateListener distancesUpdatedListener;
    private com.google.android.gms.maps.model.LatLng lastGooglePosition;
    private LatLng lastBaiduPosition;
    private float zoomLevel;
    private GoogleApiClient mGoogleApiClient;


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

    private void startLocationClient() {
        if (!Config.USE_BAIDU) {
            startGoogleLocationClient();
        } else {
            startBaiduLocationClient();
        }
    }

    private void startGoogleLocationClient() {
        if (mGoogleApiClient == null || (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected())) {
            L.e(TAG, "startGoogleLocationClient");
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            // Connect the client.
            mGoogleApiClient.connect();

        }
    }

    private void startBaiduLocationClient() {
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

    @SuppressLint("MissingPermission")
    public void startGpsLocationClient() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (isPermissionGuaranteed()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Keys.UPDATE_INTERVAL, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Keys.UPDATE_INTERVAL, 0, this);
            isConnected = true;
            notifyAllRequestedLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        L.i(TAG, "onConnected() [bundle = " + bundle + "]");
        isConnected = true;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(Keys.UPDATE_INTERVAL);
        locationRequest.setFastestInterval(Keys.FASTEST_INTERVAL);

        if (isPermissionGuaranteed()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
            try {
                this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (Exception e) {
                L.e(TAG, "onConnected. locationClient error" + e.getMessage(), e);
            }
            notifyAllRequestedLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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
            notifyAllRequestedLocation();
        }
    }


    /**
     * BAIDU location listeners
     */

    @Override
    public void onReceiveLocation(BDLocation baiduLocation) {
        if (baiduLocation != null) {
            L.i(TAG, "onReceiveLocation() Source location [ " + baiduLocation.getLatitude() + ", " + baiduLocation.getLongitude() + "]");
            Location location = convertBaiduLocationToLocation(baiduLocation);

            ChinaTransformLocation.transformFromChinaToBaiduLocation(location);
            L.i(TAG, "onReceiveLocation() Baidu location [ " + location.getLatitude() + ", " + location.getLongitude() + "]");

            lastLocation = location;
            notifyAllRequestedLocation();
        }
    }

    /**
     * Get Last known location.
     *
     * @return null if not connected to Google Play Service or
     */
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        if (!Config.USE_BAIDU) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && isPermissionGuaranteed()) {
                this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            ChinaTransformLocation.transformFromWorldToChinaLocation(lastLocation);

        } else {
            if (baiduLocationClient != null && baiduLocationClient.isStarted()) {
                this.lastLocation = convertBaiduLocationToLocation(baiduLocationClient.getLastKnownLocation());
            }
            ChinaTransformLocation.transformFromChinaToBaiduLocation(lastLocation);
        }

        if (lastLocation != null) {
            L.i(TAG, "getLocation() [ " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude()
                    + ", time=" + new Date(lastLocation.getTime()) + "]");

            if (preferencesManager.getUseLocationServices() && lastLocation.getTime()
                    < new Date().getTime() - DateUtils.MINUTE_IN_MILLIS * 2
                    && ((!Config.USE_BAIDU && (mGoogleApiClient == null || !mGoogleApiClient.isConnected()))
                    || (Config.USE_BAIDU && (baiduLocationClient == null || !baiduLocationClient.isStarted())))) {
                lastLocation = null;
                startLocationClient();
            }
        }
        return lastLocation;
    }

    private void getAddress(Location location, IAddress callback) {
        GeoCoder geoCoder = new GeoCoder(Locale.ENGLISH);
        geoCoder.getFromLocation(location, callback);
    }

    /**
     * Add request to the Queue and wait for update
     *
     * @param listener - result callback
     */
    public void getLocationAsync(ILocationUpdate listener) {
        requested.add(listener);
    }

    /**
     * Force to recalculate distances to the tasks according to the actual location
     *
     * @param distancesUpdateListener - listener to be called when the recalculation in done
     */
    public void recalculateDistances(DistancesUpdateListener distancesUpdateListener) {
        this.distancesUpdatedListener = distancesUpdateListener;
        TasksBL.getTasksFromDB(handler);
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

    private void notifyDatabaseDistancesRecalculated() {
        if (distancesUpdatedListener != null) {
            distancesUpdatedListener.onDistancesUpdated();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /**
     * Process data from local database
     */
    private class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    if (cursor != null && cursor.getCount() > 0) {
                        final Location currentLocation = lastLocation;
                        TasksBL.calculateTaskDistance(handler, currentLocation, cursor);
                    } else {
                        notifyDatabaseDistancesRecalculated();
                    }
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
                        notifyDatabaseDistancesRecalculated();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Retrieve current location from {@link MatrixLocationManager}
     *
     * @param force                   - if true than get location asynchronously, false - block Thread and wait update!
     * @param currentLocationListener - result callback
     */
    @SuppressLint("MissingPermission")
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
            lm.getLocationAsync(location1 -> {
                if (Config.USE_BAIDU) {
                    ChinaTransformLocation.transformFromBaiduToWorldLocation(location1);
                }
                currentLocationListener.getLocationSuccess(location1);
            });
            L.i(TAG, "onDisconnected()");
            if (lm.isConnected()) {
                if (!Config.USE_BAIDU) {
                    if (lm.mGoogleApiClient.isConnected() && MatrixLocationManager.isPermissionGuaranteed()) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                lm.mGoogleApiClient, lm.locationRequest, lm);
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
        lm.getAddress(location, address -> {
            String countryName = "";
            String cityName = "";
            String districtName = "";

            if (address != null) {
                countryName = !TextUtils.isEmpty(address.getCountryName()) ? address.getCountryName() : "";
                cityName = !TextUtils.isEmpty(address.getLocality()) ? address.getLocality() : "";
                districtName = !TextUtils.isEmpty(address.getSubLocality()) ? address.getSubLocality() : "";
            }

            getAddressListener.onGetAddressSuccess(location, countryName, cityName, districtName);
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
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
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

    interface IAddress {
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

    abstract public class SimpleGetCurrentLocationListener implements GetCurrentLocationListener {

        @Override
        public void getLocationStart() {

        }

        @Override
        public void getLocationInProcess() {

        }

        @Override
        abstract public void getLocationSuccess(Location location);

        @Override
        abstract public void getLocationFail(String errorText);
    }

    public com.google.android.gms.maps.model.LatLng getLastGooglePosition() {
        return lastGooglePosition;
    }

    public LatLng getLastBaiduPositionPosition() {
        return lastBaiduPosition;
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setLastGooglePosition(com.google.android.gms.maps.model.LatLng position) {
        lastGooglePosition = position;
    }

    public void setLastBaiduPosition(LatLng position) {
        lastBaiduPosition = position;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public boolean isLastLocationSaved() {
        return lastBaiduPosition != null || lastGooglePosition != null;
    }

    public static boolean isPermissionGuaranteed() {
        return !(ActivityCompat.checkSelfPermission(App.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(App.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }
}
