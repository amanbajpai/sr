package com.ros.smartrocket.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.db.entity.ProgressUpdate;
import com.ros.smartrocket.db.entity.account.register.RegistrationPermissions;
import com.ros.smartrocket.db.entity.task.WaitingUploadTask;
import com.ros.smartrocket.presentation.task.map.TasksMapFragment;

/**
 * Global constant storage. Keep here small constants
 */
@SuppressLint("CommitPrefEdits")
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

    public String getUUID() {
        return new DeviceUuidFactory(App.getInstance()).getDeviceUuid().toString();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
//    public String getFCMRegistrationId() {
//        return getString(Keys.FCM_PROPERTY_REG_ID, "");
//    }
//
//    /**
//     * Save registration Id in preferences
//     *
//     * @param regId - current GCM id
//     */
//    public void setFCMRegistrationId(String regId) {
//        setString(Keys.FCM_PROPERTY_REG_ID, regId);
//    }
    public String getToken() {
        return getString(Keys.TOKEN, "");
    }

    public void removeToken() {
        remove(Keys.TOKEN);
        remove(Keys.TOKEN_FOR_UPLOAD_FILE);
    }

    public void setToken(String token) {
        setString(Keys.TOKEN, token);
    }

    public String getTokenForUploadFile() {
        return getString(Keys.TOKEN_FOR_UPLOAD_FILE, "");
    }

    public void setTokenForUploadFile(String token) {
        setString(Keys.TOKEN_FOR_UPLOAD_FILE, token);
    }

    public long getTokenUpdateDate() {
        return getLong(Keys.TOKEN_UPDATE_DATE, 0);
    }

    public void setTokenUpdateDate(long timeInMillis) {
        setLong(Keys.TOKEN_UPDATE_DATE, timeInMillis);
    }

    public String getLanguageCode() {
        return LocaleUtils.getCorrectLanguageCode(getString(Keys.LANGUAGE_CODE, ""));
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

    public int getLastNotAnsweredQuestionOrderId(int waveId, int taskId, int missionId) {
        return getInt(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + waveId + "_" + taskId + "_" + missionId, 1);
    }

    public void setLastNotAnsweredQuestionOrderId(int waveId, int taskId, int missionId, int orderId) {
        setInt(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + waveId + "_" + taskId + "_" + missionId, orderId);
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

    public int getLastAppVersion() {
        return getInt(Keys.LAST_APP_VERSION, 0);
    }

    public void setLastAppVersion(int appVersion) {
        setInt(Keys.LAST_APP_VERSION, appVersion);
    }

    public boolean getShowHiddenTask() {
        return getBoolean(Keys.SHOW_HIDDEN_TASKS, false);
    }

    public void setShowHiddenTask(boolean use) {
        setBoolean(Keys.SHOW_HIDDEN_TASKS, use);
    }

    public boolean getShowActivityDialog() {
        return getBoolean(Keys.SHOW_ACTIVITY_DIALOG, true);
    }

    public void setShowActivityDialog(boolean show) {
        setBoolean(Keys.SHOW_ACTIVITY_DIALOG, show);
    }

    public boolean getIsFirstLogin() {
        return getBoolean(Keys.IS_FIRST_LOGIN, true);
    }

    public void setIsFirstLogin(boolean isFirstLogin) {
        setBoolean(Keys.IS_FIRST_LOGIN, isFirstLogin);
    }

    public void setShowPushNotifStar(boolean b) {
        setBoolean(Keys.SHOW_PUSH_NOTIF_STAR, b);
    }

    public boolean showPushNotifStar() {
        return getBoolean(Keys.SHOW_PUSH_NOTIF_STAR, false);
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

    public void saveUploadFilesProgress(WaitingUploadTask task, Integer notUploadedCount) {
        Gson gson = new Gson();
        String progress = gson.toJson(new ProgressUpdate(task, notUploadedCount));
        setString(Keys.UPLOAD_FILES_PROGRESS, progress);
    }

    public void clearUploadFilesProgress() {
        setString(Keys.UPLOAD_FILES_PROGRESS, null);
    }

    public ProgressUpdate getUploadProgress() {
        ProgressUpdate progressUpdate = null;
        String progress = getString(Keys.UPLOAD_FILES_PROGRESS, "");
        if (!TextUtils.isEmpty(progress)) {
            Gson gson = new Gson();
            progressUpdate = gson.fromJson(progress, ProgressUpdate.class);
        }
        return progressUpdate;
    }

    public void saveRegistrationPermissions(RegistrationPermissions permissions) {
        Gson gson = new Gson();
        String regPermission = gson.toJson(permissions);
        setString(Keys.REGISTRATION_PERMISSIONS, regPermission);
    }

    public RegistrationPermissions getRegPermissions() {
        RegistrationPermissions permissions = new RegistrationPermissions(RegistrationPermissions.ALL);
        String regPermissions = getString(Keys.REGISTRATION_PERMISSIONS, "");
        if (!TextUtils.isEmpty(regPermissions)) {
            Gson gson = new Gson();
            permissions = gson.fromJson(regPermissions, RegistrationPermissions.class);
        }
        return permissions;
    }

    public void saveAppVersion(AppVersion appVersion) {
        Gson gson = new Gson();
        setString(Keys.APP_VERSION, gson.toJson(appVersion));
    }

    public AppVersion getAppVersion() {
        AppVersion appVersion = new AppVersion("0.0.0");
        String version = getString(Keys.APP_VERSION, "");
        if (!TextUtils.isEmpty(version)) {
            Gson gson = new Gson();
            appVersion = gson.fromJson(version, AppVersion.class);
        }
        return appVersion;
    }

    public void setTandCShowedForCurrentUser() {
        setTandCShowed(getLastEmail());
    }

    public void setTandCShowed(String email) {
        setBoolean(Keys.T_AND_C + email, true);
    }

    public boolean isTandCShowed() {
        return getBoolean(Keys.T_AND_C + getLastEmail(), false);
    }

    public String getFirebaseToken() {
        return getString(Keys.FIREBASE_PUSH_TOKEN, "");
    }

    public void setFirebaseToken(String token) {
        setString(Keys.FIREBASE_PUSH_TOKEN, token);
    }


    public String getlastRecordedAudioLength() {
        return getString(Keys.LAST_AUDIO_RECORDED_LENGTH, "");
    }

    public void setlastRecordedAudioLength(String audioLength) {
        setString(Keys.LAST_AUDIO_RECORDED_LENGTH, audioLength);
    }


}
