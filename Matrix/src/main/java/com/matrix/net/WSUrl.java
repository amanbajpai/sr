package com.matrix.net;

import com.matrix.Config;

import java.util.HashMap;


public final class WSUrl {
    public final static String GET_TASKS = Config.WEB_SERVICE_URL + "public/1.0/tasks.json"; //TODO EditUrl

    public final static int GET_TASKS_ID = 1;

    public final static HashMap<String, Integer> urls;

    static {
        urls = new HashMap<String, Integer>();
        urls.put(GET_TASKS, GET_TASKS_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = urls.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
