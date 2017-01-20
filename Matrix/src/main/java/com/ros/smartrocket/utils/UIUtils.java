package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.helpshift.support.Support;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.images.ImageLoader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

/**
 * Utils class for easy work with UI Views
 */
public class UIUtils {
    private static final String TAG = "UIUtils";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    private static final SimpleDateFormat ISO_DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ", Locale.ENGLISH);
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
    private static final int MIN_PASSWORD_LANDTH = 8;
    private static final int MAX_PASSWORD_LANDTH = 16;
    private static final Random RANDOM = new Random();

    public static final String DEFAULT_LANG = java.util.Locale.getDefault().toString();
    static final String[] SUPPORTED_LANGS_CODE = new String[]{"en", "zh", "zh_CN", "zh_TW", "en_SG", "zh_HK"};
    public static final String[] VISIBLE_LANGS_CODE = new String[]{"en", "zh_CN", "zh_TW"};
    public static String[] VISIBLE_LANGUAGE = new String[]{"English", "中文（简体）", "中文（繁體）"};

    /**
     * Show simple Toast message
     *
     * @param context - current context
     * @param resId   - resource id
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

    public static void showToastCustomDuration(String text, long durationInMillis) {
        final Toast t = Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        new CountDownTimer(Math.max(durationInMillis - 2000, 1000), 1000) {
            @Override
            public void onFinish() {
                t.show();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                t.show();
            }
        }.start();
    }

    /**
     * Show simple Toast message
     *
     * @param context - current context
     * @param msg
     */
    public static void showSimpleToast(Context context, String msg) {
        showSimpleToast(context, msg, Toast.LENGTH_LONG, Gravity.BOTTOM);
        ;
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
     * @param context  - current activity
     * @param editText
     */

    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;

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
     * Check if Network provider is enabled
     *
     * @param context - current context
     * @return boolean
     */
    public static boolean isNetworkEnabled(Context context) {
        boolean isEnable = false;
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            isEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        return isEnable;
    }


    public static boolean isAllLocationSourceEnabled(Context context) {
        return isGpsEnabled(context) && isNetworkEnabled(context);
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
    public static boolean isMockLocationEnabled(Context context, Location location) {
        boolean isMockLocation = false;
        if (BuildConfig.CHECK_MOCK_LOCATION) {
            if (Build.VERSION.SDK_INT > 18) {
                if (location != null) {
                    isMockLocation = location.isFromMockProvider();
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                        isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
                    }
                } catch (Exception e) {
                    Log.e("Mock location enabled", "Exception", e);
                }
            } else {
                isMockLocation = !android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings
                        .Secure.ALLOW_MOCK_LOCATION).equals("0");
            }
        }
        return isMockLocation;

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
                UIUtils.ISO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
                result = UIUtils.ISO_DATE_FORMAT.parse(dateString).getTime();
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
                result = UIUtils.HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
                break;
            case 1:
                if (isChineLanguage()) {
                    result = UIUtils.DAY_MONTH_YEAR_1_FORMAT_CHINE.format(new Date(dateLong));
                } else {
                    result = UIUtils.DAY_MONTH_YEAR_1_FORMAT.format(new Date(dateLong));
                }
                break;
            case 2:
                UIUtils.ISO_DATE_FORMAT2.setTimeZone(TimeZone.getTimeZone("UTC"));
                String utcDate = UIUtils.ISO_DATE_FORMAT2.format(new Date(dateLong));

                result = utcDate.substring(0, utcDate.length() - 5) + "+00:00";
                break;
            case 3:
                if (isChineLanguage()) {
                    result = UIUtils.HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT_CHINE.format(new Date(dateLong));
                } else {
                    result = UIUtils.HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT.format(new Date(dateLong));
                }
                break;
            case 4:
                result = UIUtils.DAY_MONTH_YEAR_2_FORMAT.format(new Date(dateLong));
                break;
            case 5:
                result = UIUtils.DAY_MONTH_YEAR_HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
                break;
            case 6:
                result = UIUtils.HOUR_MINUTE_DAY_MONTH_YEAR_2_FORMAT.format(new Date(dateLong));
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
     * Check if email is valid
     *
     * @param s - string to check
     * @return boolean
     */
    public static String getNumbersOnly(CharSequence s) {
        return s.toString().replaceAll("[^0-9]", "");
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
        boolean availableLength = password.length() >= UIUtils.MIN_PASSWORD_LANDTH
                && password.length() <= UIUtils.MAX_PASSWORD_LANDTH;

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

        return containUpperLetter && containLowerLetter && containNumber
                && availableLength && containSpecialSymbol;
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
        float convertedDistance
                = distance < UIUtils.METERS_IN_KM && useMetersIfLessThanOne ? distance : distance / UIUtils.METERS_IN_KM;
        String mOrKm = context.getString(distance < UIUtils.METERS_IN_KM && useMetersIfLessThanOne ? R.string.distance_m : R.string
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
        return UIUtils.RANDOM.nextInt(max);
    }

    public static int getRandomInt(int min, int max) {
        return UIUtils.RANDOM.nextInt(max - min + 1) + min;
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

    public static void setRadioButtonsByState(RadioButton maleButton, RadioButton femaleButton, boolean isValid) {
        if (isValid) {
            maleButton.setButtonDrawable(R.drawable.radio_button_male_normal_selector);
            femaleButton.setButtonDrawable(R.drawable.radio_button_female_normal_selector);
        } else {
            maleButton.setButtonDrawable(R.drawable.radio_button_male_error);
            femaleButton.setButtonDrawable(R.drawable.radio_button_female_error);
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
        int days = (int) (timeInMillisecond / DateUtils.DAY_IN_MILLIS);
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
     * @param balance - current balance
     * @return String
     */
    public static String getBalanceOrPrice(Double balance, String symbol, Integer precision, Integer roundingMode) {
        String result = null;
        if (balance != null && precision != null && roundingMode != null) {
            Double roundedBalance = round(balance, precision, roundingMode);
            result = symbol + " " + String.format(Locale.getDefault(), "%." + precision + "f", roundedBalance);
        }

        if (TextUtils.isEmpty(result)) {
            result = symbol + " " + balance;
        }

        return result;
    }

    /**
     * Return price in right format
     *
     * @param balance - current balance
     * @return String
     */
    public static String getBalanceOrPrice(Double balance, String symbol) {
        return getBalanceOrPrice(balance, symbol, null, null);
    }

    public static void setActionBarBackground(AppCompatActivity activity, int statusId, boolean isPreclaim) {
        int backgroundRes;
        switch (TasksBL.getTaskStatusType(statusId)) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (isPreclaim) {
                    backgroundRes = R.drawable.action_bar_violet;
                } else {
                    backgroundRes = R.drawable.action_bar_green;
                }
                break;
            case SCHEDULED:
            case PENDING:
                backgroundRes = R.drawable.action_bar_blue;
                break;
            case COMPLETED:
                backgroundRes = R.drawable.action_bar_grey;
                break;
            case VALIDATION:
                backgroundRes = R.drawable.action_bar_grey;
                break;
            case RE_DO_TASK:
                backgroundRes = R.drawable.action_bar_red;
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                backgroundRes = R.drawable.action_bar_gold;
                break;
            case REJECTED:
                backgroundRes = R.drawable.action_bar_black;
                break;
            default:
                backgroundRes = R.drawable.action_bar_green;
                break;
        }
        activity.getSupportActionBar().setBackgroundDrawable(activity.getResources().getDrawable(backgroundRes));
    }

    public static BitmapDescriptor getPinBitmap(Task task) {
        BitmapDescriptor icon;
        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    if (!task.getIsHide()) {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_violet);
                    } else {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_violet_hidden);
                    }
                } else {
                    if (!task.getIsHide()) {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
                    } else {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green_hidden);
                    }
                }
                break;
            case SCHEDULED:
            case PENDING:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_blue);
                break;
            case COMPLETED:
            case VALIDATION:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_grey);
                break;
            case RE_DO_TASK:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
                break;
            case VALIDATED:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_yellow);
                break;

            default:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
                break;
        }

        return icon;
    }

    public static int getPinResId(Task task) {
        int iconResId;
        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    if (!task.getIsHide()) {
                        iconResId = R.drawable.pin_violet_new;
                    } else {
                        iconResId = R.drawable.pin_violet_hidden_new;
                    }
                } else {
                    if (!task.getIsHide()) {
                        iconResId = R.drawable.pin_green_new;
                    } else {
                        iconResId = R.drawable.pin_green_hidden_new;
                    }
                }
                break;
            case SCHEDULED:
            case PENDING:
                iconResId = R.drawable.pin_blue_new;
                break;
            case COMPLETED:
            case VALIDATION:
                iconResId = R.drawable.pin_grey_hidden_new;
                break;
            case RE_DO_TASK:
                iconResId = R.drawable.pin_red_new;
                break;
            case VALIDATED:
                iconResId = R.drawable.pin_yellow_new;
                break;

            default:
                iconResId = R.drawable.pin_green_new;
                break;
        }

        return iconResId;
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
        return "zh_CN".equals(code) || "en_SG".equals(code) || "zh".equals(code)
                || "zh_TW".equals(code) || "zh_HK".equals(code);
    }

    public static String getDeviceManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return capitalize(manufacturer);
    }

    /**
     * Tries to take second part of device model string, if it starts with manufacture string.
     * Otherwise returns model string.
     *
     * @return device model
     */
    public static String getDeviceModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.length() >= manufacturer.length() && model.startsWith(manufacturer)) {
            return model.replace(manufacturer, "");
        } else {
            return model;
        }
    }

    public static String getDeviceName(Context context) {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName = getProperties(context, "android_models.properties").getProperty(model);

        if (!TextUtils.isEmpty(deviceName)) {
            if (deviceName.startsWith(capitalize(manufacturer))) {
                return deviceName.substring(manufacturer.length() + 1, deviceName.length());
            } else {
                return deviceName;
            }
        } else {
            return model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private static Properties getProperties(Context context, String fileName) {
        Properties properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            properties.load(inputStream);

        } catch (IOException e) {
            L.e("AssetsPropertyReader", e.toString());
        }
        return properties;
    }

    public static long getMemorySize(int type) {
        try {
            Runtime info = Runtime.getRuntime();
            switch (type) {
                case 1:
                    return info.freeMemory();
                case 2:
                    return info.totalMemory() - info.freeMemory();
                case 3:
                    return info.totalMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getCertificateSHA1Fingerprint(Context context) {
        StringBuilder hexString = new StringBuilder();
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            int flags = PackageManager.GET_SIGNATURES;

            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);

                Signature[] signatures = packageInfo.signatures;
                byte[] cert = signatures[0].toByteArray();
                InputStream input = new ByteArrayInputStream(cert);

                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate c = (X509Certificate) cf.generateCertificate(input);

                try {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    byte[] publicKey = md.digest(c.getEncoded());

                    for (int i = 0; i < publicKey.length; i++) {
                        String appendString = Integer.toHexString(0xFF & publicKey[i]);
                        if (appendString.length() == 1) {
                            hexString.append("0");
                        }
                        hexString.append(appendString.toUpperCase());
                        if (i + 1 < publicKey.length) {
                            hexString.append(":");
                        }
                    }

                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hexString.toString();
    }

    public static String getLogs() {
        StringBuilder resultString = new StringBuilder();
        String separator = System.getProperty("line.separator");
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                resultString.append(line);
                resultString.append(separator);
            }
        } catch (Exception e) {
            L.e(TAG, "Error get logs");
        }
        return resultString.toString();
    }

    public static Integer getConnectedNetwork(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                return cm.getActiveNetworkInfo().getType();
            }
        } catch (Exception e) {
            L.e(TAG, "getConnectedNetwork. Get type error.");
        }
        return null;
    }

    public static boolean deviceIsReady(Activity c) {
        boolean result = isOnline(c) && isAllLocationSourceEnabled(c)
                && !isMockLocationEnabled(c, null);
        if (!isOnline(c)) {
            DialogUtils.showNetworkDialog(c);
        } else if (!isAllLocationSourceEnabled(c)) {
            DialogUtils.showLocationDialog(c, true);
        } else if (isMockLocationEnabled(c, null)) {
            DialogUtils.showMockLocationDialog(c, true);
        }
        return result;
    }

    public static boolean isAllFilesSend(String currentEmail) {
        boolean result = true;
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        String lastEmail = preferencesManager.getLastEmail();
        if (!TextUtils.isEmpty(lastEmail) && !lastEmail.equals(currentEmail)) {
            int notUploadedFileCount = FilesBL.getNotUploadedFileCount();
            result = notUploadedFileCount == 0;
        }
        return result;
    }

    public static boolean setDefaultLanguage(Context context, String languageCode) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        boolean languageChanged = !preferencesManager.getLanguageCode().equals(languageCode);
        preferencesManager.setLanguageCode(languageCode);

        Configuration config = context.getResources().getConfiguration();

        if ("zh_CN".equals(languageCode) || "en_SG".equals(languageCode)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            Support.setSDKLanguage("zh_CN");
        } else if ("zh".equals(languageCode) || "zh_TW".equals(languageCode) || "zh_HK".equals(languageCode)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
            Support.setSDKLanguage("zh_TW");
        } else {
            config.locale = new Locale(languageCode);
            Support.setSDKLanguage("en");
        }

        context.getApplicationContext().getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        return languageChanged;
    }

    static String getLanguageCodeFromSupported() {
        for (String lc : SUPPORTED_LANGS_CODE) {
            if (DEFAULT_LANG.equals(lc)) {
                return DEFAULT_LANG;
            }
        }
        return "en";
    }


    public static void setCurrentLanguage() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();

        if (!TextUtils.isEmpty(preferencesManager.getLanguageCode())) {
            UIUtils.setDefaultLanguage(App.getInstance(), preferencesManager.getLanguageCode());
        } else {
            String supportedLanguage = getLanguageCodeFromSupported();
            preferencesManager.setLanguageCode(supportedLanguage);
            UIUtils.setDefaultLanguage(App.getInstance(), supportedLanguage);
        }
    }
}
