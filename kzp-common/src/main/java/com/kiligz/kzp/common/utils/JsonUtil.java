package com.kiligz.kzp.common.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * json工具类
 *
 * @author Ivan
 * @since 2022/11/8
 */
@Slf4j
public class JsonUtil {
    /**
     * 全局mapper
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 用于redis缓存的mapper
     */
    private static final ObjectMapper redisMapper = new ObjectMapper();

    static {
        redisMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        redisMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static ObjectMapper getRedisMapper() {
        return redisMapper;
    }
}
