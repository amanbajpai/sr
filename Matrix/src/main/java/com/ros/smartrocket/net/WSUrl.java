package com.ros.smartrocket.net;

import com.ros.smartrocket.Config;

import java.util.HashMap;


public final class WSUrl {
    public static final String LOGIN = Config.WEB_SERVICE_URL + "api/Authorize";
    public static final String FORGOT_PASSWORD = Config.WEB_SERVICE_URL + "api/Authorize/ForgotPassword?email=%s&language=%s";
    public static final String GET_CURRENT_T_AND_C = Config.WEB_SERVICE_URL + "GetTermsAndConditionsCurrentVersion";
    public static final String ACTIVATE_ACCOUNT = Config.WEB_SERVICE_URL + "api/Authorize/Activate";
    public static final String SET_PASSWORD = Config.WEB_SERVICE_URL + "api/Authorize/ChangePassword";
    public static final String CHECK_LOCATION = Config.WEB_SERVICE_URL + "api/Authorize/PositionCheck";
    public static final String REGISTRATION = Config.WEB_SERVICE_URL + "api/Authorize/Register?language=%s";
    public static final String UPLOAD_PHOTO = Config.WEB_SERVICE_URL + "api/Authorize/UpdatePhoto";
    public static final String SUBSCRIPTION = Config.WEB_SERVICE_URL + "api/Authorize/RegisterApplicant";
    public static final String GET_WAVES = Config.WEB_SERVICE_URL + "api/Waves?latitude=%s&longitude=%s&&radius=%s&language=%s";
    public static final String GET_WAVE_TASKS = Config.WEB_SERVICE_URL + "api/Waves/%s/Tasks";
    public static final String GET_MY_TASKS = Config.WEB_SERVICE_URL + "api/Tasks/ByCurrentUser?language=%s";
    public static final String CLAIM_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Claim";
    public static final String SEND_ANSWERS = Config.WEB_SERVICE_URL + "api/Tasks/Answers?language=%s";
    public static final String UNCLAIM_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Unclaimed";
    public static final String VALIDATE_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Validate";
    public static final String START_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Start";
    public static final String REJECT_TASK = Config.WEB_SERVICE_URL + "api/Tasks/Reject";
    public static final String GCM_REGISTER_DEVICE = Config.WEB_SERVICE_URL + "api/Authorize/RegisterDevice";
    public static final String GCM_TEST_PUSH = Config.WEB_SERVICE_URL + "api/Authorize/PushMessage";
    public static final String GET_MY_ACCOUNT = Config.WEB_SERVICE_URL + "api/Authorize/Account?language=%s";
    public static final String GET_QUESTIONS = Config.WEB_SERVICE_URL
            + "api/Waves/Questionnaire?waveId=%s&language=%s";
    public static final String GET_REDO_QUESTION = Config.WEB_SERVICE_URL
            + "api/Tasks/Re-Do-Questions?taskId=%s&language=%s";
    public static final String UPLOAD_TASK_FILE = Config.WEB_SERVICE_URL + "api/Tasks/QuestionFile";
    public static final String GET_REFERRAL_CASES = Config.WEB_SERVICE_URL
            + "api/Authorize/ReferralCases?countryId=%s&language=%s";
    public static final String SAVE_REFERRAL_CASE = Config.WEB_SERVICE_URL + "api/Authorize/ReferralCase";
    public static final String CASHING_OUT = Config.WEB_SERVICE_URL + "WithdrawMoney";
    public static final String GET_SHARING_DATA = Config.WEB_SERVICE_URL + "api/Socials/SharingData?language=%s";

    public static final int LOGIN_ID = 1;
    public static final int GET_WAVES_ID = 2;
    public static final int GET_WAVES_TASKS_ID = 3;
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
    public static final int GET_REFERRAL_CASES_ID = 19;
    public static final int SAVE_REFERRAL_CASE_ID = 20;
    public static final int REJECT_TASK_ID = 21;
    public static final int ACTIVATE_ACCOUNT_ID = 22;
    public static final int UPLOAD_PHOTO_ID = 23;
    public static final int GET_CURRENT_T_AND_C_ID = 24;
    public static final int SET_PASSWORD_ID = 24;
    public static final int CASHING_OUT_ID = 25;
    public static final int GET_SHARING_DATA_ID = 26;

    public static final HashMap<String, Integer> URLS;

    static {
        URLS = new HashMap<String, Integer>();
        URLS.put(LOGIN, LOGIN_ID);
        URLS.put(GET_WAVES, GET_WAVES_ID);
        URLS.put(GET_WAVE_TASKS, GET_WAVES_TASKS_ID);
        URLS.put(GET_MY_TASKS, GET_MY_TASKS_ID);
        URLS.put(REGISTRATION, REGISTRATION_ID);
        URLS.put(SUBSCRIPTION, SUBSCRIPTION_ID);
        URLS.put(CLAIM_TASK, CLAIM_TASKS_ID);
        URLS.put(CHECK_LOCATION, CHECK_LOCATION_ID);
        URLS.put(GCM_REGISTER_DEVICE, GCM_REGISTER_DEVICE_ID);
        URLS.put(GCM_TEST_PUSH, GCM_TEST_PUSH_ID);
        URLS.put(GET_MY_ACCOUNT, GET_MY_ACCOUNT_ID);
        URLS.put(GET_QUESTIONS, GET_QUESTIONS_ID);
        URLS.put(GET_REDO_QUESTION, GET_REDO_QUESTION_ID);
        URLS.put(UPLOAD_TASK_FILE, UPLOAD_TASK_FILE_ID);
        URLS.put(UNCLAIM_TASK, UNCLAIM_TASKS_ID);
        URLS.put(START_TASK, START_TASK_ID);
        URLS.put(VALIDATE_TASK, VALIDATE_TASK_ID);
        URLS.put(GET_REFERRAL_CASES, GET_REFERRAL_CASES_ID);
        URLS.put(SAVE_REFERRAL_CASE, SAVE_REFERRAL_CASE_ID);
        URLS.put(REJECT_TASK, REJECT_TASK_ID);
        URLS.put(ACTIVATE_ACCOUNT, ACTIVATE_ACCOUNT_ID);
        URLS.put(UPLOAD_PHOTO, UPLOAD_PHOTO_ID);
        URLS.put(GET_CURRENT_T_AND_C, GET_CURRENT_T_AND_C_ID);
        URLS.put(SET_PASSWORD, SET_PASSWORD_ID);
        URLS.put(CASHING_OUT, CASHING_OUT_ID);
        URLS.put(GET_SHARING_DATA, GET_SHARING_DATA_ID);
    }

    public static int matchUrl(String url) {
        Integer matchedId = URLS.get(url);
        if (matchedId == null) {
            matchedId = -1;
        }
        return matchedId;
    }
}
