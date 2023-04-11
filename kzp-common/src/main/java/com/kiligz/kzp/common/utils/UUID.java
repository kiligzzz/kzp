package com.kiligz.kzp.common.utils;

/**
 * 通用唯一标识符
 *
 * @author Ivan
 * @since 2023/3/10
 */
public class UUID {
    /**
     * uuid的默认长度
     */
    public static final int DEFAULT_LENGTH = 12;

    /**
     * 默认长度唯一id
     */
    public static String uuid() {
        return uuid(DEFAULT_LENGTH);
    }

    /**
     * 指定长度唯一id
     */
    public static String uuid(int length) {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 100000);
        String traceId = Long.toString(timestamp, 16) + Integer.toString(random, 16);
        if (traceId.length() > length) {
            traceId = traceId.substring(0, length);
        } else {
            traceId = String.format("%0" + length + "d", Long.parseLong(traceId, 16));
        }
        return traceId;
    }
}
