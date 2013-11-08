package com.matrix;

public interface Keys {
    // Application
    String APP_VERSION = "app_version";

    //Main menu
    String REFRESH_MAIN_MENU = "refresh_main_menu";

    // Update service
    String PROGRESS_STATUS_MESSAGE = "progress_status_message";
    String PROGRESS_STATUS_PERSENT = "progress_status_persent";

    String ACTION = "action";

    int ACTION_GET_TASKS = 1;
    // last 1

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
}
