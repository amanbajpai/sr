package com.ros.smartrocket.net;

import com.ros.smartrocket.Config;

import java.util.HashMap;


public final class WSUrl {
    public static final String LOGIN = Config.WEB_SERVICE_URL + "api/Authorize";
    public static final String CHECK_LOCATION = Config.WEB_SERVICE_URL + "api/Authorize/PositionCheck";
    public static final String REGISTRATION = Config.WEB_SERVICE_URL + "api/Authorize/Register";
    public static final String SUBSCRIPTION = Config.WEB_SERVICE_URL + "api/Authorize/RegisterApplicant";
    public static final String GET_SURVEYS = Config.WEB_SERVICE_URL + "api/Surveys?latitude=%s&longitude=%s&radius=%s&language=%s";
    public static final String GET_SURVEYS_TASKS = Config.WEB_SERVICE_URL + "api/Surveys/%s/Tasks";
    public static final String GET_MY_TASKS = Config.WEB_SERVICE_URL + "api/Tasks/ByCurrentUser?language=%s";
    public static final String CLAIM_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Claim";
    public static final String SEND_ANSWERS = Config.WEB_SERVICE_URL + "api/Tasks/Answers";
    public static final String UNCLAIM_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Unclaimed";
    public static final String VALIDATE_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Validate";
    public static final String START_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Start";
    public static final String GCM_REGISTER_DEVICE = Config.WEB_SERVICE_URL + "api/Authorize/RegisterDevice";
    public static final String GCM_TEST_PUSH = Config.WEB_SERVICE_URL + "api/Authorize/PushMessage";
    public static final String GET_MY_ACCOUNT = Config.WEB_SERVICE_URL + "api/Authorize/Account";
    public static final String GET_QUESTIONS = Config.WEB_SERVICE_URL + "api/Surveys/Questionnaire?surveyId=%s&language=%s";
    public static final String GET_REDO_QUESTION = Config.WEB_SERVICE_URL + "api/Tasks/Re-Do-Questions?taskId=%s&language=%s";
    public static final String UPLOAD_TASK_FILE = Config.WEB_SERVICE_URL + "api/Tasks/QuestionFile";

    public static final int LOGIN_ID = 1;
    public static final int GET_SURVEYS_ID = 2;
    public static final int GET_SURVEYS_TASKS_ID = 3;
    public static final int GET_MY_TASKS_ID = 4;
    public static final int REGISTRATION_ID = 5;
    public static final int SUBSCRIPTION_ID = 6;
    public static final int CLAIM_TASKS_ID = 7;
    public static final int CHECK_LOCATION_ID = 8;
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

    public static final HashMap<String, Integer> urls;

    static {
        urls = new HashMap<String, Integer>();
        urls.put(LOGIN, LOGIN_ID);
        urls.put(GET_SURVEYS, GET_SURVEYS_ID);
        urls.put(GET_SURVEYS_TASKS, GET_SURVEYS_TASKS_ID);
        urls.put(GET_MY_TASKS, GET_MY_TASKS_ID);
        urls.put(REGISTRATION, REGISTRATION_ID);
        urls.put(SUBSCRIPTION, SUBSCRIPTION_ID);
        urls.put(CLAIM_TASK, CLAIM_TASKS_ID);
        urls.put(CHECK_LOCATION, CHECK_LOCATION_ID);
        urls.put(GCM_REGISTER_DEVICE, GCM_REGISTER_DEVICE_ID);
        urls.put(GCM_TEST_PUSH, GCM_TEST_PUSH_ID);
        urls.put(GET_MY_ACCOUNT, GET_MY_ACCOUNT_ID);
        urls.put(GET_QUESTIONS, GET_QUESTIONS_ID);
        urls.put(GET_REDO_QUESTION, GET_REDO_QUESTION_ID);
        urls.put(UPLOAD_TASK_FILE, UPLOAD_TASK_FILE_ID);
        urls.put(UNCLAIM_TASK, UNCLAIM_TASKS_ID);
        urls.put(START_TASK, START_TASK_ID);
        urls.put(VALIDATE_TASK, VALIDATE_TASK_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = urls.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
