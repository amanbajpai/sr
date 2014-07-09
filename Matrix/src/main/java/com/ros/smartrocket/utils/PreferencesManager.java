package com.ros.smartrocket.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.fragment.TasksMapFragment;

/**
 * Global constant storage. Keep here small constants
 */
public class PreferencesManager {
    private static final String TAG = "PreferencesManager";
    private SharedPreferences preferences;
    private static PreferencesManager preferencesManager = null;

    /**
     * Get instance of {@link PreferencesManager}
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
     * @param context - current context
     * @return String
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
        return getString(Keys.GCM_PROPERTY_REG_ID, "");
    }

    /**
     * Save registration Id in preferences
     *
     * @param regId - current GCM id
     */
    public void setGCMRegistrationId(String regId) {
        setString(Keys.GCM_PROPERTY_REG_ID, regId);
    }

    public String getToken() {
        return getString(Keys.TOKEN, "");
    }

    public void setToken(String token) {
        setString(Keys.TOKEN, token);
    }

    public String getLanguageCode() {
        return getString(Keys.LANGUAGE_CODE, "");
    }

    public void setLanguageCode(String languageCode) {
        setString(Keys.LANGUAGE_CODE, languageCode);
    }

    public long getDeadlineReminderMillisecond() {
        return getLong(Keys.DEADLINE_REMINDER_MILLISECOND, DateUtils.MINUTE_IN_MILLIS * 30);
    }

    public void setDeadlineReminderMillisecond(long deadlineReminderMillisecond) {
        setLong(Keys.DEADLINE_REMINDER_MILLISECOND, deadlineReminderMillisecond);
    }

    public boolean getUseOnlyWiFiConnaction() {
        return getBoolean(Keys.USE_ONLY_WI_FI_CONNACTION, false);
    }

    public void setUseOnlyWiFiConnaction(boolean use) {
        setBoolean(Keys.USE_ONLY_WI_FI_CONNACTION, use);
    }

    public boolean getUseLocationServices() {
        return getBoolean(Keys.USE_LOCATION_SERVICES, true);
    }

    public void setUseLocationServices(boolean use) {
        setBoolean(Keys.USE_LOCATION_SERVICES, use);
    }

    public boolean getUseSocialSharing() {
        return getBoolean(Keys.USE_SOCIAL_SHARING, true);
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
        return getBoolean(Keys.USE_PUSH_MESSAGES, true);
    }

    public void setUsePushMessages(boolean use) {
        setBoolean(Keys.USE_PUSH_MESSAGES, use);
    }

    public boolean getUseDeadlineReminder() {
        return getBoolean(Keys.USE_DEADLINE_REMINDER, true);
    }

    public void setUseDeadlineReminder(boolean use) {
        setBoolean(Keys.USE_DEADLINE_REMINDER, use);
    }

    public int getLastNotAnsweredQuestionOrderId(int waveId, int taskId) {
        return getInt(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + waveId + "_" + taskId, 1);
    }

    public void setLastNotAnsweredQuestionOrderId(int waveId, int taskId, int orderId) {
        setInt(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + waveId + "_" + taskId, orderId);
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

    public long getLastRefreshMonthLimitDate() {
        return getLong(Keys.LAST_REFRESH_MONTH_LIMIT_DATE, 0);
    }

    public void setLastRefreshMonthLimitDate(long date) {
        setLong(Keys.LAST_REFRESH_MONTH_LIMIT_DATE, date);
    }

    public String getShortUrlToShare() {
        return getString(Keys.SHORT_URL_TO_SHARE, "");
    }

    public void setShortUrlToShare(String url) {
        setString(Keys.SHORT_URL_TO_SHARE, url);
    }

    public int getBitMaskSocialNetwork() {
        return getInt(Keys.BIT_MASK_SOCIAL_NETWORK, 0);
    }

    public void setBitMaskSocialNetwork(int bitMask) {
        setInt(Keys.BIT_MASK_SOCIAL_NETWORK, bitMask);
    }

    public void setLastLevelNumber(int levelNumber) {
        setInt(Keys.LAST_LEVEL_NUMBER, levelNumber);
    }

    public int getLastLevelNumber() {
        return getInt(Keys.LAST_LEVEL_NUMBER, -1);
    }

    public void setDefaultRadius(int radius) {
        setInt(Keys.DEFAULT_RADIUS, radius);
    }

    public int getDefaultRadius() {
        return getInt(Keys.DEFAULT_RADIUS, TasksMapFragment.DEFAULT_TASK_RADIUS);
    }

    public String getLastEmail() {
        return getString(Keys.LAST_EMAIL, "");
    }

    public void setLastEmail(String login) {
        setString(Keys.LAST_EMAIL, login);
    }

    public String getLastPassword() {
        return getString(Keys.LAST_PASSWORD, "");
    }

    public void setLastPassword(String password) {
        setString(Keys.LAST_PASSWORD, password);
    }

    public boolean getShowHiddenTask() {
        return getBoolean(Keys.SHOW_HIDDEN_TASKS, false);
    }

    public void setShowHiddenTask(boolean use) {
        setBoolean(Keys.SHOW_HIDDEN_TASKS, use);
    }

    public boolean getShowHiddenProject() {
        return getBoolean(Keys.SHOW_HIDDEN_PROJECT, false);
    }

    public void setShowHiddenProject(boolean use) {
        setBoolean(Keys.SHOW_HIDDEN_PROJECT, use);
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
