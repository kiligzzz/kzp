package com.kiligz.kzp.common.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 转换工具类
 *
 * @author Ivan
 * @since 2022/11/28
 */
public class ConvertUtil {

    /*------------------------ 源类型: String ------------------------*/

    /**
     * 时间格式
     */
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 转为Timestamp类型
     */
    public static Timestamp toTimestamp(String timeStr) {
        LocalDateTime time = LocalDateTime.parse(timeStr, DTF);
        long timeMilli = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Timestamp(timeMilli);
    }
}
