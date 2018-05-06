package com.uuch.android_zxinglibrary.utils;

/**
 * Created by Ribbit on 2017/10/23.
 */

public class rsa {
    static int Power(int a, int m, int n)
    {
        if (m == 1) return a%n;
        if (m == 2) return a*a%n;
        if (m % 2 == 1) {
            int temp = Power(a, m / 2, n) % n;
            temp = temp*temp%n;
            temp = temp*a%n;
            return temp;
        }
        else {
            int temp = Power(a, m / 2, n) % n;
            temp = temp*temp%n;
            return temp;
        }
    }
}
