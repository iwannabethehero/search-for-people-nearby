package com.hanlzz.test;

import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch cd = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    save();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    cd.countDown();
                }
            }).start();
        }
        cd.await();
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
