package com.hanlzz.util;

/**
 * better than geohash 在中国好使
 * @author liets
 */
public class HansHash {
    //len 5253 km         wid 5677 km
    // lon 73 ~ 136
    // lat 3 ~ 54

    public static void main(String[] args) {
        double v = DistanceUtil.calcDistance(100, 3, 100, 54);
        System.out.println(v);
    }

    public static int getHansHashStr(double x, double y) {
        int p = (int)((x-70)*100);
        int q = (int)(y*100);
        return (p<<15)|q;
    }

    public static int[] findNearGrid(double x, double y,int area){
        int p = (int)((x-70)*100 - area);
        int q = (int)(y*100 - area);
        int idx = 0,d= 2*area+1;
        int[] ans = new int[d*d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                ans[idx++] = ((p+i)<<15)|(q+j);
            }
        }
        return ans;
    }
}
