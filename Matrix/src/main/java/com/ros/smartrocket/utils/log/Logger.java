package com.ros.smartrocket.utils.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ankurrawal on 27/4/18.
 */

public class Logger {


    public static final String DATE_TIME_STAMP_HIRES_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static void Logger(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v("\n" + TAG, msg);
            try {
                appendLog(App.getInstance(), TAG + " : " + msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void appendLog(Context context, String text) {
        File root = Environment.getExternalStorageDirectory(); //con.getExternalFilesDir(null);
        File logFile = new File(root, getAppNameFileName(context));
        String timeStamp = getTimeStampString();
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                    true));
            buf.append(timeStamp + " ");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // App name methods

    private static String getAppNameFileName(Context context) {
        String result = getAppName(context);
        // replace non-alphanumeric with _
        result = result.replaceAll("[^a-zA-Z0-9.-]", "_") + ".txt";
        return result;
    }

    private static String getTimeStampString() {
        Calendar now = Calendar.getInstance();
        return calToDateTimeHiresStr(now);
    }

    private static String getAppName(Context context) {
        PackageManager lPackageManager = context.getPackageManager();
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : "Unknown");
    }

    private static String calToDateTimeHiresStr(Calendar adatetime) {
        return calToStr(adatetime, DATE_TIME_STAMP_HIRES_FORMAT);
    }

    private static String calToStr(Calendar date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date.getTime());
    }

}
