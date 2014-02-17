package com.ros.smartrocket;


import com.ros.smartrocket.utils.UIUtils;

public class Config {
    public enum Environment {
        DEVELOPMENT, PRODUCTION, STAGING;
    }

    public static final Environment ENV = Environment.DEVELOPMENT;
    public static final String GOOGLE_API_KEY = "AIzaSyDbenpp_65I1QGBbF8wJK8blokzFYKKhwI";
    public static final String DEV_EMAIL = "dmma@ciklum.com";
    public static final String CACHE_PREFIX_DIR = "/Android/data/com.ros.smartrocket/cache/";

    public static final String APP_VERSION;

    public static final String CACHE_DIR = android.os.Environment.getExternalStoragePublicDirectory
            (CACHE_PREFIX_DIR).getPath();
    public static final String LONG_URL_TO_SHARE = "https://play.google.com/store/apps/details?id=com.ua.makeev"
            + ".lovewidgetpaid";
    public static final int REFRESH_LOCATION_DISTANCE = 100;

    public static final int REFRESH_LOCATION_TIME = 0;
    public static final int TREE_G_UPLOAD_TASK_LIMIT_MB = 5;

    public static final int TREE_G_UPLOAD_MONTH_LIMIT_MB = 50;
    public static final int CHECK_NOT_UPLOADED_FILE_MILLISECONDS = 1000 * 60 * 10;
    public static final int SHOW_NOTIFICATION_FOR_NOT_UPLOADED_FILE_MILLISECONDS = 1000 * 60 * 5;
    public static final String ACRA_FORM_KEY = "dFZnWE93RnVfb2VhSHFFR2VicWoyamc6MA";

    public static final boolean ACRA_ENABLED;
    public static final String WEB_SERVICE_URL;

    public static String TERMS_AND_CONDITION_URL;

    public static boolean LOG_ENABLED;

    static {
        APP_VERSION = UIUtils.getAppVersion(App.getInstance());
        switch (ENV) {
            case PRODUCTION:
                ACRA_ENABLED = false;
                LOG_ENABLED = false;
                WEB_SERVICE_URL = "http://matrix.api.uran.po.ciklum.net/";
                TERMS_AND_CONDITION_URL = "http://matrix.web.uran.po.ciklum.net/TermsAndConditions";
                break;
            case STAGING:
                ACRA_ENABLED = true;
                LOG_ENABLED = true;
                WEB_SERVICE_URL = "http://matrix.stage.api.uran.po.ciklum.net/";
                TERMS_AND_CONDITION_URL = "http://matrix.stage.web.uran.po.ciklum.net/TermsAndConditions";
                break;
            case DEVELOPMENT:
            default:
                ACRA_ENABLED = true;
                LOG_ENABLED = true;
                WEB_SERVICE_URL = "http://matrix.api.uran.po.ciklum.net/";
                TERMS_AND_CONDITION_URL = "http://matrix.web.uran.po.ciklum.net/TermsAndConditions";
                break;
        }
    }
}
