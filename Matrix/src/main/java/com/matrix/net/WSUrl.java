package com.matrix.net;

import com.matrix.Config;

import java.util.HashMap;


public final class WSUrl {
    public final static String LOGIN = Config.WEB_SERVICE_URL + "Authorize";
    public final static String REGISTRATION = Config.WEB_SERVICE_URL + "Register";
    public final static String SUBSCRIPTION = Config.WEB_SERVICE_URL + "Subscription";
    public final static String GET_ALL_TASKS = Config.WEB_SERVICE_URL + "public/1.0/alltasks.json"; //TODO EditUrl
    public final static String GET_MY_TASKS = Config.WEB_SERVICE_URL + "public/1.0/mytasks.json"; //TODO EditUrl

    public final static int LOGIN_ID = 1;
    public final static int GET_ALL_TASKS_ID = 2;
    public final static int GET_MY_TASKS_ID = 3;
    public final static int REGISTRATION_ID = 4;
    public final static int SUBSCRIPTION_ID = 5;

    public final static HashMap<String, Integer> urls;

    static {
        urls = new HashMap<String, Integer>();
        urls.put(LOGIN, LOGIN_ID);
        urls.put(GET_ALL_TASKS, GET_ALL_TASKS_ID);
        urls.put(GET_MY_TASKS, GET_MY_TASKS_ID);
        urls.put(REGISTRATION, REGISTRATION_ID);
        urls.put(SUBSCRIPTION, SUBSCRIPTION_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = urls.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
