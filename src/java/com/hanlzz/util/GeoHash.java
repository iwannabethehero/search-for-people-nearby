package com.hanlzz.util;

import java.util.HashMap;

public class GeoHash {

    private static final char[] BASE32 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
            'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    /**
     * b  c  f  g  u  v  y  z
     * 8  9  d  e  s  t  w  x
     * 2  3  6  7  k  m  q  r
     * 0  1  4  5  h  j  n  p
     */
    private static final char[][] BASE32_ODD = {{'0', '1', '4', '5', 'h', 'j', 'n', 'p'}, {'2', '3', '6', '7', 'k', 'm', 'q', 'r'},
            {'8', '9', 'd', 'e', 's', 't', 'w', 'x'}, {'b', 'c', 'f', 'g', 'u', 'v', 'y', 'z'}};


    /**
     * p  r  x  z
     * n  q  w  y
     * j  m  t  v
     * h  k  s  u
     * 5  7  e  g
     * 4  6  d  f
     * 1  3  9  c
     * 0  2  8  b
     */
    private static final char[][] BASE32_EVEN = {{'0', '2', '8', 'b'}, {'1', '3', '9', 'c'}, {'4', '6', 'd', 'f'}, {'5', '7', 'e', 'g'},
            {'h', 'k', 's', 'u'}, {'j', 'm', 't', 'v'}, {'n', 'q', 'w', 'y'}, {'p', 'r', 'x', 'z'}};

    /**
     * 0000 0000 0000 0000 0000 1111 1111 1111
     * rldu    x    y
     */
    private static final HashMap<Character, Integer> ODD_MAP = new HashMap<>();
    private static final HashMap<Character, Integer> EVEN_MAP = new HashMap<>();

    static {
        for (int i = 0; i < BASE32_EVEN.length; i++) {
            for (int j = 0; j < BASE32_EVEN[0].length; j++) {
                EVEN_MAP.put(BASE32_EVEN[i][j], (j << 4) | i);
            }
        }
        for (int i = 0; i < BASE32_ODD.length; i++) {
            for (int j = 0; j < BASE32_ODD[0].length; j++) {
                ODD_MAP.put(BASE32_ODD[i][j], (j << 4) | i);
            }
        }
    }


    /**
     * @param x 经度
     * @param y 纬度
     * @return base32 GeoHashCode
     */
    public static String getGeoHashStr(double x, double y, int len) {
        long bit = fillBinaryBit(x, y, len);
        char[] cs = new char[len];
        for (int i = len - 1; i > -1; i--) {
            cs[i] = BASE32[(int) bit & 31];
            bit >>= 5;
        }
        return new String(cs);
    }

    public static void main(String[] args) {
        //wtw37qtd5
        System.out.println(getGeoHashStr(121.43960190000007, 31.1932993, 9));
        String[] wx4gs = findNearGrid("wx4g");
        for (String s : wx4gs) {
            System.out.print(s + '\t');
        }
    }

    private static long fillBinaryBit(double x, double y, int len) {
        double xc = 0, yc = 0;
        final double xk = 360, yk = 180;
        int f = 4;
        long ans = 0;
        len = len * 5;
        for (int i = 0; i < len; i++, f *= 2) {
            boolean bx = x > xc;
            ans = (ans << 1) | (bx ? 1 : 0);
            xc += (bx ? xk / f : -xk / f);
            if (++i == len) {
                break;
            }
            ans = (ans << 1) | ((bx = y > yc) ? 1 : 0);
            yc += (bx ? yk / f : -yk / f);
        }
        return ans;
    }

    public static String[] findNearGrid(String geoHash) {
        int len = geoHash.length();
        char[] cs = geoHash.toCharArray();

        String[] ans = new String[9];
        ans[0] = geoHash;
        ans[1] = findArea(cs, len - 1, -1, -1);
        ans[2] = findArea(cs, len - 1, -1, 0);
        ans[3] = findArea(cs, len - 1, -1, 1);
        ans[4] = findArea(cs, len - 1, 0, -1);
        ans[5] = findArea(cs, len - 1, 0, 1);
        ans[6] = findArea(cs, len - 1, 1, -1);
        ans[7] = findArea(cs, len - 1, 1, 0);
        ans[8] = findArea(cs, len - 1, 1, 1);
        return ans;
    }


    private static String findArea(char[] cs, int idx, int up, int right) {
        boolean odd = (idx & 1) == 0;
        HashMap<Character, Integer> grid = odd ? EVEN_MAP : ODD_MAP;
        int lenX = odd ? 8 : 4, lenY = odd ? 4 : 8;

        int info = grid.get(cs[idx]);
        int x = (info & 15) + up;
        int y = (info >> 4) + right;
        boolean overX = false, overY = false;
        if (x == lenX) {
            x = 0;
            overX = true;
        } else if (x == -1) {
            x = lenX - 1;
            overX = true;
        }
        if (y == lenY) {
            y = 0;
            overY = true;
        } else if (y == -1) {
            y = lenY - 1;
            overY = true;
        }
        if (idx == 0) {
            overX = overY = false;
        }
        char tmp = cs[idx];
        cs[idx] = odd ? BASE32_ODD[y][x] : BASE32_EVEN[y][x];
        String ans;
        if (overX || overY) {
            ans = findArea(cs, idx - 1, overX ? up : 0, overY ? right : 0);
        } else {
            ans = new String(cs);
        }
        cs[idx] = tmp;
        return ans;
    }
}
