package com.ros.smartrocket.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.io.IOException;

public class GcmManager {
    private static final String TAG = GcmManager.class.getSimpleName();

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static GcmManager instance = null;
    private GoogleCloudMessaging gcm;

    public static GcmManager getInstance() {
        if (instance == null) {
            instance = new GcmManager();
        }
        return instance;
    }

    public GcmManager() {
        gcm = GoogleCloudMessaging.getInstance(App.getInstance());
    }

    public void registerGcm() {
        Context context = App.getInstance();
        if (UIUtils.isGooglePlayServicesAvailable(context)) {
            String regid = getRegistrationId(context);

            if (!TextUtils.isEmpty(regid)) {
                registerGCMInBackground();
            }
        } else {
            L.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    public void unregisterGcm() {
        unregisterGCMInBackground();
    }

    public void registerGCMInBackground() {
        new AsyncTask<Void, String, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String registrationId = null;
                try {
                    registrationId = gcm.register(BuildConfig.GCM_SENDER_ID);
                    L.d(TAG, "Device registered: registrationId = " + registrationId);

                    //TODO Send registrationId to server
                    storeRegistrationId(registrationId);

                } catch (IOException e) {
                    L.e(TAG, "registerGCMInBackground() [Error :" + e.getMessage() + "]");
                }
                return registrationId;
            }
        }.execute();
    }

    public void unregisterGCMInBackground() {
        new AsyncTask<Void, String, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean isUnregistered = false;
                try {
                    gcm.unregister();
                    isUnregistered = true;
                } catch (IOException e) {
                    L.e(TAG, "unregisterGCMInBackground() [Error :" + e.getMessage() + "]");
                }
                return isUnregistered;
            }

            @Override
            protected void onPostExecute(Boolean unregistered) {
                if (unregistered) {
                    L.d(TAG, "Device unregistered");
                } else {
                    L.d(TAG, "Unregister error");
                }
            }
        }.execute();
    }

    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            L.i(TAG, "Registration not found.");
            registrationId = "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = UIUtils.getAppVersionCode(context);
        if (registeredVersion != currentVersion) {
            L.i(TAG, "App version changed.");
            registrationId = "";
        }
        return registrationId;
    }

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        int appVersion = UIUtils.getAppVersionCode(App.getInstance());

        L.i(TAG, "Saving regId on app version " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    public GoogleCloudMessaging getGcm() {
        return gcm;
    }
}
