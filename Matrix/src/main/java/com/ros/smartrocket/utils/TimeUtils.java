package com.ros.smartrocket.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtils {
    public static final int ONE_SECOND_IN_MILL = 1000;

    public static String getFormattedTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.YEAR);
    }

    public static String toTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }
}
