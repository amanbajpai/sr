package com.ros.smartrocket;


import android.text.format.DateUtils;

public class Config {
    public static final String ANDROID_API_KEY = BuildConfig.ANDROID_API_KEY;
    public static final String BAIDU_API_KEY = BuildConfig.BAIDU_API_KEY;
    public static final String SERVER_API_KEY = BuildConfig.SERVER_API_KEY;
    public static final String GCM_SENDER_ID = BuildConfig.GCM_SENDER_ID;
    public static final String KNOWLEDGE_BASE_URL = "https://smartrocket.desk.com/customer/authentication/multipass/callback?multipass=%1$s&signature=%2$s";
    public static final String SHARE_URL = "http://smart-rocket.com/crew/";

    public static final int TREE_G_UPLOAD_TASK_LIMIT_MB = 0;
    public static final int TREE_G_UPLOAD_MONTH_LIMIT_MB = 0;

    public static final int CHECK_NOT_UPLOADED_FILE_MILLISECONDS = 1000 * 60 * 10;
    public static final int SHOW_NOTIFICATION_FOR_NOT_UPLOADED_FILE_MILLISECONDS = 1000 * 60 * 5;
    public static final long DEADLINE_REMINDER_MILLISECONDS = DateUtils.MINUTE_IN_MILLIS;

    public static final String WEB_SERVICE_URL = BuildConfig.WEB_SERVICE_URL;
    public static String PROFILE_PAGE_URL = BuildConfig.PROFILE_PAGE_URL;
    public static String GEOCODER_URL = BuildConfig.GEOCODER_URL;

    public static boolean LOG_ENABLED = BuildConfig.LOG_ENABLED;
    public static boolean USE_BAIDU = BuildConfig.USE_BAIDU;
}
