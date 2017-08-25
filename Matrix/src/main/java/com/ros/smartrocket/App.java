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
import com.google.gson.Gson;
import com.helpshift.All;
import com.helpshift.Core;
import com.helpshift.InstallConfig;
import com.helpshift.exceptions.InstallException;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.retrofit.MatrixApi;
import com.ros.smartrocket.net.retrofit.RetrofitHolder;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.Locale;

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
    private RetrofitHolder retrofitHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        retrofitHolder = new RetrofitHolder();
        initHS();
        initSDKs();
        initLocaleSettings();
        fillDeviceInfo();
        requestToCurrentLocation();
        clearMonthLimitIfNeed();
    }

    private void fillDeviceInfo() {
        deviceId = UIUtils.getDeviceId(this);
        deviceApiNumber = android.os.Build.VERSION.SDK_INT;
        deviceType = "android";
        locationManager = new MatrixLocationManager(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initLocaleSettings();
    }

    public void initLocaleSettings() {
        LocaleUtils.setCurrentLanguage();
        Locale newLocale = LocaleUtils.getCurrentLocale();
        Configuration config = getResources().getConfiguration();
        config.setLocale(newLocale);
        getApplicationContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void initSDKs() {
        if (Config.USE_BAIDU) {
            SDKInitializer.initialize(getApplicationContext());
            JPushInterface.setDebugMode(BuildConfig.DEBUG);
        } else {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
        }
        Fabric.with(this, new Crashlytics());
    }

    private void initHS() {
        InstallConfig installConfig = new InstallConfig.Builder().build();
        Core.init(All.getInstance());
        try {
            Core.install(this,
                    "61ff0f6188482d2091170a688375265b",
                    "smartrocket.helpshift.com",
                    "smartrocket_platform_20160227023341398-aa7246f2aeba0ab",
                    installConfig);
        } catch (InstallException e) {
            e.printStackTrace();
        }
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
        PreferencesManager.getInstance().setUsePushMessages(myAccount.getAllowPushNotification());
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
        locationManager.getLocationAsync(location -> L.i(TAG, "[NEW LOC = " + location + "]"));
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceApiNumber() {
        return String.valueOf(deviceApiNumber);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public MatrixLocationManager getLocationManager() {
        return locationManager;
    }


    public void clearPositionData() {
        locationManager.setLastBaiduPosition(null);
        locationManager.setLastGooglePosition(null);
        locationManager.setZoomLevel(0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public MatrixApi getApi() {
        return retrofitHolder.getMatrixApi();
    }
}
