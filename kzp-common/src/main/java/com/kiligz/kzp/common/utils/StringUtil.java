package com.kiligz.kzp.common.utils;

import cn.hutool.core.util.StrUtil;
import com.kiligz.kzp.common.constant.Consts;
import org.springframework.util.StringUtils;

/**
 * String工具类，包装其它String工具类的方法（可维护性、可扩展性）
 *
 * @author Ivan
 * @since 2022/11/28
 */
public class StringUtil {
    /**
     * 将下划线方式命名的字符串转换为驼峰式
     */
    public static String toCamelCase(String str) {
        return StrUtil.toCamelCase(str);
    }

    /**
     * 将首字母大写
     */
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /**
     * 去除文件说明
     */
    public static String removeExplain(String str) {
        if (str == null) return null;

        while (str.contains(Consts.EXPLAIN_BEGIN)) {
            String pre = str.substring(0, str.indexOf(Consts.EXPLAIN_BEGIN));
            String suf = str.substring(str.indexOf(Consts.EXPLAIN_END) + Consts.EXPLAIN_END.length());
            str = pre + suf;
        }
        return str;
    }
}
