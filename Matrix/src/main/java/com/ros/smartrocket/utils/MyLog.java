package com.ros.smartrocket.utils;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.ros.smartrocket.Config;

/**
 * Class for custom logging with Crashlytics logs
 * If {@link BuildConfig.DEBUG} is true, logs run locally
 * If {@link BuildConfig.DEBUG} is false, logs send to Fabric
 */
@SuppressWarnings("all")
public final class MyLog {
    private static final String TAG = "SR";
    private static final boolean DEBUG = Config.LOG_ENABLED;

    private MyLog() {
    }

    /**
     * Logs stack trace on any line of code
     */
    public static void logStackTrace() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            MyLog.v(element);
        }
    }

    /**
     * Logs stack trace of exception
     *
     * @param ex Throwable
     */
    public static void logStackTrace(Throwable ex) {
        runLogs(Log.WARN, ex);
        for (StackTraceElement element : ex.getStackTrace()) runLogs(Log.WARN, element);
    }

    /**
     * Log data with {@link Log.VERBOSE} priority
     *
     * @param params varargs params
     */
    public static void v(Object... params) {
        runLogs(Log.VERBOSE, params);
    }

    /**
     * Log data with {@link Log.DEBUG} priority
     *
     * @param params varargs params
     */
    public static void d(Object... params) {
        runLogs(Log.DEBUG, params);
    }

    /**
     * Log data with {@link Log.INFO} priority
     *
     * @param params varargs params
     */
    public static void i(Object... params) {
        runLogs(Log.INFO, params);
    }

    /**
     * Log data with {@link Log.WARN} priority
     *
     * @param params varargs params
     */
    public static void w(Object... params) {
        runLogs(Log.WARN, params);
    }

    /**
     * Log data with {@link Log.ERROR} priority
     *
     * @param params varargs params
     */
    public static void e(Object... params) {
        runLogs(Log.ERROR, params);
    }

    private static String buildString(Object[] params) {
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
            sb.append(" ");
        }
        return sb.toString();
    }

    private static void runLogs(int priority, Object... params) {
        if (DEBUG) {
            Crashlytics.log(priority, TAG, buildString(params));
        } else {
            Crashlytics.log(buildString(params));
        }
    }
}