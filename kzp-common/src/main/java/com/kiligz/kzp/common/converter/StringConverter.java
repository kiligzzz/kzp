package com.kiligz.kzp.common.converter;

import com.kiligz.kzp.common.constant.Kzp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 转换工具类
 *
 * @author Ivan
 * @since 2022/11/28
 */
public class StringConverter {

    /*------------------------ 源类型: String ------------------------*/

    /**
     * 时间格式
     */
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * str转为Timestamp类型
     */
    public static Timestamp toTimestamp(String str) {
        LocalDateTime time = LocalDateTime.parse(str, DTF);
        long timeMilli = time.atZone(Kzp.zoneId()).toInstant().toEpochMilli();
        return new Timestamp(timeMilli);
    }

    /**
     * 直接返回str
     */
    public static String toSelf(String str) {
        return str;
    }

    /**
     * 转为int类型
     */
    public static int toInt(String str) {
        return Integer.parseInt(str);
    }
}
