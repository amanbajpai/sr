package com.ros.smartrocket.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.MatrixLocation;

import java.util.Date;

/**
 * Global constant storage. Keep here small constants
 */
public class PreferencesManager {
    private static final String TAG = "PreferencesManager";
    private SharedPreferences preferences;
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
        preferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    }

    private SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * Get string value of device UUID. UUID generated by {@link DeviceUuidFactory}
     *
     * @param context
     * @return
     */
    public String getUUID(Context context) {
        return new DeviceUuidFactory(context).getDeviceUuid().toString();
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
        return registrationId;
    }

    /**
     * Save registration Id in preferences
     *
     * @param regId
     * @return
     */
    public void setGCMRegistrationId(String regId) {
        Editor editor = getPreferences().edit();
        editor.putString(Keys.GCM_PROPERTY_REG_ID, regId);
        editor.commit();
    }


    /**
     * Check is GCM is registered on the server
     *
     * @return
     */
    public boolean isGCMIdRegisteredOnServer() {
        final SharedPreferences prefs = getPreferences();
        boolean result = prefs.getBoolean(Keys.GCM_IS_GCMID_REGISTERED, false);
        return result;
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

    public String getToken() {
        return getString(Keys.TOKEN, "");
    }

    public void setToken(String token) {
        setString(Keys.TOKEN, token);
    }

    public String getLanguageCode() {
        return getString(Keys.LANGUAGE_CODE, "en");
    }

    public void setLanguageCode(String languageCode) {
        setString(Keys.LANGUAGE_CODE, languageCode);
    }

    public int getAppointmentInervalCode() {
        return getInt(Keys.APPOINTMENT_INTERVAL_CODE, 0);
    }

    public void setAppointmentInervalCode(int appointmentInervalCode) {
        setInt(Keys.APPOINTMENT_INTERVAL_CODE, appointmentInervalCode);
    }

    public boolean getUseOnlyWiFiConnaction() {
        return getBoolean(Keys.USE_ONLY_WI_FI_CONNACTION, false);
    }

    public void setUseOnlyWiFiConnaction(boolean use) {
        setBoolean(Keys.USE_ONLY_WI_FI_CONNACTION, use);
    }

    public boolean getUseLocationServices() {
        return getBoolean(Keys.USE_LOCATION_SERVICES, false);
    }

    public void setUseLocationServices(boolean use) {
        setBoolean(Keys.USE_LOCATION_SERVICES, use);
    }

    public boolean getUseSocialSharing() {
        return getBoolean(Keys.USE_SOCIAL_SHARING, false);
    }

    public void setUseSocialSharing(boolean use) {
        setBoolean(Keys.USE_SOCIAL_SHARING, use);
    }

    public boolean getUseSaveImageToCameraRoll() {
        return getBoolean(Keys.USE_SAVE_IMAGE_TO_CAMERA_ROLL, false);
    }

    public void setUseSaveImageToCameraRoll(boolean use) {
        setBoolean(Keys.USE_SAVE_IMAGE_TO_CAMERA_ROLL, use);
    }

    public boolean getUsePushMessages() {
        return getBoolean(Keys.USE_PUSH_MESSAGES, false);
    }

    public void setUsePushMessages(boolean use) {
        setBoolean(Keys.USE_PUSH_MESSAGES, use);
    }

    public boolean getUseDeadlineReminder() {
        return getBoolean(Keys.USE_DEADLINE_REMINDER, false);
    }

    public void setUseDeadlineReminder(boolean use) {
        setBoolean(Keys.USE_DEADLINE_REMINDER, use);
    }

    public int getLastNotAnsweredQuestionOrderId(int surveyId, int taskId) {
        return getInt(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + surveyId + "_" + taskId, 1);
    }

    public void setLastNotAnsweredQuestionOrderId(int surveyId, int taskId, int orderId) {
        setInt(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + surveyId + "_" + taskId, orderId);
    }

    public int get3GUploadTaskLimit() {
        return getInt(Keys.TREE_G_UPLOAD_TASK_LIMIT, Config.TREE_G_UPLOAD_TASK_LIMIT_MB);
    }

    public void set3GUploadTaskLimit(int limitMb) {
        setInt(Keys.TREE_G_UPLOAD_TASK_LIMIT, limitMb);
    }

    public int get3GUploadMonthLimit() {
        return getInt(Keys.TREE_G_UPLOAD_MONTH_LIMIT, Config.TREE_G_UPLOAD_MONTH_LIMIT_MB);
    }

    public void set3GUploadMonthLimit(int limitMb) {
        setInt(Keys.TREE_G_UPLOAD_MONTH_LIMIT, limitMb);
    }

    public int getUsed3GUploadMonthlySize() {
        return getInt(Keys.USED_TREE_G_UPLOAD_MONTHLY_SIZE, 0);
    }

    public void setUsed3GUploadMonthlySize(int usedSize) {
        setInt(Keys.USED_TREE_G_UPLOAD_MONTHLY_SIZE, usedSize);
    }

    public String getShortUrlToShare() {
        return getString(Keys.SHORT_URL_TO_SHARE, "");
    }

    public void setShortUrlToShare(String url) {
        setString(Keys.SHORT_URL_TO_SHARE, url);
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
