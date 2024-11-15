package com.cow.util.general;


public class MyUtils {
    public static String getCode(int length) {
        String str ="123456";
        return str.substring(str.length() - length);
    }

    private MyUtils() {
    }
}
