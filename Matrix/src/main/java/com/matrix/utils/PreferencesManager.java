package com.matrix.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.matrix.App;
import com.matrix.Keys;
import com.matrix.db.entity.MatrixLocation;
import com.matrix.db.entity.RegistrationResponse;

import java.util.Date;

/**
 * Global constant storage. Keep here small constants
 */
public class PreferencesManager {
    private static final String TAG = "PreferencesManager";
    private SharedPreferences _preferences;
    private static PreferencesManager preferencesManager = null;

    /**
     * Get instance of {@link PreferencesManager}
     *
     * @return
     */
    public static PreferencesManager getInstance() {
        if (preferencesManager == null) {
            preferencesManager = new PreferencesManager();
        }
        return preferencesManager;
    }

    private PreferencesManager() {
        _preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    }

    private SharedPreferences getPreferences() {
        return _preferences;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getGCMRegistrationId() {
        Context context = App.getInstance().getApplicationContext();
        final SharedPreferences prefs = getPreferences();
        String registrationId = prefs.getString(Keys.GCM_PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            L.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Keys.GCM_PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = UIUtils.getAppVersionCode(context);
        if (registeredVersion != currentVersion) {
            L.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Save registration Id in preferences
     *
     * @param regId
     * @return
     */
    public void setGCMRegistrationId(String regId) {
        Context context = App.getInstance().getApplicationContext();
        Editor editor = getPreferences().edit();
        editor.putString(Keys.GCM_PROPERTY_REG_ID, regId);
        editor.commit();
    }

    /**
     *
     */
    public void setCurrentLocation(MatrixLocation location) {
        Gson gson = new Gson();
        String json = gson.toJson(location);
        Editor editor = getPreferences().edit();
        editor.putString(Keys.PREFERENCE_CURRENT_LOCATION, json);
        editor.commit();
        Location newLocation = getCurrentLocation();
        if (newLocation != null) {
            L.i(TAG, "curr location[" + newLocation + "]");
            L.i(TAG, "curr location[time=" + new Date(newLocation.getTime()) + "]");
        }

    }

    public Location getCurrentLocation() {
        Location location = null;
        Gson gson = new Gson();
        String locationJson = getString(Keys.PREFERENCE_CURRENT_LOCATION, "");
        if (!locationJson.equals("")) {
            MatrixLocation matrixLocation = gson.fromJson(locationJson, MatrixLocation.class);
            location = matrixLocation.getLocation();
        }
        return location;
    }

    public int getInt(String key, int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public long getLong(String key, long defaultValue) {
        return getPreferences().getLong(key, defaultValue);
    }

    public void setLong(String key, long value) {
        Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public float getFloat(String key, float defaultValue) {
        return getPreferences().getFloat(key, defaultValue);
    }

    public void setFloat(String key, float value) {
        Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return getPreferences().getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void remove(String key) {
        Editor editor = getPreferences().edit();
        editor.remove(key);
        editor.commit();
    }

    public void clearAll() {
        Editor editor = getPreferences().edit();
        editor.clear();
        editor.commit();
    }
}