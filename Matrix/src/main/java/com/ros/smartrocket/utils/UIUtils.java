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
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Utils class for easy work with UI Views
 */
public class UIUtils {
    private static final String TAG = "UIUtils";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",
            Locale.ENGLISH); //'Z'
    private static final SimpleDateFormat HOUR_MINUTE_1_FORMAT = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_1_FORMAT = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_2_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT = new SimpleDateFormat("HH:mm a dd MMM"
            + " yy", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_2_FORMAT = new SimpleDateFormat("HH:mm a dd MMM"
            + " yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_HOUR_MINUTE_1_FORMAT = new SimpleDateFormat("dd.MM"
            + ".yyyy / HH:mm", Locale.ENGLISH);

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
        String currentVersion = "";
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            L.e(TAG, "getAppVersion() Error get app version");
        }
        return currentVersion;
    }


    /**
     * Get developer version of application
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            L.e(TAG, "getAppVersionCode() Error get app version");
        }
        return currentVersion;
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
        boolean isOnline = false;
        if (c != null) {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    public static boolean isWiFi(Context c) {
        boolean isOnline = false;
        if (c != null) {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    public static boolean is3G(Context c) {
        boolean isOnline = false;
        if (c != null) {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    public static boolean isGpsEnabled(Context c) {
        boolean isEnable = false;
        if (c != null) {
            LocationManager locationManager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
            isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        return isEnable;
    }

    public static boolean isGooglePlayServicesEnabled(Context c) {
        boolean isEnable = false;
        if (c != null) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
            isEnable = resultCode == ConnectionResult.SUCCESS;
        }

        return isEnable;
    }

    public static boolean isMockLocationEnabled(Context context) {
        if (Config.CAN_USE_FAKE_LOCATION) {
            return false;
        } else {
            return !Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
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

        // TODO dateString = "2015-03-25T13:27:00.000+02:00";
        try {
            return ISO_DATE_FORMAT.parse(dateString).getTime();
        } catch (Exception e) {
            L.e("isoTimeToLong", "Parse error" + e);
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
                return HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
            case 1:
                return DAY_MONTH_YEAR_1_FORMAT.format(new Date(dateLong));
            case 2:
                return ISO_DATE_FORMAT.format(new Date(dateLong));
            case 3:
                return HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT.format(new Date(dateLong));
            case 4:
                return DAY_MONTH_YEAR_2_FORMAT.format(new Date(dateLong));
            case 5:
                return DAY_MONTH_YEAR_HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
            case 6:
                return HOUR_MINUTE_DAY_MONTH_YEAR_2_FORMAT.format(new Date(dateLong));
            default:
                break;
        }
        return "longToStringFormatNotFound";
    }

    public static long getCurrentTimeInMilliseconds() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.length() < 16;
    }

    public static String convertMToKm(Context context, float distance, int textResId, boolean useMetersIfLessThanOne) {
        String result = "0";
        String format = "%.1f";
        float convertedDistance = distance < 1000 && useMetersIfLessThanOne ? distance : distance / 1000;
        String mOrKm = context.getString(distance < 1000 && useMetersIfLessThanOne ? R.string.distance_m : R.string
                .distance_km);

        if (textResId != 0) {
            result = String.format(context.getString(textResId),
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

    public static int getRandomInt(int max) {
        return new Random().nextInt(max);
    }

    public static int getRandomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static void setEditTextColorByState(Context context, EditText editText, boolean isValidState) {
        if (isValidState) {
            editText.setTextColor(context.getResources().getColor(R.color.grey));
            editText.setHintTextColor(context.getResources().getColor(R.color.grey));
        } else {
            editText.setTextColor(context.getResources().getColor(R.color.red));
            editText.setHintTextColor(context.getResources().getColor(R.color.red));
        }
    }

    public static void setEmailEditTextImageByState(EditText editText, boolean isValidState) {
        if (isValidState) {
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mail_icon_select, 0, 0, 0);
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mail_icon_error, 0, 0, 0);
        }
    }

    public static void setEmailImageByState(ImageView imageView, boolean isValidState) {
        if (isValidState) {
            imageView.setImageResource(R.drawable.mail_icon_select);
        } else {
            imageView.setImageResource(R.drawable.mail_icon_error);
        }
    }

    public static void setPasswordEditTextImageByState(EditText editText, boolean isValidState) {
        if (isValidState) {
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass_icon_select, 0, 0, 0);
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass_icon_error, 0, 0, 0);
        }
    }

    public static void setProfilePhotoImageViewmageByState(ImageView imageView, boolean isValidState) {
        if (!isValidState) {
            imageView.setImageResource(R.drawable.cam_error);
        }
    }

    public static void setActivityBackgroundColor(Activity activity, int color) {
        View view = activity.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    public static String getTimeInDayHoursMinutes(Context context, long timeInMillisecond) {
        int days = (int) timeInMillisecond / 24 / 60 / 60 / 1000;
        int hours = (int) (timeInMillisecond - DateUtils.DAY_IN_MILLIS * days) / 60 / 60 / 1000;
        int minutes = (int) (timeInMillisecond - DateUtils.DAY_IN_MILLIS * days - DateUtils.HOUR_IN_MILLIS * hours) / 60 / 1000;

        String daysText = "";
        if (days != 0) {
            daysText = days + " " + context.getResources().getQuantityString(R.plurals.day, days) + " ";
        }
        String hoursText = "";
        if (hours != 0) {
            hoursText = hours + " " + context.getResources().getQuantityString(R.plurals.hour, hours) + " ";
        }
        String minutesText = "";
        if (minutes != 0) {
            minutesText = minutes + " " + context.getResources().getQuantityString(R.plurals.minute, minutes) + " ";
        }

        return daysText + hoursText + minutesText;
    }

    public static int getSurveyTypeIcon(int surveyType) {
        int iconResId;
        switch (surveyType) {
            case 1:
                iconResId = R.drawable.project_type_1;
                break;
            case 2:
                iconResId = R.drawable.project_type_2;
                break;
            case 3:
                iconResId = R.drawable.project_type_3;
                break;
            case 4:
                iconResId = R.drawable.project_type_4;
                break;
            case 5:
                iconResId = R.drawable.project_type_5;
                break;
            default:
                iconResId = R.drawable.ic_launcher;
                break;
        }
        return iconResId;
    }

    public static int getSurveyTypePopupIcon(int surveyType) {
        int iconResId;
        switch (surveyType) {
            case 1:
                iconResId = R.drawable.project_type_1_popup;
                break;
            case 2:
                iconResId = R.drawable.project_type_2_popup;
                break;
            case 3:
                iconResId = R.drawable.project_type_3_popup;
                break;
            case 4:
                iconResId = R.drawable.project_type_4_popup;
                break;
            case 5:
                iconResId = R.drawable.project_type_5_popup;
                break;
            default:
                iconResId = R.drawable.ic_launcher;
                break;
        }
        return iconResId;
    }
}
