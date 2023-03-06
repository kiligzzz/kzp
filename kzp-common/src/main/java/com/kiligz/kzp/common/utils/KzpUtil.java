package com.kiligz.kzp.common.utils;

/**
 * @author Ivan
 * @since 2022/11/22
 */
public class KzpUtil {

    public static <T> void checkNotNull(T t) {
        if (t == null)
            throw new NullPointerException("不能为空");
    }
}
