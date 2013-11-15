package com.matrix.net;

import com.matrix.Config;

import java.util.HashMap;


public final class WSUrl {
    public final static String LOGIN = Config.WEB_SERVICE_URL + "Authorize";
    public final static String REGISTRATION = Config.WEB_SERVICE_URL + "Register";
    public final static String SUBSCRIPTION = Config.WEB_SERVICE_URL + "Subscription";
    public final static String GET_SURVEYS = Config.WEB_SERVICE_URL + "Surveys";
    public final static String GET_SURVEYS_TASKS = Config.WEB_SERVICE_URL + "Surveys/%s/Tasks";
    public final static String GET_MY_TASKS = Config.WEB_SERVICE_URL + "public/1.0/mytasks.json"; //TODO EditUrl
    public final static String BOOK_TASKS = Config.WEB_SERVICE_URL + "Tasks/%s/Book"; //TODO EditUrl

    public final static int LOGIN_ID = 1;
    public final static int GET_SURVEYS_ID = 2;
    public final static int GET_SURVEYS_TASKS_ID = 3;
    public final static int GET_MY_TASKS_ID = 4;
    public final static int REGISTRATION_ID = 5;
    public final static int SUBSCRIPTION_ID = 6;
    public final static int BOOK_TASKS_ID = 7;

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
    }

    public static int matchUrl(String url) {
        Integer matchedId = urls.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
