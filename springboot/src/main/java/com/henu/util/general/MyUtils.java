package com.henu.util.general;


public class MyUtils {
    private MyUtils() {
    }

    public static String getCode(int length) {
        String str = "123456";
        return str.substring(str.length() - length);
    }
}
