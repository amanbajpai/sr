package com.matrix;


import com.matrix.utils.UIUtils;

public class Config {
    public enum Environment {
        DEVELOPMENT, PRODUCTION;
    }

    public static final Environment ENV = Environment.DEVELOPMENT;

    public static final String APP_VERSION;

    public static final String CACHE_DIR = android.os.Environment.getExternalStoragePublicDirectory(
            "/Android/data/com.matrix/cache/").getPath();

    public static final String ACRA_FORM_KEY = "";
    public static final boolean ACRA_ENABLED;

    public static final String WEB_SERVICE_URL;

    public static boolean LOG_ENABLED;

    static {
        APP_VERSION = UIUtils.getAppVersion(App.getInstance());
        switch (ENV) {
            case PRODUCTION:
                LOG_ENABLED = false;
                ACRA_ENABLED = false;
                WEB_SERVICE_URL = "";
                break;
            case DEVELOPMENT:
            default:
                ACRA_ENABLED = false;
                LOG_ENABLED = true;
                WEB_SERVICE_URL = "";
                break;
        }
    }
}
