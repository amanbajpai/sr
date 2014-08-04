package com.ros.smartrocket;


import android.text.format.DateUtils;
import com.ros.smartrocket.utils.UIUtils;

public class Config {
    public enum Environment {
        DEVELOPMENT, PRODUCTION, STAGING
    }

    public static final Environment ENV = Environment.DEVELOPMENT;
    public static final String GOOGLE_API_KEY = "AIzaSyDbenpp_65I1QGBbF8wJK8blokzFYKKhwI";
    public static final String DEV_EMAIL = "dmma@ciklum.com";
    public static final String CACHE_PREFIX_DIR = "/Android/data/com.ros.smartrocket/cache/";

    public static final String APP_VERSION;

    public static final String CACHE_DIR = android.os.Environment.getExternalStoragePublicDirectory
            (CACHE_PREFIX_DIR).getPath();
    public static final String KNOWLEDGE_BASE_URL = "https://smartrocket.desk.com/";

    public static final int TREE_G_UPLOAD_TASK_LIMIT_MB = 0;
    public static final int TREE_G_UPLOAD_MONTH_LIMIT_MB = 0;

    public static final int CHECK_NOT_UPLOADED_FILE_MILLISECONDS = 1000 * 60 * 10;
    public static final int SHOW_NOTIFICATION_FOR_NOT_UPLOADED_FILE_MILLISECONDS = 1000 * 60 * 5;
    public static final long DEADLINE_REMINDER_MILLISECONDS = DateUtils.MINUTE_IN_MILLIS;
    public static final String ACRA_FORM_KEY = "dFZnWE93RnVfb2VhSHFFR2VicWoyamc6MA";

    public static final boolean ACRA_ENABLED;
    public static final String WEB_SERVICE_URL;

    public static String TERMS_AND_CONDITION_URL;
    public static String PROFILE_PAGE_URL;

    public static boolean LOG_ENABLED;
    public static final boolean CAN_USE_FAKE_LOCATION = false; //TODO Remove in production

    static {
        APP_VERSION = UIUtils.getAppVersion(App.getInstance());
        switch (ENV) {
            case PRODUCTION:
                ACRA_ENABLED = false;
                LOG_ENABLED = false;
                WEB_SERVICE_URL = "";
                TERMS_AND_CONDITION_URL = "";
                PROFILE_PAGE_URL = "";
                break;
            case STAGING:
                ACRA_ENABLED = true;
                LOG_ENABLED = true;
                WEB_SERVICE_URL = "http://api.staging.redoceansolutions.com/";
                TERMS_AND_CONDITION_URL = "http://web.staging.redoceansolutions" +
                        ".com/TermsAndConditions?withoutMenu=true&language=%s&version=%s";
                PROFILE_PAGE_URL = "http://web.staging.redoceansolutions.com/Account/Manage";
                break;
            case DEVELOPMENT:
            default:
                ACRA_ENABLED = true;
                LOG_ENABLED = true;
                WEB_SERVICE_URL = "http://dev.api.matrix.osiris.pp.ciklum.com/";
                TERMS_AND_CONDITION_URL = "http://dev.web.matrix.osiris.pp.ciklum" +
                        ".com/TermsAndConditions?withoutMenu=true&language=%s&version=%s";
                PROFILE_PAGE_URL = "http://dev.web.matrix.osiris.pp.ciklum.com/Account/Manage";
                break;
        }
    }
}
