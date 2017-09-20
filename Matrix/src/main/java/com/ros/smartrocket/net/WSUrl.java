package com.ros.smartrocket.net;

import com.ros.smartrocket.Config;

import java.util.HashMap;


public final class WSUrl {

    public static final String GCM_REGISTER_DEVICE = Config.WEB_SERVICE_URL + "api/Authorize/RegisterDevice";
    // Tasks
    public static final String VALIDATE_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Validate";
    public static final String UPLOAD_TASK_FILE = Config.WEB_SERVICE_URL + "api/Tasks/QuestionFile";
    static final int GCM_REGISTER_DEVICE_ID = 9;
    static final int UPLOAD_TASK_FILE_ID = 14;
    static final int VALIDATE_TASK_ID = 17;
    static final HashMap<String, Integer> URLS;

    static {
        URLS = new HashMap<>();
        URLS.put(GCM_REGISTER_DEVICE, GCM_REGISTER_DEVICE_ID);
        URLS.put(UPLOAD_TASK_FILE, UPLOAD_TASK_FILE_ID);
        URLS.put(VALIDATE_TASK, VALIDATE_TASK_ID);
    }

    static int matchUrl(String url) {
        Integer matchedId = URLS.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
