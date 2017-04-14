package com.ros.smartrocket.utils;

import com.ros.smartrocket.Config;

/**
 * Class for logging
 */

public class L {
    public static void i(String tag, String string) {
        if (Config.LOG_ENABLED && tag != null && string != null) {
            android.util.Log.i(tag, string);
        }
    }

    public static void e(String tag, String string) {
        if (Config.LOG_ENABLED && tag != null && string != null) {
            android.util.Log.e(tag, string);
        }
    }

    public static void e(String tag, String string, Exception e) {
        if (Config.LOG_ENABLED && tag != null && string != null) {
            android.util.Log.e(tag, string, e);
        }
    }

    public static void d(String tag, String string) {
        if (Config.LOG_ENABLED && tag != null && string != null) {
            android.util.Log.d(tag, string);
        }
    }

    public static void v(String tag, String string) {
        if (Config.LOG_ENABLED && tag != null && string != null) {
            android.util.Log.v(tag, string);
        }
    }

    public static void w(String tag, String string) {
        if (Config.LOG_ENABLED && tag != null && string != null) {
            android.util.Log.w(tag, string);
        }
    }
}
