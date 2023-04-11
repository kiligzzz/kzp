package com.kiligz.kzp.common.constant;

import java.util.Random;

/**
 * 符号常量
 *
 * @author Ivan
 * @since 2022/11/10
 */
public class Consts {
    /*------------------------ kzp ------------------------*/
    public static final String KZP = "kzp";

    /*------------------------ 符号 ------------------------*/
    public static final String BLANK = " ";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String POUND = "#";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String VERTICAL = "|";
    public static final String Hyphen = "-";

    /*---------------------- 文件说明 ----------------------*/
    public static final String EXPLAIN_BEGIN = "<kzp#>";
    public static final String EXPLAIN_END = "<#kzp>";

    /*------------------------ 时间 ------------------------*/
    private static final Random RANDOM = new Random();
    public static final int ONE_HOUR = 60 * 60;
    public static final int ONE_DAY = 60 * 60 * 24;
    public static final long RANDOM_DAY = RANDOM.nextInt(ONE_HOUR) + ONE_DAY;

    /*------------------------ 容量 ------------------------*/
    public static final int DEFAULT_CAPACITY = 100000;

    /*------------------------ 渠道 ------------------------*/
    public static final String SMS = "sms";
    public static final String EMAIL = "email";
    public static final String WECHAT = "wechat";
    public static final String FEI = "fei";
    public static final String DING = "ding";
}
