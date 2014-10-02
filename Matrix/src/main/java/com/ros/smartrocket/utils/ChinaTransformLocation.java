package com.ros.smartrocket.utils;

import android.location.Location;

public class ChinaTransformLocation {
    private static final String TAG = ChinaTransformLocation.class.getSimpleName();
    private static final double PI = 3.14159265358979323;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;
    private static final double OPEN_ANGLE = 180.0;
    private static final double CHINE_LONG_MIN = 72.004;
    private static final double CHINE_LONG_MAX = 137.8347;
    private static final double CHINE_LAT_MIN = 0.8293;
    private static final double CHINE_LAT_MAX = 55.8271;

    private static final double HONG_KONG_LONG_1 = 113.994141;
    private static final double HONG_KONG_LONG_2 = 114.399261;
    private static final double HONG_KONG_LONG_3 = 113.841705;
    private static final double HONG_KONG_LONG_4 = 114.399261;
    private static final double HONG_KONG_LAT_1 = 22.436715;
    private static final double HONG_KONG_LAT_2 = 22.510317;
    private static final double HONG_KONG_LAT_3 = 22.127925;
    private static final double HONG_KONG_LAT_4 = 22.436715;

    private static final double MACAU_LONG_1 = 113.528595;
    private static final double MACAU_LONG_2 = 113.569107;
    private static final double MACAU_LONG_3 = 113.545074;
    private static final double MACAU_LONG_4 = 113.601379;
    private static final double MACAU_LAT_1 = 22.173809;
    private static final double MACAU_LAT_2 = 22.213768;
    private static final double MACAU_LAT_3 = 22.105343;
    private static final double MACAU_LAT_4 = 22.173809;

    private static final double MN_0_1 = 0.1;
    private static final double MN_0_2 = 0.2;
    private static final double MN_2 = 2.0;
    private static final double MN_3 = 3.0;
    private static final double MN_6 = 6.0;
    private static final double MN_12 = 12.0;
    private static final double MN_20 = 20.0;
    private static final double MN_30 = 30.0;
    private static final double MN_35 = 35.0;
    private static final double MN_40 = 40.0;
    private static final double MN_100 = 100.0;
    private static final double MN_105 = 105.0;
    private static final double MN_150 = 150.0;
    private static final double MN_160 = 160.0;
    private static final double MN_300 = 300.0;
    private static final double MN_320 = 320.0;

    public ChinaTransformLocation() {

    }

    public static void transformLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (!outOfChina(latitude, longitude)) {
                double dLat = transformLat(longitude - MN_105, latitude - MN_35);
                double dLon = transformLon(longitude - MN_105, latitude - MN_35);
                double radLat = latitude / OPEN_ANGLE * PI;
                double magic = Math.sin(radLat);

                magic = 1 - EE * magic * magic;

                double sqrtMagic = Math.sqrt(magic);

                dLat = (dLat * OPEN_ANGLE) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
                dLon = (dLon * OPEN_ANGLE) / (A / sqrtMagic * Math.cos(radLat) * PI);

                latitude = latitude + dLat;
                longitude = longitude + dLon;

                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }
    }

    private static boolean outOfChina(double lat, double lon) {
        boolean outOfChina = false;
        if (lon < CHINE_LONG_MIN || lon > CHINE_LONG_MAX) {
            outOfChina = true;
        }
        if (lat < CHINE_LAT_MIN || lat > CHINE_LAT_MAX) {
            outOfChina = true;
        }

        if (
            //Hong-Kong
                (lon > HONG_KONG_LONG_1 && lon < HONG_KONG_LONG_2
                        && lat > HONG_KONG_LAT_1 && lat < HONG_KONG_LAT_2)
                        || (lon > HONG_KONG_LONG_3 && lon < HONG_KONG_LONG_4
                        && lat > HONG_KONG_LAT_3 && lat <= HONG_KONG_LAT_4)

                        //Macau
                        || (lon > MACAU_LONG_1 && lon < MACAU_LONG_2 && lat > MACAU_LAT_1 && lat <= MACAU_LAT_2)
                        || (lon > MACAU_LONG_3 && lon < MACAU_LONG_4 && lat > MACAU_LAT_3 && lat <= MACAU_LAT_4)) {
            outOfChina = true;
        }

        L.i(TAG, "outOfChina = " + outOfChina);

        return outOfChina;
    }

    private static double transformLat(double x, double y) {
        double ret = -MN_100 + MN_2 * x + MN_3 * y + MN_0_2 * y * y + MN_0_1 * x * y + MN_0_2 * Math.sqrt(Math.abs(x));
        ret += (MN_20 * Math.sin(MN_6 * x * PI) + MN_20 * Math.sin(MN_2 * x * PI)) * MN_2 / MN_3;
        ret += (MN_20 * Math.sin(y * PI) + MN_40 * Math.sin(y / MN_3 * PI)) * MN_2 / MN_3;
        ret += (MN_160 * Math.sin(y / MN_12 * PI) + MN_320 * Math.sin(y * PI / MN_30)) * MN_2 / MN_3;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = MN_300 + x + MN_2 * y + MN_0_1 * x * x + MN_0_1 * x * y + MN_0_1 * Math.sqrt(Math.abs(x));
        ret += (MN_20 * Math.sin(MN_6 * x * PI) + MN_20 * Math.sin(MN_2 * x * PI)) * MN_2 / MN_3;
        ret += (MN_20 * Math.sin(x * PI) + MN_40 * Math.sin(x / MN_3 * PI)) * MN_2 / MN_3;
        ret += (MN_150 * Math.sin(x / MN_12 * PI) + MN_300 * Math.sin(x / MN_30 * PI)) * MN_2 / MN_3;
        return ret;
    }
}
