package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.images.ImageLoader;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Utils class for easy work with UI Views
 */
public class UIUtils {
    private static final String TAG = "UIUtils";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat ISO_DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
    private static final SimpleDateFormat HOUR_MINUTE_1_FORMAT = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_1_FORMAT = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_1_FORMAT_CHINE = new SimpleDateFormat("yyyy年MM月dd日",
            Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_2_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT = new SimpleDateFormat("dd MMM"
            + " yy  HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT_CHINE = new SimpleDateFormat("yyyy年MM月"
            + "dd日  HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_2_FORMAT = new SimpleDateFormat("dd MMM"
            + " yyyy  HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_HOUR_MINUTE_1_FORMAT = new SimpleDateFormat("dd.MM"
            + ".yyyy / HH:mm", Locale.ENGLISH);

    private static final long METERS_IN_KM = 1000;
    private static final Random random = new Random();

    public UIUtils() {
    }

    /**
     * Show simple Toast message
     *
     * @param context - current context
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
     * @param context  - current context
     * @param resId
     * @param duration
     */
    public static void showSimpleToast(Context context, int resId, int duration) {
        showSimpleToast(context, resId, duration, Gravity.BOTTOM);
    }

    public static void showSimpleToast(Context context, int resId, int duration, int gravity) {
        if (context != null) {
            Toast toast = Toast.makeText(context, resId, duration);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
    }

    /**
     * Show simple Toast message
     *
     * @param context - current context
     * @param msg
     */
    public static void showSimpleToast(Context context, String msg) {
        showSimpleToast(context, msg, Toast.LENGTH_SHORT, Gravity.BOTTOM);
    }

    /**
     * Show simple Toast message
     *
     * @param context  - current context
     * @param msg
     * @param duration
     * @param gravity
     */
    public static void showSimpleToast(Context context, String msg, int duration, int gravity) {
        if (context != null && msg != null) {
            Toast toast = Toast.makeText(context, msg, duration);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
    }

    /**
     * Hide soft keyboard
     *
     * @param activity - current activity
     * @param editText
     */

    public static void hideSoftKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Show soft keyboard
     *
     * @param activity - current activity
     */
    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
    }

    /**
     * Get version name visible for users
     *
     * @param context - current context
     * @return String
     */
    public static String getAppVersion(Context context) {
        String currentVersion = "";
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            L.e(TAG, "getAppVersion() Error get app version", e);
        }
        return currentVersion;
    }


    /**
     * Get developer version of application
     *
     * @param context - current context
     * @return int
     */
    public static int getAppVersionCode(Context context) {
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            L.e(TAG, "getAppVersionCode() Error get app version", e);
        }
        return currentVersion;
    }

    /**
     * Check if app is running
     *
     * @param context - current context
     */
    public static boolean isApplicationRuning(Context context) {
        Boolean result = false;
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (topActivity.getPackageName().equals(context.getPackageName())) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * @param context - current context
     * @param pixels  - px
     */
    public static int getDpFromPx(Context context, int pixels) {
        final float d = context.getResources().getDisplayMetrics().density;
        return (int) (pixels / d);
    }

    /**
     * @param context - current context
     * @param dp      - dp
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

    /**
     * Least common multiple between the numbers
     *
     * @param a - number one
     * @param b - number two
     */
    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        int x = a % b;
        return gcd(b, x);
    }

    /**
     * Check if there is Ethernet connection
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isOnline(Context context) {
        boolean isOnline = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    /**
     * Check if there is WiFi connection
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isWiFi(Context context) {
        boolean isOnline = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    /**
     * Check if there is 3G connection
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        boolean isOnline = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    /**
     * Check if GPS is enabled
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isGpsEnabled(Context context) {
        boolean isEnable = false;
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        return isEnable;
    }

    /**
     * Check if GooglePlayServices is enabled
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isGooglePlayServicesEnabled(Context context) {
        boolean isEnable = false;
        if (context != null) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
            isEnable = resultCode == ConnectionResult.SUCCESS;
        }

        return isEnable;
    }

    /**
     * Check if MockLocation is enabled
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isMockLocationEnabled(Context context) {
        boolean result = false;
        if (!Config.CAN_USE_FAKE_LOCATION) {
            result = !"0".equals(Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ALLOW_MOCK_LOCATION));
        }
        return result;
    }

    /**
     * Check if intent is available
     *
     * @param context - current context
     * @param intent  - intent to check
     * @return boolean
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.isEmpty();
    }

    public static String formatAmount(int num) {
        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols();
        decimalFormatSymbol.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbol);
        return decimalFormat.format(num);
    }

    /**
     * Get device id
     *
     * @param context - current context
     * @return boolean
     */
    public static String getDeviceId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * Convert ISO time to long format
     *
     * @param dateString - iso string to formatting
     */
    public static long isoTimeToLong(String dateString) {
        long result = 0;
        if (!TextUtils.isEmpty(dateString)) {
            dateString = dateString.substring(0, dateString.length() - 14)/* + "+00:00"*/;
            try {
                ISO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
                result = ISO_DATE_FORMAT.parse(dateString).getTime();
            } catch (Exception e) {
                L.e("isoTimeToLong", "Parse error" + e, e);
            }
        }
        return result;
    }

    /**
     * Return date string in selected format
     *
     * @param dateLong - date
     * @param formatId - selected format
     * @return String
     */
    public static String longToString(long dateLong, int formatId) {
        String result;
        switch (formatId) {
            case 0:
                result = HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
                break;
            case 1:
                if (isChineLanguage()) {
                    result = DAY_MONTH_YEAR_1_FORMAT_CHINE.format(new Date(dateLong));
                } else {
                    result = DAY_MONTH_YEAR_1_FORMAT.format(new Date(dateLong));
                }
                break;
            case 2:
                ISO_DATE_FORMAT2.setTimeZone(TimeZone.getTimeZone("UTC"));
                String utcDate = ISO_DATE_FORMAT2.format(new Date(dateLong));

                result = utcDate.substring(0, utcDate.length() - 5) + "+00:00";
                break;
            case 3:
                if (isChineLanguage()) {
                    result = HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT_CHINE.format(new Date(dateLong));
                } else {
                    result = HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT.format(new Date(dateLong));
                }
                break;
            case 4:
                result = DAY_MONTH_YEAR_2_FORMAT.format(new Date(dateLong));
                break;
            case 5:
                result = DAY_MONTH_YEAR_HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
                break;
            case 6:
                result = HOUR_MINUTE_DAY_MONTH_YEAR_2_FORMAT.format(new Date(dateLong));
                break;
            default:
                result = "longToStringFormatNotFound";
                break;
        }
        return result;
    }

    public static long getCurrentTimeInMilliseconds() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Check if camera is available
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Check if email is valid
     *
     * @param email - email to check
     * @return boolean
     */
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check if password is valid
     *
     * @param password - password to check
     * @return boolean
     */
    public static boolean isPasswordValid(String password) {
        boolean containUpperLetter = false;
        boolean containLowerLetter = false;
        boolean containNumber = false;
        boolean containSpecialSymbol = false;
        boolean availableLength = password.length() >= 8 && password.length() <= 16;

        char[] charArray = password.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isLetter(charArray[i]) && Character.isUpperCase(charArray[i])) {
                containUpperLetter = true;
            } else if (Character.isLetter(charArray[i]) && Character.isLowerCase(charArray[i])) {
                containLowerLetter = true;
            } else if (Character.isDigit(charArray[i])) {
                containNumber = true;
            } else if (!Character.isLetter(charArray[i]) && !Character.isDigit(charArray[i])) {
                containSpecialSymbol = true;
            }

            if (containUpperLetter && containLowerLetter && containNumber && containSpecialSymbol) {
                break;
            }
        }

        return containUpperLetter && containLowerLetter && containNumber && availableLength && containSpecialSymbol;
    }

    /**
     * Convert Meters to Kilometers
     *
     * @param context                - current context
     * @param distance               - current distance
     * @param textResId              - set result distance in this resource
     * @param useMetersIfLessThanOne - if false will use KM only
     * @return String
     */
    public static String convertMToKm(Context context, float distance, int textResId, boolean useMetersIfLessThanOne) {
        String result;
        String format = "%.1f";
        float convertedDistance = distance < METERS_IN_KM && useMetersIfLessThanOne ? distance : distance / METERS_IN_KM;
        String mOrKm = context.getString(distance < METERS_IN_KM && useMetersIfLessThanOne ? R.string.distance_m : R.string
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

    public static int getRandomInt(int max) {
        return random.nextInt(max);
    }

    public static int getRandomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
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

    public static void setSpinnerBackgroundByState(Spinner spinner, boolean isValidState) {
        if (isValidState) {
            spinner.setBackgroundResource(R.drawable.spinner_green);
        } else {
            spinner.setBackgroundResource(R.drawable.spinner_red);
        }
        spinner.setPadding(0, 0, 0, 0);
    }

    public static void setCheckBoxBackgroundByState(CheckBox checkBox, boolean isValidState) {
        if (!isValidState) {
            checkBox.setButtonDrawable(R.drawable.check_box_error);
        }
    }

    public static void setActivityBackgroundColor(Activity activity, int color) {
        View view = activity.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    public static String getTimeInDayHoursMinutes(Context context, long timeInMillisecond) {
        int days = (int) Math.floor(timeInMillisecond / DateUtils.DAY_IN_MILLIS);
        int hours = (int) Math.floor((timeInMillisecond - DateUtils.DAY_IN_MILLIS * days) / DateUtils.HOUR_IN_MILLIS);
        int minutes = (int) Math.floor((timeInMillisecond - DateUtils.DAY_IN_MILLIS * days
                - DateUtils.HOUR_IN_MILLIS * hours) / DateUtils.MINUTE_IN_MILLIS);

        String daysText = "";
        if (days != 0) {
            daysText = days + " " + context.getResources().getQuantityString(R.plurals.day, days) + " ";
        }
        String hoursText = "";
        if (hours != 0) {
            hoursText = hours + " " + context.getResources().getQuantityString(R.plurals.hour, hours) + " ";
        }
        String minutesText = "";
        if (days == 0 && minutes != 0) {
            minutesText = minutes + " " + context.getResources().getQuantityString(R.plurals.minute, minutes) + " ";
        }

        return daysText + hoursText + minutesText;
    }

    public static void showWaveTypeActionBarIcon(final BaseActivity activity, String url) {
        ImageLoader.getInstance().loadBitmap(url, new ImageLoader.OnFetchCompleteListener() {
            @Override
            public void onFetchComplete(final Bitmap bitmap) {
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = new BitmapDrawable(activity.getResources(), bitmap);
                            activity.getSupportActionBar().setIcon(drawable);
                        }
                    });
                }
            }
        });
    }

    public static void showWaveTypeIcon(final Activity activity, final ImageView iconImageView, String url) {
        ImageLoader.getInstance().loadBitmap(url, new ImageLoader.OnFetchCompleteListener() {
            @Override
            public void onFetchComplete(final Bitmap bitmap) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iconImageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    /**
     * Return price in right format
     *
     * @param context - current context
     * @param balance - current balance
     * @return String
     */
    public static String getBalanceOrPrice(Context context, Double balance, String symbol) {
        String result = symbol + " ";
        /*String balanceString = String.valueOf(balance);
        int countAfterPoint = balanceString.substring(balanceString.lastIndexOf(".")+1,
                balanceString.length()).length();
        if (countAfterPoint > 0) {
            result = result + String.format(Locale.US, "%.2f", balance);
        } else {
            result = result + String.format(Locale.US, "%.0f", balance);
        }*/
        return result + balance;
    }

    public static void setActionBarBackground(ActionBarActivity activity, int statusId) {
        int backgroundRes;
        switch (TasksBL.getTaskStatusType(statusId)) {
            case none:
            case claimed:
            case started:
                backgroundRes = R.drawable.action_bar_green;
                break;
            case scheduled:
            case pending:
                backgroundRes = R.drawable.action_bar_blue;
                break;
            case completed:
                backgroundRes = R.drawable.action_bar_grey;
                break;
            case validation:
                backgroundRes = R.drawable.action_bar_grey;
                break;
            case reDoTask:
                backgroundRes = R.drawable.action_bar_red;
                break;
            case validated:
            case inPaymentProcess:
            case paid:
                backgroundRes = R.drawable.action_bar_gold;
                break;
            case rejected:
                backgroundRes = R.drawable.action_bar_black;
                break;
            default:
                backgroundRes = R.drawable.action_bar_green;
                break;
        }
        activity.getSupportActionBar().setBackgroundDrawable(activity.getResources().getDrawable(backgroundRes));
    }

    /**
     * Return number of hours in millisecond
     *
     * @param hoursCount - current hoursCount
     * @return long
     */
    public static long getHoursAsMilliseconds(int hoursCount) {
        return hoursCount * DateUtils.HOUR_IN_MILLIS;
    }

    public static void transformLocation() {

    }

    public static boolean isChineLanguage() {
        String code = PreferencesManager.getInstance().getLanguageCode();
        return code.contains("zh");
    }
}
