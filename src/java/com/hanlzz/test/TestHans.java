package com.hanlzz.test;

import com.hanlzz.util.DistanceUtil;
import com.hanlzz.util.HansHash;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TestHans {
    public static void main(String[] args) throws InterruptedException {
//        for (int i = 0; i < 100; i++) {
//            new Thread(() -> getNearByPeople(111.4, 39.9)).start();
//        }
//        Thread.sleep(10000);
//        Jedis jd = new Jedis("yixius.cn", 6379);
//        Long aLong = jd.dbSize();


//        System.out.println(UUID.randomUUID().toString().substring(0,6));
//        getNearByPeople(1.30501, 0.104002);
//        save();
    }

    private static void getNearByPeople(double lon, double lat) {
        Jedis jd = new Jedis("yixius.cn", 6379);
//        System.out.println("距离你1000米内的人有 : ");
        long st = System.currentTimeMillis();

//        String key = getGeoHashStr(lon, lat, 6);
        int[] grid = HansHash.findNearGrid(lon, lat, 1);

        for (int gd : grid) {
            Map<String, String> map = jd.hgetAll(String.valueOf(gd));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String[] sp = entry.getValue().split(",");
                int distance = (int) DistanceUtil.calcDistance(lon, lat, Double.parseDouble(sp[0]), Double.parseDouble(sp[1]));
//                if (distance < 1000) {
                System.out.println("前缀 : " + gd + " 微信号 : " + entry.getKey() + " 经度 : " + sp[0] + "  纬度 : " + sp[1] + "  距离你 : " + distance + "米 ");
//                }
            }
            break;
        }

        long end = System.currentTimeMillis();
        System.out.println("查询完毕! 耗时 : " + (end - st) + "毫秒");
        jd.close();
    }


    private static void save() throws InterruptedException {
        Random rd = new Random();
        Jedis jd = new Jedis("127.0.0.1", 6379);

        for (int i = 0; i < 100000; i++) {
            double lon = 73 + rd.nextDouble() * 50;
            double lat = 3 + rd.nextDouble() * 50;
            int str = getHansHashStr(lon, lat);
            String val = lon + "," + lat;
            try {
                jd.hset(String.valueOf(str), "wx_" + UUID.randomUUID().toString().substring(0, 6), val);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                jd.close();
                jd = new Jedis("127.0.0.1", 6379);
            }
        }
        jd.close();
        System.out.println("done");
    }

    private static int getHansHashStr(double x, double y) {
        int p = (int) ((x - 70) * 100);
        int q = (int) (y * 100);
        return (p << 15) | q;
    }
}
