package com.cow.util.tracecode;

import java.util.UUID;

public class TraceCodeUtil {

    private static String getNewUUID() {
        String Part1 = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 3);
        long timestamp = System.currentTimeMillis();
        String Part2 = String.valueOf(timestamp);
        String Part3 = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4);
        return Part1+Part2+Part3;
    }
}
