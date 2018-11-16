package com.ros.smartrocket;


import android.text.format.DateUtils;

public class Config {
    public static final String BAIDU_API_KEY = BuildConfig.BAIDU_API_KEY;
    public static final String SERVER_API_KEY = BuildConfig.SERVER_API_KEY;
//    public static final String GCM_SENDER_ID = BuildConfig.GCM_SENDER_ID;
    public static final String SHARE_URL = "http://smart-rocket.com/crew/";

    /*SR-20*/
    public static final String SHARE_URL_ALL_COUNTRIES = "https://play.google.com/store/apps/details?id=com.ros.smartrocket";
    public static final String SHARE_URL_CHINA = "http://android.app.qq.com/myapp/detail.htm?apkName=com.ros.smartrocket";

    public static final int TREE_G_UPLOAD_TASK_LIMIT_MB = 0;
    public static final int TREE_G_UPLOAD_MONTH_LIMIT_MB = 0;

    public static final long DEADLINE_REMINDER_MILLISECONDS = DateUtils.MINUTE_IN_MILLIS;

    public static final String WEB_SERVICE_URL = BuildConfig.WEB_SERVICE_URL;
    public static String PROFILE_PAGE_URL = BuildConfig.PROFILE_PAGE_URL;
    public static String GEOCODER_URL = BuildConfig.GEOCODER_URL;

    public static boolean LOG_ENABLED = BuildConfig.LOG_ENABLED;
    public static boolean USE_BAIDU = BuildConfig.CHINESE;
}
