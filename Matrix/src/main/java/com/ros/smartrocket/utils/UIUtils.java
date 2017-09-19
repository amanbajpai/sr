package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.utils.image.ActionBarIconTarget;
import com.ros.smartrocket.utils.image.WaveTypeIconTarget;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

public class UIUtils {
    private static final String TAG = "UIUtils";
    private static final SimpleDateFormat GOOGLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    private static final SimpleDateFormat ISO_DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_1_FORMAT = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_1_FORMAT_CHINA = new SimpleDateFormat("yyyy年MM月dd日",
            Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT_CHINE = new SimpleDateFormat("yyyy年MM月"
            + "dd日  HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_2_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_MINUTE_DAY_MONTH_YEAR_2_FORMAT = new SimpleDateFormat("dd MMM"
            + " yyyy  HH:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat DAY_MONTH_YEAR_HOUR_MINUTE_1_FORMAT = new SimpleDateFormat("dd.MM"
            + ".yyyy / HH:mm", Locale.ENGLISH);

    private static final long METERS_IN_KM = 1000;
    private static final int MIN_PASSWORD_LANDTH = 8;
    private static final int MAX_PASSWORD_LANDTH = 16;
    private static final Random RANDOM = new Random();

    private static final int MAX_LOG_SIZE = 80000;

    public static void showSimpleToast(Context context, int resId) {
        if (context != null) {
            Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

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

    public static void showSimpleToast(Context context, String msg) {
        showSimpleToast(context, msg, Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public static void showSimpleToast(Context context, String msg, int duration, int gravity) {
        if (context != null && msg != null) {
            Toast toast = Toast.makeText(context, msg, duration);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
    }

    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static String getAppVersion(Context context) {
        String currentVersion = "";
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            L.e(TAG, "getAppVersion() Error get app version", e);
        }
        return currentVersion;
    }

    public static int getAppVersionCode(Context context) {
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            L.e(TAG, "getAppVersionCode() Error get app version", e);
        }
        return currentVersion;
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;

    }

    public static int getDpFromPx(Context context, int pixels) {
        final float d = context.getResources().getDisplayMetrics().density;
        return (int) (pixels / d);
    }

    public static int getPxFromDp(Context context, int dp) {
        final float d = context.getResources().getDisplayMetrics().density;
        return (int) (dp * d);
    }

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    public static boolean isOnline(Context context) {
        boolean isOnline = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    public static boolean isWiFi(Context context) {
        boolean isOnline = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    public static boolean is3G(Context context) {
        boolean isOnline = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return isOnline;
    }

    public static boolean isGpsEnabled(Context context) {
        boolean isEnable = false;
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        return isEnable;
    }

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

    public static boolean isGooglePlayServicesEnabled(Context context) {
        boolean isEnable = false;
        if (context != null) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
            isEnable = resultCode == ConnectionResult.SUCCESS;
        }

        return isEnable;
    }

    public static boolean isMockLocationEnabled(Context context, Location location) {
        boolean isMockLocation = false;
        boolean isMockLocationNew = false;
        if (BuildConfig.CHECK_MOCK_LOCATION) {
            if (Build.VERSION.SDK_INT > 18) {
                if (location != null) {
                    isMockLocationNew = location.isFromMockProvider();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                    isMockLocationNew = isMockLocation || (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
                } catch (Exception e) {
                    Log.e("Mock location enabled", "Exception", e);
                }
            } else {
                isMockLocation = !android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings
                        .Secure.ALLOW_MOCK_LOCATION).equals("0");
            }
        }
        return isMockLocation || isMockLocationNew;

    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    public static String getDeviceId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

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

    public static String googleProfileDateToString(String dateString) {
        long result = 0;
        if (!TextUtils.isEmpty(dateString)) {
            try {
                UIUtils.GOOGLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
                result = UIUtils.GOOGLE_DATE_FORMAT.parse(dateString).getTime();
            } catch (Exception e) {
                L.e("isoTimeToLong", "Parse error" + e, e);
            }
        }
        return longToString(result, 2);
    }

    public static String longToString(long dateLong, int formatId) {
        String result;
        switch (formatId) {
            case 0:
                result = UIUtils.HOUR_MINUTE_1_FORMAT.format(new Date(dateLong));
                break;
            case 1:
                result = getLanguageRelatedDate(dateLong);
                break;
            case 3:
                result = getLanguageRelatedDateTime(dateLong);
                break;
            case 2:
                UIUtils.ISO_DATE_FORMAT2.setTimeZone(TimeZone.getTimeZone("UTC"));
                String utcDate = UIUtils.ISO_DATE_FORMAT2.format(new Date(dateLong));
                result = utcDate.substring(0, utcDate.length() - 5) + "+00:00";
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

    private static String getLanguageRelatedDateTime(long dateLong) {
        String result;
        if (LocaleUtils.isChinaLanguage()) {
            result = UIUtils.HOUR_MINUTE_DAY_MONTH_YEAR_1_FORMAT_CHINE.format(new Date(dateLong));
        } else {
            Locale locale = LocaleUtils.getCurrentLocale();
            result = new SimpleDateFormat("dd MMM yy  HH:mm a", locale).format(new Date(dateLong));
        }
        return result;
    }

    private static String getLanguageRelatedDate(long dateLong) {
        String result;
        if (LocaleUtils.isChinaLanguage()) {
            result = UIUtils.DAY_MONTH_YEAR_1_FORMAT_CHINA.format(new Date(dateLong));
        } else {
            Locale locale = LocaleUtils.getCurrentLocale();
            result = new SimpleDateFormat("dd MMMM yyyy", locale).format(new Date(dateLong));
        }
        return result;
    }

    public static long getCurrentTimeInMilliseconds() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getNumbersOnly(CharSequence s) {
        return s.toString().replaceAll("[^0-9]", "");
    }

    public static boolean isPasswordValid(String password) {
        boolean containUpperLetter = false;
        boolean containLowerLetter = false;
        boolean containNumber = false;
        boolean containSpecialSymbol = false;
        boolean availableLength = password.length() >= UIUtils.MIN_PASSWORD_LANDTH
                && password.length() <= UIUtils.MAX_PASSWORD_LANDTH;

        char[] charArray = password.toCharArray();
        for (char aCharArray : charArray) {
            if (Character.isLetter(aCharArray) && Character.isUpperCase(aCharArray)) {
                containUpperLetter = true;
            } else if (Character.isLetter(aCharArray) && Character.isLowerCase(aCharArray)) {
                containLowerLetter = true;
            } else if (Character.isDigit(aCharArray)) {
                containNumber = true;
            } else if (!Character.isLetter(aCharArray) && !Character.isDigit(aCharArray)) {
                containSpecialSymbol = true;
            }

            if (containUpperLetter && containLowerLetter && containNumber && containSpecialSymbol) {
                break;
            }
        }

        return containUpperLetter && containLowerLetter && containNumber
                && availableLength && containSpecialSymbol;
    }


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

    public static int getRandomInt(int max) {
        return UIUtils.RANDOM.nextInt(max);
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
        LocaleUtils.setCompoundDrawable(editText, isValidState
                ? R.drawable.mail_icon_select
                : R.drawable.mail_icon_error);
    }

    public static void setEmailImageByState(ImageView imageView, boolean isValidState) {
        if (isValidState) {
            imageView.setImageResource(R.drawable.mail_icon_select);
        } else {
            imageView.setImageResource(R.drawable.mail_icon_error);
        }
    }

    public static void setPasswordEditTextImageByState(EditText editText, boolean isValidState) {
        LocaleUtils.setCompoundDrawable(editText, isValidState
                ? R.drawable.pass_icon_select
                : R.drawable.pass_icon_error);
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
        Picasso.with(activity).load(url).into(new ActionBarIconTarget(activity));
    }

    public static void showWaveTypeIcon(final Activity activity, final ImageView iconImageView, String url) {
        Picasso.with(activity).load(url).into(new WaveTypeIconTarget(activity, iconImageView));
    }

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

    public static String getDeviceManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return capitalize(manufacturer);
    }

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
            while ((line = bufferedReader.readLine()) != null && resultString.length() <= MAX_LOG_SIZE) {
                resultString.append(line);
                resultString.append(separator);
            }
        } catch (Exception e) {
            L.e(TAG, "Error get logs");
        }
        return resultString.toString();
    }

    public static boolean isDeviceReady(Activity c) {
        boolean result = isOnline(c) && isAllLocationSourceEnabled(c)
                && !isMockLocationEnabled(c, App.getInstance().getLocationManager().getLocation());
        if (!isOnline(c)) {
            DialogUtils.showNetworkDialog(c);
        } else if (!isAllLocationSourceEnabled(c)) {
            DialogUtils.showLocationDialog(c, true);
        } else if (isMockLocationEnabled(c, App.getInstance().getLocationManager().getLocation())) {
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

    public static int getMaxAudioWaveSize() {
        return getScreenWidth() / getPxFromDp(App.getInstance(), 1);
    }

    private static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}

