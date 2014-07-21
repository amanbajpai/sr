package com.ros.smartrocket.utils;

import android.location.Location;

public class ChinaTransformLocation {

    private final static double PI = 3.14159265358979323;
    private final static double a = 6378245.0;
    private final static double ee = 0.00669342162296594323;

    public static void transformLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (!outOfChina(latitude, longitude)) {
                double dLat = transformLat(longitude - 105.0, latitude - 35.0);
                double dLon = transformLon(longitude - 105.0, latitude - 35.0);
                double radLat = latitude / 180.0 * PI;
                double magic = Math.sin(radLat);

                magic = 1 - ee * magic * magic;

                double sqrtMagic = Math.sqrt(magic);

                dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
                dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);

                latitude = latitude + dLat;
                longitude = longitude + dLon;

                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }
    }

    private static boolean outOfChina(double lat, double lon) {
        boolean outOfChina = false;
        if (lon < 72.004 || lon > 137.8347) {
            outOfChina = true;
        }
        if (lat < 0.8293 || lat > 55.8271) {
            outOfChina = true;
        }
        return outOfChina;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
}
