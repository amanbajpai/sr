package com.matrix.net;

import com.matrix.Config;

import java.util.HashMap;


public final class WSUrl {
    public final static String LOGIN = Config.WEB_SERVICE_URL + "Authorize";
    public final static String REGISTRATION = Config.WEB_SERVICE_URL + "Register";
    public final static String GET_TASKS = Config.WEB_SERVICE_URL + "public/1.0/tasks.json"; //TODO EditUrl

    public final static int LOGIN_ID = 1;
    public final static int GET_TASKS_ID = 2;
    public final static int REGISTRATION_ID = 3;

    public final static HashMap<String, Integer> urls;

    static {
        urls = new HashMap<String, Integer>();
        urls.put(LOGIN, LOGIN_ID);
        urls.put(GET_TASKS, GET_TASKS_ID);
        urls.put(REGISTRATION, REGISTRATION_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = urls.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
