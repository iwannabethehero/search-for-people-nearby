package com.hanlzz.test;

import com.hanlzz.util.DistanceUtil;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Random;
import java.util.stream.DoubleStream;

import static com.hanlzz.util.GeoHash.findNearGrid;
import static com.hanlzz.util.GeoHash.getGeoHashStr;

public class TestDis {
    public static void main(String[] args) {
//        getNearByPeople(36.000001,17.000002);

        getNearByPeople(1.30501, 0.104002);
//        save();
    }

    private static void getNearByPeople(double lon, double lat) {
        Jedis jd = new Jedis("127.0.0.1", 6379);
        System.out.println("距离你200米内的人有 : ");
        long st = System.currentTimeMillis();

        String key = getGeoHashStr(lon, lat, 6);
        String[] grid = findNearGrid(key);

        for (String gd : grid) {
            Map<String, String> map = jd.hgetAll(gd);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String[] sp = entry.getValue().split(",");
                int distance = (int) DistanceUtil.calcDistance(lon, lat, Double.parseDouble(sp[0]), Double.parseDouble(sp[1]));
                if (distance < 200) {
                    System.out.println("前缀 : "+gd+" 微信号 : " + entry.getKey() + " 经度 : " + sp[0] + "  纬度 : " + sp[1] + "  距离你 : " + distance + "米 ");
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("查询完毕! 耗时 : " + (end - st) + "毫秒");
        jd.close();
    }


    private static void save() {
        Random rd = new Random();
        Jedis jd = new Jedis("127.0.0.1", 6379);

        for (int i = 0; i < 500000; i++) {
            double lon = rd.nextDouble() * 2;
            double lat = rd.nextDouble();
            String str = getGeoHashStr(lon, lat, 6);
            String val = lon + "," + lat;
            jd.hset(str, "wx_" + i, val);
            System.out.println(i);
        }
    }
}
