package com.matrix;

public interface Keys {
    // Application
    String APP_VERSION = "app_version";
    String TOKEN = "token";


    //Main menu
    String REFRESH_MAIN_MENU = "refresh_main_menu";

    // Update service
    String PROGRESS_STATUS_MESSAGE = "progress_status_message";
    String PROGRESS_STATUS_PERSENT = "progress_status_persent";

    // Operation tags - What is this?
    // TODO: Add more comments here what is this? Where we can use it and how.
    String GET_SURVEYS_OPERATION_TAG = "get_surveys_operation_tag";
    String GET_SURVEYS_TASKS_OPERATION_TAG = "get_surveys_tasks_operation_tag";
    String GET_MY_TASKS_OPERATION_TAG = "get_my_tasks_operation_tag";
    String LOGIN_OPERATION_TAG = "login_operation_tag";
    String REGISTRETION_OPERATION_TAG = "registration_operation_tag";
    String CHECK_LOCATION_OPERATION_TAG = "check_location_operation_tag";
    String SUBSCRIBE_OPERATION_TAG = "subscribe_operation_tag";
    String BOOK_TASK_OPERATION_TAG = "book_task_operation_tag";
    String GCM_REGISTER_DEVICE_TAG = "gcm_register_device_tag";
    String GCM_TEST_PUSH_TAG = "gcm_test_push_tag";

    String CONTENT_TYPE = "content_type";
    String FIND_TASK = "find_task";
    String MY_TASK = "my_task";
    //Tasks
    String TASK_ID = "task_id";
    //Survey
    String SURVEYS = "Surveys";
    String SURVEY_ID = "survey_id";

    //Location
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String COUNTRY_ID = "country_id";
    String COUNTRY_NAME = "country_name";
    String CITY_ID = "city_id";
    String CITY_NAME = "city_name";

    String GROUP_CODE = "group_code";

    String PREFERENCE_CURRENT_LOCATION = "current_location";


    /* ------------ Location parameters -----------------------*/
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 600; // every 10 minutes
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    public static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;


    /* ------------ GCM parameters -----------------------*/
    public static final String GCM_ID = "145199350695";

    public static final String GCM_PROPERTY_REG_ID = "gcm_registration_id";
    public static final String GCM_PROPERTY_APP_VERSION = "gcm_appVersion";
    public static final String GCM_IS_GCMID_REGISTERED = "gcm_id_is_registered";


}
