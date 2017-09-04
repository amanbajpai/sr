package com.ros.smartrocket.net;

import com.ros.smartrocket.Config;

import java.util.HashMap;


public final class WSUrl {
    // Authorize
    public static final String LOGIN = Config.WEB_SERVICE_URL + "api/Authorize";
    public static final String UPDATE_USER = Config.WEB_SERVICE_URL + "api/Authorize/UpdateUser";
    public static final String GCM_REGISTER_DEVICE = Config.WEB_SERVICE_URL + "api/Authorize/RegisterDevice";
    public static final String GCM_TEST_PUSH = Config.WEB_SERVICE_URL + "api/Authorize/PushMessage";
    public static final String GET_MY_ACCOUNT = Config.WEB_SERVICE_URL + "api/Authorize/Account?language=%s";
    public static final String GET_NEW_TOKEN = Config.WEB_SERVICE_URL + "api/Authorize/ReIssueCredentials";
    public static final String ALLOW_PUSH_NOTIFICATION = Config.WEB_SERVICE_URL + "api/Authorize/AllowPushNotification";
    public static final String TEST_PUSH_NOTIFICATION = Config.WEB_SERVICE_URL + "api/Authorize/PushBulkMessage";

    // Waves
    public static final String GET_WAVES = Config.WEB_SERVICE_URL +
            "api/Waves?latitude=%s&longitude=%s&radius=%s&language=%s";
    public static final String GET_WAVE_TASKS = Config.WEB_SERVICE_URL + "api/Waves/%s/Tasks";
    public static final String GET_QUESTIONS = Config.WEB_SERVICE_URL
            + "api/Waves/Questionnaire?waveId=%s&language=%s&taskId=%s";

    // Tasks
    public static final String GET_MY_TASKS = Config.WEB_SERVICE_URL + "api/Tasks/ByCurrentUser?language=%s";
    public static final String CLAIM_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Claim";
    public static final String SEND_ANSWERS = Config.WEB_SERVICE_URL + "api/Tasks/Answers?missionId=%s&language=%s";
    public static final String UNCLAIM_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Unclaimed";
    public static final String VALIDATE_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Validate";
    public static final String START_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Start";
    public static final String REJECT_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Reject";
    public static final String GET_REDO_QUESTION = Config.WEB_SERVICE_URL
            + "api/Tasks/Re-Do-Questions?taskId=%s&missionId=%s&language=%s";
    public static final String UPLOAD_TASK_FILE = Config.WEB_SERVICE_URL + "api/Tasks/QuestionFile";

    // Other
    public static final String CASHING_OUT = Config.WEB_SERVICE_URL + "WithdrawMoney";
    public static final String GET_SHARING_DATA = Config.WEB_SERVICE_URL + "api/Socials/SharingData?language=%s";
    public static final String GET_ALIPAY_ACCOUNT = Config.WEB_SERVICE_URL + "api/Payments/AliPayAccount";
    public static final String GET_NATIONAL_ID_ACCOUNT = Config.WEB_SERVICE_URL + "api/Payments/NationalIdAccount";
    public static final String SEND_ACTIVITY = Config.WEB_SERVICE_URL + "SendActivity";
    public static final String GET_WECHAT_TOKEN = "https://api.wechat.com/sns/oauth2/access_token";
    public static final String GET_WECHAT_USER_INFO= "https://api.wechat.com/sns/userinfo";
    public static final String CLOSE_ACCOUNT= LOGIN + "/Terminate";




    public static final int GET_WAVES_ID = 2;
    public static final int GET_WAVES_TASKS_ID = 3;
    public static final int GET_MY_TASKS_ID = 4;
    public static final int CLAIM_TASKS_ID = 7;
    public static final int GCM_REGISTER_DEVICE_ID = 9;
    public static final int GCM_TEST_PUSH_ID = 10;
    public static final int GET_MY_ACCOUNT_ID = 11;
    public static final int GET_QUESTIONS_ID = 12;
    public static final int GET_REDO_QUESTION_ID = 13;
    public static final int UPLOAD_TASK_FILE_ID = 14;
    public static final int UNCLAIM_TASKS_ID = 15;
    public static final int START_TASK_ID = 16;
    public static final int VALIDATE_TASK_ID = 17;
    public static final int SEND_ANSWERS_ID = 18;
    public static final int REJECT_TASK_ID = 21;
    public static final int UPLOAD_PHOTO_ID = 23;
    public static final int CASHING_OUT_ID = 25;
    public static final int GET_SHARING_DATA_ID = 26;
    public static final int GET_NEW_TOKEN_ID = 27;
    public static final int GET_ALIPAY_ACCOUNT_ID = 29;
    public static final int GET_NATIONAL_ID_ACCOUNT_ID = 30;
    public static final int ALLOW_PUSH_NOTIFICATION_ID = 32;
    public static final int TEST_PUSH_NOTIFICATION_ID = 33;

    public static final HashMap<String, Integer> URLS;

    static {
        URLS = new HashMap<>();
        URLS.put(GET_WAVES, GET_WAVES_ID);
        URLS.put(GET_WAVE_TASKS, GET_WAVES_TASKS_ID);
        URLS.put(GET_MY_TASKS, GET_MY_TASKS_ID);
        URLS.put(CLAIM_TASK, CLAIM_TASKS_ID);
        URLS.put(GCM_REGISTER_DEVICE, GCM_REGISTER_DEVICE_ID);
        URLS.put(GCM_TEST_PUSH, GCM_TEST_PUSH_ID);
        URLS.put(GET_MY_ACCOUNT, GET_MY_ACCOUNT_ID);
        URLS.put(GET_QUESTIONS, GET_QUESTIONS_ID);
        URLS.put(GET_REDO_QUESTION, GET_REDO_QUESTION_ID);
        URLS.put(UPLOAD_TASK_FILE, UPLOAD_TASK_FILE_ID);
        URLS.put(UNCLAIM_TASK, UNCLAIM_TASKS_ID);
        URLS.put(START_TASK, START_TASK_ID);
        URLS.put(VALIDATE_TASK, VALIDATE_TASK_ID);
        URLS.put(REJECT_TASK, REJECT_TASK_ID);
        URLS.put(UPDATE_USER, UPLOAD_PHOTO_ID);
        URLS.put(CASHING_OUT, CASHING_OUT_ID);
        URLS.put(GET_SHARING_DATA, GET_SHARING_DATA_ID);
        URLS.put(GET_NEW_TOKEN, GET_NEW_TOKEN_ID);
        URLS.put(GET_ALIPAY_ACCOUNT, GET_ALIPAY_ACCOUNT_ID);
        URLS.put(ALLOW_PUSH_NOTIFICATION, ALLOW_PUSH_NOTIFICATION_ID);
        URLS.put(TEST_PUSH_NOTIFICATION, TEST_PUSH_NOTIFICATION_ID);
        URLS.put(GET_NATIONAL_ID_ACCOUNT, GET_NATIONAL_ID_ACCOUNT_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = URLS.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
