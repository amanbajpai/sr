package com.ros.smartrocket;

public interface Keys {
    // Application
    String APP_VERSION = "app_version";
    String TOKEN = "token";
    String SHORT_URL_TO_SHARE = "short_url_to_share";

    //Settings
    String LANGUAGE_CODE = "language_code";
    String DEADLINE_REMINDER_MILLISECOND = "deadline_reminder_millisecond";
    String USE_ONLY_WI_FI_CONNACTION = "use_only_wifi_connaction";
    String USE_LOCATION_SERVICES = "use_location_services";
    String USE_SOCIAL_SHARING = "use_social_sharing";
    String USE_SAVE_IMAGE_TO_CAMERA_ROLL = "use_save_image_to_camera_roll";
    String USE_PUSH_MESSAGES = "use_push_messages";
    String USE_DEADLINE_REMINDER = "use_deadline_reminder";
    String TREE_G_UPLOAD_TASK_LIMIT = "tree_g_upload_package_limit";
    String TREE_G_UPLOAD_MONTH_LIMIT = "tree_g_upload_month_limit";
    String USED_TREE_G_UPLOAD_MONTHLY_SIZE = "used_tree_g_upload_monthly_size";
    String LAST_REFRESH_MONTH_LIMIT_DATE = "last_refresh_moth_limit_date";
    String LAST_LEVEL_NUMBER = "last_level_number";
    String DEFAULT_RADIUS = "default_radius";

    //Main menu
    String REFRESH_MAIN_MENU = "refresh_main_menu";

    // Update service
    String PROGRESS_STATUS_MESSAGE = "progress_status_message";
    String PROGRESS_STATUS_PERSENT = "progress_status_persent";

    // Operation tags - Using for cache response from NetworkService
    String GET_SURVEYS_OPERATION_TAG = "get_surveys_operation_tag";
    String GET_REFERRAL_CASES_OPERATION_TAG = "get_referral_cases_operation_tag";
    String SAVE_REFERRAL_CASES_OPERATION_TAG = "save_referral_cases_operation_tag";
    String GET_SURVEYS_TASKS_OPERATION_TAG = "get_surveys_tasks_operation_tag";
    String GET_QUESTIONS_OPERATION_TAG = "get_questions_operation_tag";
    String GET_REDO_QUESTION_OPERATION_TAG = "get_redo_questions_operation_tag";
    String GET_MY_TASKS_OPERATION_TAG = "get_my_tasks_operation_tag";
    String LOGIN_OPERATION_TAG = "login_operation_tag";
    String FORGOT_PASSWORD_OPERATION_TAG = "forgot_password_tag";
    String REGISTRATION_OPERATION_TAG = "registration_operation_tag";
    String CHECK_LOCATION_OPERATION_TAG = "check_location_operation_tag";
    String SUBSCRIBE_OPERATION_TAG = "subscribe_operation_tag";
    String CLAIM_TASK_OPERATION_TAG = "claim_task_operation_tag";
    String SEND_ANSWERS_OPERATION_TAG = "send_answers_operation_tag";
    String UNCLAIM_TASK_OPERATION_TAG = "unclaim_task_operation_tag";
    String VALIDATE_TASK_OPERATION_TAG = "validate_task_operation_tag";
    String START_TASK_OPERATION_TAG = "start_task_operation_tag";
    String REJECT_TASK_OPERATION_TAG = "reject_task_operation_tag";
    String GET_MY_ACCOUNT_OPERATION_TAG = "get_my_account_operation_tag";
    String UPLOAD_TASK_FILE_OPERATION_TAG = "upload_task_file_operation_tag";
    String UPLOAD_TASK_TEMP_FILE_OPERATION_TAG = "upload_task_temp_file_operation_tag";
    String GCM_REGISTER_DEVICE_TAG = "gcm_register_device_tag";
    String GCM_TEST_PUSH_TAG = "gcm_test_push_tag";
    String ACTIVATE_ACCOUNT_OPERATION_TAG = "activate_account_operation_tag";

    // Supported presentation modes for Tasks
    String CONTENT_TYPE = "content_type";
    String FIND_TASK = "find_task";
    String MY_TASK = "my_task";

    String MAP_MODE_VIEWTYPE = "map_mode_viewtype";
    String MAP_VIEWITEM_ID = "map_viewitem_id";
    /**
     * All possible view modes on map
     */
    public enum MapViewMode {
        ALLTASKS,
        MYTASKS,
        SURVEYTASKS,
        SINGLETASK;
    }

    //Tasks
    String TASK_ID = "task_id";
    //Survey
    String SURVEYS = "Surveys";
    String SURVEY = "survey";
    String SURVEY_ID = "survey_id";
    String NEAR_TASK_ID = "near_task_id";

    //Location
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String COUNTRY_ID = "country_id";
    String COUNTRY_NAME = "country_name";
    String CITY_ID = "city_id";
    String CITY_NAME = "city_name";

    String GROUP_CODE = "group_code";

    String PREFERENCE_CURRENT_LOCATION = "current_location";

    //My account
    String MY_ACCOUNT = "my_account";

    //Question
    String QUESTION = "question";
    String LAST_NOT_ANSWERED_QUESTION_ORDER_ID = "last_not_answered_question_order_id";
    String SHOW_RECHECK_ANSWERS_BUTTON = "show_recheck_answers_button";

    //NotUploadFile Service
    String ACTION_CHECK_NOT_UPLOADED_FILES = "check_not_uploaded_files";


    /* ------------ Location parameters -----------------------*/
    // Milliseconds per second
    int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    int UPDATE_INTERVAL_IN_SECONDS = 600; // every 10 minutes
    // Update frequency in milliseconds
    long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;


    /* ------------ GCM parameters -----------------------*/
    String GCM_ID = "145199350695";

    String GCM_PROPERTY_REG_ID = "gcm_registration_id";
    String GCM_PROPERTY_APP_VERSION = "gcm_appVersion";
    String GCM_IS_GCMID_REGISTERED = "gcm_id_is_registered";

    //NotUploadFile Service
    String ACTION_START_REMINDER_TIMER = "start_reminder_timer";
    String ACTION_STOP_REMINDER_TIMER = "stop_reminder_timer";
}
