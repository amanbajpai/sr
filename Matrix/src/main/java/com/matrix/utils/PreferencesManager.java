package com.matrix.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.matrix.App;


public class PreferencesManager {
    private SharedPreferences _preferences;
    private static PreferencesManager preferencesManager = null;

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