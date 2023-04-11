package com.kiligz.kzp.trace.domain;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kiligz.kzp.common.utils.UUID;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;
import java.time.Instant;

/**
 * 存储链路上下文信息
 *
 * @author Ivan
 * @since 2023/3/10
 */
@Data
public class Trace implements Serializable {
    // 可传播的ThreadLocal
    private static final ThreadLocal<Trace> THREAD_LOCAL = new TransmittableThreadLocal<>();
    // trace的key
    public static final String KEY = "trace";


    // 全局唯一
    private String traceId;
    // 服务唯一
    private String spanId;
    // 请求时间
    private Instant reqTime;

    /**
     * 不允许new
     */
    private Trace() {
    }

    /**
     * http请求，初始化Trace，并放入MDC
     */
    public static void init() {
        Trace trace = new Trace();
        trace.traceId = uuid();
        trace.spanId = uuid();
        trace.reqTime = Instant.now();
        putMDC(trace);
        set(trace);
    }

    /**
     * rpc请求，刷新Trace
     */
    public static void refresh(Trace trace) {
        trace.spanId = uuid();
        putMDC(trace);
        set(trace);
    }

    /**
     * 获取traceId
     */
    public static String traceId() {
        return get().traceId;
    }

    /**
     * 获取spanId
     */
    public static String spanId() {
        return get().spanId;
    }

    /**
     * 获取reqTime
     */
    public static Instant reqTime() {
        return get().reqTime;
    }

    /**
     * 将trace对象放入ThreadLocal中
     */
    private static void set(Trace trace) {
        THREAD_LOCAL.set(trace);
    }

    /**
     * 从ThreadLocal中获取trace对象
     */
    public static Trace get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 删除trace对象并清空MDC
     */
    public static void remove() {
        THREAD_LOCAL.remove();
        MDC.clear();
    }

    /**
     * 唯一id
     */
    private static String uuid() {
        return UUID.uuid();
    }

    /**
     * 放入MDC，用于日志打印
     */
    private static void putMDC(Trace trace) {
        MDC.put("traceId", trace.traceId);
        MDC.put("spanId", trace.spanId);
    }

    /**
     * 清空MDC
     */
    private static void clearMDC() {
        MDC.clear();
    }
}
