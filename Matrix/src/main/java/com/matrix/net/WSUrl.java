package com.matrix.net;

import com.matrix.Config;

import java.util.HashMap;


public final class WSUrl {
    public final static String LOGIN = Config.WEB_SERVICE_URL + "api/Authorize";
    public final static String CHECK_LOCATION = Config.WEB_SERVICE_URL + "api/Authorize/PositionCheck";
    public final static String REGISTRATION = Config.WEB_SERVICE_URL + "api/Authorize/Register";
    public final static String SUBSCRIPTION = Config.WEB_SERVICE_URL + "Subscription";
    public final static String GET_SURVEYS = Config.WEB_SERVICE_URL + "api/Surveys?language=%s&lat=%s&long=%s";
    public final static String GET_SURVEYS_TASKS = Config.WEB_SERVICE_URL + "api/Surveys/%s/Tasks";
    public final static String GET_MY_TASKS = Config.WEB_SERVICE_URL + "public/1.0/mytasks.json"; //TODO EditUrl
    public final static String BOOK_TASKS = Config.WEB_SERVICE_URL + "Tasks/%s/Book"; //TODO EditUrl
    public final static String GCM_REGISTER_DEVICE = Config.WEB_SERVICE_URL + "api/Authorize/RegisterDevice";
    public final static String GCM_TEST_PUSH = Config.WEB_SERVICE_URL + "api/Authorize/PushMessage";
    public final static String GET_MY_ACCOUNT = Config.WEB_SERVICE_URL + "api/Authorize/Account";

    public final static int LOGIN_ID = 1;
    public final static int GET_SURVEYS_ID = 2;
    public final static int GET_SURVEYS_TASKS_ID = 3;
    public final static int GET_MY_TASKS_ID = 4;
    public final static int REGISTRATION_ID = 5;
    public final static int SUBSCRIPTION_ID = 6;
    public final static int BOOK_TASKS_ID = 7;
    public final static int CHECK_LOCATION_ID = 8;
    public final static int GCM_REGISTER_DEVICE_ID = 9;
    public final static int GCM_TEST_PUSH_ID = 10;
    public final static int GET_MY_ACCOUNT_ID = 11;

    public final static HashMap<String, Integer> urls;

    static {
        urls = new HashMap<String, Integer>();
        urls.put(LOGIN, LOGIN_ID);
        urls.put(GET_SURVEYS, GET_SURVEYS_ID);
        urls.put(GET_SURVEYS_TASKS, GET_SURVEYS_TASKS_ID);
        urls.put(GET_MY_TASKS, GET_MY_TASKS_ID);
        urls.put(REGISTRATION, REGISTRATION_ID);
        urls.put(SUBSCRIPTION, SUBSCRIPTION_ID);
        urls.put(BOOK_TASKS, BOOK_TASKS_ID);
        urls.put(CHECK_LOCATION, CHECK_LOCATION_ID);
        urls.put(GCM_REGISTER_DEVICE, GCM_REGISTER_DEVICE_ID);
        urls.put(GCM_TEST_PUSH, GCM_TEST_PUSH_ID);
        urls.put(GET_MY_ACCOUNT, GET_MY_ACCOUNT_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = urls.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
