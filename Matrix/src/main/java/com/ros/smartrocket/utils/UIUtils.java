package com.ros.smartrocket.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utils class for easy work with UI Views
 */
public class UIUtils {
    private static final String TAG = "UIUtils";
    public static SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH); //'Z'
    public static SimpleDateFormat hourMinute1Format = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
    public static SimpleDateFormat dayMonthYear1Format = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
    public static SimpleDateFormat hourMinuteDyMonthYear1Format = new SimpleDateFormat("HH:mm a dd MMM yy", Locale.ENGLISH);

    /**
     * Show simple Toast message
     *
     * @param context
     * @param resId
     */
    public static void showSimpleToast(Context context, int resId) {
        if (context != null) {
            Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    /**
     * Show simple Toast message
     *
     * @param context
     * @param resId
     * @param duration
     */
    public static void showSimpleToast(Context context, int resId, int duration) {
        if (context != null) {
            Toast toast = Toast.makeText(context, resId, duration);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    /**
     * Show simple Toast message
     *
     * @param context
     * @param msg
     */
    public static void showSimpleToast(Context context, String msg) {
        if (context != null && msg != null) {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    public static void hideSoftKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
    }

    /**
     * Get version name visible for users
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String current_version = "";
        try {
            current_version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            L.e(TAG, "getAppVersion() Error get app version");
        }
        return current_version;
    }


    /**
     * Get developer version of application
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int current_version = 0;
        try {
            current_version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            L.e(TAG, "getAppVersionCode() Error get app version");
        }
        return current_version;
    }

    /**
     * @param context
     */
    public static boolean isApplicationRuning(Context context) {
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (topActivity.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param context
     * @param pixels
     */
    public static int getDpFromPx(Context context, int pixels) {
        final float d = context.getResources().getDisplayMetrics().density;
        return (int) (pixels / d);
    }

    /**
     * @param context
     * @param dp
     */
    public static int getPxFromDp(Context context, int dp) {
        final float d = context.getResources().getDisplayMetrics().density;
        return (int) (dp * d);
    }

    /**
     * @param unrounded
     * @param precision
     * @param roundingMode
     */
    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        int x = a % b;
        return gcd(b, x);
    }

    /*public static long getMemorySize(int type) {
        try {
            Runtime info = Runtime.getRuntime();
            switch (type) {
                case 1:
                    return info.freeMemory();
                case 2:
                    return info.totalMemory() - info.freeMemory();
                case 3:
                    return info.totalMemory();
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }*/

    /**
     * Check if there is Ethernet connection
     *
     * @param c
     * @return
     */
    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isWiFi(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isGpsEnabled(Context c) {
        LocationManager locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isGooglePlayServicesEnabled(Context c) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static boolean isMockLocationEnabled(Context context) {
        return !Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String formatAmount(int num) {
        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols();
        decimalFormatSymbol.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbol);
        return decimalFormat.format(num);
    }

    public static void setFakeAlpha(View view, float alpha) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
        alphaAnimation.setDuration(0); // Make animation instant
        alphaAnimation.setFillAfter(true); // Tell it to persist after the animation ends
        view.startAnimation(alphaAnimation);
    }

    public static void unSetFakeAlpha(View view) {
        view.clearAnimation();
    }

    @SuppressLint("NewApi")
    public static boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
                return true;
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Opinion", text);
                clipboard.setPrimaryClip(clip);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDeviceId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * Convert ISO time to long format
     *
     * @param dateString
     */
    public static long isoTimeToLong(String dateString) {
        try {
            return isoDateFormat.parse(dateString).getTime();
        } catch (Exception e) {
            L.e("twitterTimeToLong", "Parse error" + e);
        }
        return 0;
    }

    /**
     * @param dateLong
     * @param formatId
     */
    public static String longToString(long dateLong, int formatId) {
        switch (formatId) {
            case 0:
                return hourMinute1Format.format(new Date(dateLong));
            case 1:
                return dayMonthYear1Format.format(new Date(dateLong));
            case 2:
                return isoDateFormat.format(new Date(dateLong));
            case 3:
                return hourMinuteDyMonthYear1Format.format(new Date(dateLong));
            default:
                break;
        }
        return "longToStringFormatNotFound";
    }

    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String convertMToKm(Activity activity, float distance, int textResId) {
        String result = "0";
        String format = "%.1f";
        float convertedDistance = distance > 1000 ? distance / 1000 : distance;
        String mOrKm = activity.getString(distance > 1000 ? R.string.distance_km : R.string.distance_m);

        if (textResId != 0) {
            result = String.format(activity.getString(textResId),
                    String.format(Locale.US, format, convertedDistance), mOrKm);
        } else {
            result = String.format(Locale.US, format, convertedDistance);
        }
        return result;
    }

    public static boolean isTrue(Boolean s) {
        return s != null && s;
    }

    public static boolean isFalse(Boolean s) {
        return s == null || !s;
    }

    public static String getNumbersOnly(CharSequence s) {
        return s.toString().replaceAll("[^0-9]", ""); // Should of course be more robust
    }
}
