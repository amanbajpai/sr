package com.ros.smartrocket;

import android.text.format.DateUtils;

public interface Keys {
    // Application
    String TOKEN = "token";
    String TOKEN_FOR_UPLOAD_FILE = "token_for_upload_file";
    String TOKEN_UPDATE_DATE = "token_update_date";
    String SHORT_URL_TO_SHARE = "short_url_to_share";
    String BIT_MASK_SOCIAL_NETWORK = "bit_mask_social_network";
    String LAST_EMAIL = "last_email";
    String EMAIL = "email";
    String LAST_PASSWORD = "last_password";
    String LAST_APP_VERSION = "last_app_version";
    String ACTIVATE_ACCOUNT = "Activate";
    String FORGOT_PASS = "SetNewPassword";
    String BITMAP_FILE_PATH = "bitmap_file_path";
    String VIDEO_FILE_PATH = "video_file_path";
    String ROTATE_BY_EXIF = "rotate_by_exif";
    String FINISH_MAIN_ACTIVITY = "finish_main_activity";
    String UPLOAD_FILES_PROGRESS = "upload_files_progress";
    String REGISTRATION_PERMISSIONS = "registration_permissions";
    String SHOULD_SHOW_MAIN_SCREEN = "registration_permissions";

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
    String SHOW_HIDDEN_TASKS = "show_hidden_tasks";
    String SHOW_HIDDEN_PROJECT = "show_hidden_project";
    String IS_FIRST_LOGIN = "is_first_login";
    String SHOW_ACTIVITY_DIALOG = "show_activity_dialog";

    //Main menu
    String REFRESH_MAIN_MENU = "refresh_main_menu";
    String REFRESH_MAIN_MENU_MY_TASK_COUNT = "refresh_main_menu_my_task_count";

    //Push notification
    String REFRESH_PUSH_NOTIFICATION_LIST = "refresh_push_notifications_list";

    // Operation tags - Using for cache response from NetworkService
    String GET_WAVES_OPERATION_TAG = "get_waves_operation_tag";
    String GET_REFERRAL_CASES_OPERATION_TAG = "get_referral_cases_operation_tag";
    String SAVE_REFERRAL_CASES_OPERATION_TAG = "save_referral_cases_operation_tag";
    String GET_WAVE_TASKS_OPERATION_TAG = "get_wave_tasks_operation_tag";
    String GET_QUESTIONS_OPERATION_TAG = "get_questions_operation_tag";
    String GET_REDO_QUESTION_OPERATION_TAG = "get_redo_questions_operation_tag";
    String GET_MY_TASKS_OPERATION_TAG = "get_my_tasks_operation_tag";
    String LOGIN_OPERATION_TAG = "login_operation_tag";
    String FORGOT_PASSWORD_OPERATION_TAG = "forgot_password_operation_tag";
    String GET_CURRENT_T_AND_C_OPERATION_TAG = "get_current_t_and_c_operation_tag";
    String REGISTRATION_OPERATION_TAG = "registration_operation_tag";
    String UPDATE_USER_OPERATION_TAG = "upload_photo_operation_tag";
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
    String SEND_LOG_OPERATION_TAG = "send_log_operation_tag";
    String UPLOAD_TASK_TEMP_FILE_OPERATION_TAG = "upload_task_temp_file_operation_tag";
    String GCM_REGISTER_DEVICE_TAG = "gcm_register_device_tag";
    String GCM_TEST_PUSH_TAG = "gcm_test_push_tag";
    String ACTIVATE_ACCOUNT_OPERATION_TAG = "activate_account_operation_tag";
    String SET_PASSWORD_OPERATION_TAG = "set_password_operation_tag";
    String CASHING_OUT_OPERATION_TAG = "cashing_out_operation_tag";
    String GET_SHARING_DATA_OPERATION_TAG = "get_sharing_data_operation_tag";
    String GET_NEW_TOKEN_OPERATION_TAG = "get_new_token_operation_tag";
    String GET_ALIPAY_ACCOUNT_OPERATION_TAG = "get_alipay_operation_tag";
    String GET_NATIONAL_ID_ACCOUNT_OPERATION_TAG = "get_national_id_operation_tag";
    String INTEGRATE_ALIPAY_ACCOUNT_OPERATION_TAG = "integrate_alipay_operation_tag";
    String INTEGRATE_NATIONAL_ID_ACCOUNT_OPERATION_TAG = "integrate_national_id_operation_tag";
    String SEND_ALIPAY_SMS_OPERATION_TAG = "send_alipay_sms_operation_tag";
    String ALLOW_PUSH_NOTIFICATION_OPERATION_TAG = "allow_push_notification_operation_tag";
    String TEST_PUSH_NOTIFICATION_OPERATION_TAG = "test_push_notification_operation_tag";
    String SEND_ACTIVITY_OPERATION_TAG = "send_activity_operation_tag";


    // Supported presentation modes for Tasks
    String CONTENT_TYPE = "content_type";
    String FIND_TASK = "find_task";
    String MY_TASK = "my_task";
    String TASK = "task";

    String MAP_MODE_VIEWTYPE = "map_mode_viewtype";
    String MAP_VIEW_ITEM_ID = "map_view_item_id";

    /**
     * Notification activity
     */
    String TITLE_BACKGROUND_COLOR_RES_ID = "title_background_color_res_id";
    String TITLE_ICON_RES_ID = "title_icon_res_id";
    String NOTIFICATION_TITLE = "notification_title";
    String NOTIFICATION_TEXT = "notification_text";
    String NOTIFICATION_TYPE_ID = "notification_type_id";
    String TASK_STATUS_ID = "task_status_id";
    String LEFT_BUTTON_RES_ID = "left_button_res_id";
    String RIGHT_BUTTON_RES_ID = "right_button_res_id";
    String SHOW_LEFT_BUTTON = "show_left_button";
    String SHOW_PUSH_NOTIF_STAR = "show_push_notif_star";

    /**
     * All possible view modes on map
     */
    enum MapViewMode {
        ALL_TASKS,
        MY_TASKS,
        WAVE_TASKS,
        SINGLE_TASK
    }

    //Tasks
    String TASK_ID = "task_id";
    String MISSION_ID = "mission_id";

    //Wave
    String WAVES = "Waves";
    String WAVE = "wave";
    String WAVE_ID = "wave_id";
    String IS_PRECLAIM = "is_preclaim";
    String STATUS_ID = "status_id";

    //Location
    String REFERRAL_CASES_ID = "referral_cases_id";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String COUNTRY_ID = "country_id";
    String DISTRICT_ID = "district_id";
    String COUNTRY_NAME = "country_name";
    String CITY_ID = "city_id";
    String CITY_NAME = "city_name";

    String GROUP_CODE = "group_code";
    String PROMO_CODE = "promo_code";

    String PREFERENCE_CURRENT_LOCATION = "current_location";

    //My account
    String MY_ACCOUNT = "my_account";

    //Question
    String QUESTION = "question";
    String LAST_NOT_ANSWERED_QUESTION_ORDER_ID = "last_not_answered_question_order_id";
    String FIRSTLY_SELECTION = "firstly_selection";
    String IS_REDO = "is_redo";
    String IS_REDO_REOPEN = "is_redo_reopen";
    String KEY_IS_PREVIEW = "is_preview";

    //NotUploadFile Service
    String ACTION_CHECK_NOT_UPLOADED_FILES = "check_not_uploaded_files";


    /* ------------ Location parameters -----------------------*/
    // Update frequency in seconds. every 30 seconds
    int UPDATE_INTERVAL_IN_SECONDS = 30;
    // Update frequency in milliseconds
    long UPDATE_INTERVAL = DateUtils.SECOND_IN_MILLIS * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    long FASTEST_INTERVAL = DateUtils.SECOND_IN_MILLIS * FASTEST_INTERVAL_IN_SECONDS;


    /* ------------ GCM parameters -----------------------*/
    String GCM_PROPERTY_REG_ID = "gcm_registration_id";
    String GCM_IS_GCMID_REGISTERED = "gcm_id_is_registered";

    //NotUploadFile Service
    String ACTION_START_REMINDER_TIMER = "start_reminder_timer";
    String ACTION_STOP_REMINDER_TIMER = "stop_reminder_timer";

    /* ------------ GCM parameters -----------------------*/
    String TALKING_DATA_ROW = "1068797126043F9E289911065DBB39D1";
    String TALKING_DATA_CHINA = "5BF23DC93A19BA33C9984729D825E53C";

}
