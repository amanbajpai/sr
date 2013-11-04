package com.matrix;

import android.app.Application;
import com.matrix.utils.UIUtils;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

//TODO Change formKey
@ReportsCrashes(formKey = Config.ACRA_FORM_KEY, customReportContent = { ReportField.REPORT_ID, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
        ReportField.PACKAGE_NAME, ReportField.PHONE_MODEL, ReportField.ANDROID_VERSION, ReportField.TOTAL_MEM_SIZE, ReportField.AVAILABLE_MEM_SIZE, ReportField.IS_SILENT,
        ReportField.STACK_TRACE, ReportField.CRASH_CONFIGURATION, ReportField.DISPLAY, ReportField.USER_EMAIL, ReportField.USER_CRASH_DATE,
        ReportField.SHARED_PREFERENCES })

public class App extends Application {
    private static App instance;

    protected String deviceId;
    protected int deviceApiNumber;
    protected String deviceType;
    
    @Override
    public void onCreate() {
        super.onCreate();
        initACRA();
        instance = this;

        deviceId = UIUtils.getDeviceId(this);
        deviceApiNumber = android.os.Build.VERSION.SDK_INT;
        deviceType = "android";
    }

    protected void initACRA() {
        if (Config.ACRA_ENABLED) {
            ACRA.init(this);
        }
    }

    public static App getInstance() {
        return instance;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceApiNumber() {
        return String.valueOf(deviceApiNumber);
    }

    public String getDeviceType() {
        return deviceType;
    }
}
