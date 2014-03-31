package com.ros.smartrocket;

import android.app.Application;
import android.content.res.Configuration;
import android.text.format.DateUtils;
import com.google.gson.Gson;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.fragment.SettingsFragment;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import java.util.Calendar;

@ReportsCrashes(formKey = Config.ACRA_FORM_KEY, customReportContent = {ReportField.REPORT_ID,
        ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.PACKAGE_NAME,
        ReportField.PHONE_MODEL, ReportField.ANDROID_VERSION, ReportField.TOTAL_MEM_SIZE,
        ReportField.AVAILABLE_MEM_SIZE, ReportField.IS_SILENT, ReportField.STACK_TRACE,
        ReportField.CRASH_CONFIGURATION, ReportField.DISPLAY, ReportField.USER_EMAIL, ReportField.USER_CRASH_DATE,
        ReportField.SHARED_PREFERENCES})

public class App extends Application {
    private static App instance;

    private String deviceId;
    private int deviceApiNumber;
    private String deviceType;
    private MatrixLocationManager locationManager;
    private MyAccount myAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        initACRA();
        instance = this;

        deviceId = UIUtils.getDeviceId(this);
        deviceApiNumber = android.os.Build.VERSION.SDK_INT;
        deviceType = "android";
        locationManager = new MatrixLocationManager(getApplicationContext());

        SettingsFragment.setCurrentLanguage();
        clearMonthLimitIfNeed();
    }

    protected void initACRA() {
        if (Config.ACRA_ENABLED) {
            ACRA.init(this);
        }
    }

    public static App getInstance() {
        return instance;
    }

    public MyAccount getMyAccount() {
        if (myAccount == null) {
            String profileJson = PreferencesManager.getInstance().getString(Keys.MY_ACCOUNT, "{}");
            myAccount = new Gson().fromJson(profileJson, MyAccount.class);
        }
        return myAccount;
    }

    public void setMyAccount(MyAccount profile) {
        this.myAccount = profile;
        String profileJson = new Gson().toJson(profile);
        PreferencesManager.getInstance().setString(Keys.MY_ACCOUNT, profileJson);
    }

    public static void clearMonthLimitIfNeed() {
        Calendar calendar = Calendar.getInstance();
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        if (calendar.getTimeInMillis() >= preferencesManager.getLastRefreshMonthLimitDate() + DateUtils.YEAR_IN_MILLIS / 12) {
            preferencesManager.setUsed3GUploadMonthlySize(0);
            preferencesManager.setLastRefreshMonthLimitDate(calendar.getTimeInMillis());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SettingsFragment.setCurrentLanguage();
    }

    /**
     * @return
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @return
     */
    public String getDeviceApiNumber() {
        return String.valueOf(deviceApiNumber);
    }

    /**
     * @return
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Get Location Manager for coordinates retrieval
     *
     * @return
     */
    public MatrixLocationManager getLocationManager() {
        return locationManager;
    }
}
