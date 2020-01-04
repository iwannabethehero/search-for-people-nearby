package com.hanlzz.util;

/**
 * @author liets
 */
public class DistanceUtil {

    private final static double EARTH_RADIUS = 6378.137;
    private final static double PI = 3.1415926535;

    public static double calcDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double dRadLat1 = rad(latitude1);
        double dRadLat2 = rad(latitude2);
        double a = dRadLat1 - dRadLat2;
        double b = rad(longitude1) - rad(longitude2);

        double dRad = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(dRadLat1) * Math.cos(dRadLat2) * Math.pow(Math.sin(b / 2), 2)));
        double dDistance = dRad * EARTH_RADIUS;
        dDistance = round(dDistance * 10000) / 10000;
        return Math.abs(dDistance * 1000);
    }

    private static double rad(double dDegree) {
        return dDegree * PI / 180.0;
    }

    private static double round(double d) {
        return Math.floor(d + 0.5);
    }
}
