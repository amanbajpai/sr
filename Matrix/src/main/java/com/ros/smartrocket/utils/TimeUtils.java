package com.ros.smartrocket.utils;

import java.util.Calendar;

/**
 * Created by macbook on 22.10.15.
 */
public class TimeUtils {

    public static String getFormattedTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.YEAR);
    }
}
