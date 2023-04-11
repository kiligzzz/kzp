package com.kiligz.kzp.common.utils;

/**
 * 前置条件
 *
 * @author Ivan
 * @since 2022/11/22
 */
public class Preconditions {
    /**
     * 校验对象是否为空
     */
    public static <T> void checkNotNull(T t) {
        checkNotNull(t, null);
    }
    
    /**
     * 校验对象是否为空
     */
    public static <T> void checkNotNull(T t, String format, Object... args) {
        if (t == null) {
            throw format == null ?
                    new NullPointerException() :
                    new NullPointerException(String.format(format, args));
        }
    }
}
