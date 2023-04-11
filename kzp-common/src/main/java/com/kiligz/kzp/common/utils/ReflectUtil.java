package com.kiligz.kzp.common.utils;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 反射工具类，高速缓存ASM
 *
 * @author Ivan
 * @since 2022/11/28
 */
public class ReflectUtil {
    /**
     * 方法访问器缓存
     */
    private static final ConcurrentMap<Class<?>, MethodAccess> methodAccessMap = Maps.newConcurrentMap();

    /**
     * 属性访问器缓存
     */
    private static final ConcurrentMap<Class<?>, FieldAccess> fieldAccessMap = Maps.newConcurrentMap();

    /**
     * 获取方法访问器
     */
    public static MethodAccess getMethodAccess(Class<?> clazz) {
        return getAccess(methodAccessMap, clazz, MethodAccess::get);
    }

    /**
     * 获取属性访问器
     */
    public static FieldAccess getFieldAccess(Class<?> clazz) {
        return getAccess(fieldAccessMap, clazz, FieldAccess::get);
    }

    /**
     * 获取访问器
     */
    private static <T> T getAccess(ConcurrentMap<Class<?>, T> accessMap, Class<?> clazz,
                                   Function<Class<?>, T> accessFunction) {
        if (accessMap.containsKey(clazz)) {
            return accessMap.get(clazz);
        }
        T access = accessFunction.apply(clazz);
        accessMap.putIfAbsent(clazz, access);
        return access;
    }
}
