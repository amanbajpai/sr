package com.ros.smartrocket;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.text.format.DateUtils;

import com.baidu.mapapi.SDKInitializer;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.helpshift.All;
import com.helpshift.Core;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.fragment.SettingsFragment;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import io.fabric.sdk.android.Fabric;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App instance;

    private String deviceId;
    private int deviceApiNumber;
    private String deviceType;
    private MatrixLocationManager locationManager;
    private MyAccount myAccount;
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        HashMap config = new HashMap();
        Core.init(All.getInstance());
        Core.install(this,
                "61ff0f6188482d2091170a688375265b",
                "smartrocket.helpshift.com",
                "smartrocket_platform_20160227023341398-aa7246f2aeba0ab",
                config);

        if (Config.USE_BAIDU) {
            SDKInitializer.initialize(getApplicationContext());
            JPushInterface.setDebugMode(BuildConfig.DEBUG);
        } else {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
        }


        AnalyticsWrapper.initAnalytics(this);

        Fabric.with(this, new Crashlytics());

        instance = this;

        deviceId = UIUtils.getDeviceId(this);
        deviceApiNumber = android.os.Build.VERSION.SDK_INT;
        deviceType = "android";
        locationManager = new MatrixLocationManager(getApplicationContext());

        requestToCurrentLocation();
        UIUtils.setCurrentLanguage();
        clearMonthLimitIfNeed();
    }


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            //mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static App getInstance() {
        return instance;
    }

    public MyAccount getMyAccount() {
        if (myAccount == null) {
            String profileJson = PreferencesManager.getInstance().getString(Keys.MY_ACCOUNT, "{}");
            myAccount = new Gson().fromJson(profileJson, MyAccount.class);
        }
        return myAccount;
    }

    public void setMyAccount(MyAccount profile) {
        this.myAccount = profile;
        String profileJson = new Gson().toJson(profile);
        PreferencesManager.getInstance().setString(Keys.MY_ACCOUNT, profileJson);
    }

    public static void clearMonthLimitIfNeed() {
        Calendar calendar = Calendar.getInstance();
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        if (calendar.getTimeInMillis() >= preferencesManager.getLastRefreshMonthLimitDate()
                + DateUtils.YEAR_IN_MILLIS / 12) {
            preferencesManager.setUsed3GUploadMonthlySize(0);
            preferencesManager.setLastRefreshMonthLimitDate(calendar.getTimeInMillis());
        }
    }

    public void requestToCurrentLocation() {
        Location loc = locationManager.getLocation();
        L.i(TAG, "[LOC = " + loc + "]");
        locationManager.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
            @Override
            public void onUpdate(Location location) {
                L.i(TAG, "[NEW LOC = " + location + "]");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UIUtils.setCurrentLanguage();
    }


    /**
     * @return
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @return
     */
    public String getDeviceApiNumber() {
        return String.valueOf(deviceApiNumber);
    }

    /**
     * @return
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Get Location Manager for coordinates retrieval
     *
     * @return
     */
    public MatrixLocationManager getLocationManager() {
        return locationManager;
    }

    public com.google.android.gms.maps.model.LatLng getLastGooglePosition() {
        return locationManager != null ? locationManager.getLastGooglePosition() : null;
    }

    public void clearPositionData() {
        locationManager.setLastBaiduPosition(null);
        locationManager.setLastGooglePosition(null);
        locationManager.setZoomLevel(0);
    }
}
