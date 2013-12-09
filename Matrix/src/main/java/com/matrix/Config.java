package com.matrix;


import com.matrix.utils.UIUtils;

public class Config {
    public enum Environment {
        DEVELOPMENT, PRODUCTION;
    }

    public static final Environment ENV = Environment.DEVELOPMENT;
    public static final String CACHE_PREFIX_DIR = "/Android/data/com.matrix/cache/";

    public static final String APP_VERSION;

    public static final String CACHE_DIR = android.os.Environment.getExternalStoragePublicDirectory(CACHE_PREFIX_DIR).getPath();

    public static final int REFRESH_LOCATION_DISTANCE = 100;
    public static final int REFRESH_LOCATION_TIME = 0;

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
                WEB_SERVICE_URL = "http://matrix.api.uran.po.ciklum.net/";
                break;
            case DEVELOPMENT:
            default:
                ACRA_ENABLED = false;
                LOG_ENABLED = true;
                WEB_SERVICE_URL = "http://matrix.api.uran.po.ciklum.net/";
                break;
        }
    }
}
