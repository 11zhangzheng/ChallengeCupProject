package com.henu.util.tracecode;

import java.util.UUID;

public class TraceCodeUtil {

    /**
     * 生成溯源码
     *
     * @return 20位溯源码
     */
    public static String generateTraceCode() {
        String part1 = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 3);
        long timestamp = System.currentTimeMillis();
        String part2 = String.valueOf(timestamp);
        String part3 = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4);
        return part1 + part2 + part3;
    }
}
