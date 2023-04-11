package com.kiligz.kzp.common.constant;

import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;

/**
 * kzp程序的信息
 *
 * @author Ivan
 * @since 2023/3/22
 */
@Slf4j
public class Kzp {
    /**
     * 运行标识，程序正在运行或结束
     */
    private static volatile boolean RUNNING = true;

    static {
        // JVM结束时，修改运行标识，可用来处理一些程序结束前的清理、备份等工作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> RUNNING = false));
    }

    /**
     * 返回运行标识
     */
    public static boolean isRunning() {
        return RUNNING;
    }

    /**
     * 是否开启同步redis功能
     */
    public static volatile boolean SYNC_REDIS = false;

    /**
     * 手动退出程序，用于异常情况
     */
    public static void exit() {
        log.error("程序异常退出.");
        new Thread(() -> System.exit(1)).start();
    }

    /**
     * 使用的zoneId
     */
    public static ZoneId zoneId() {
        return ZoneId.systemDefault();
    }
}
