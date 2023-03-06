package com.kiligz.kzp.common.utils;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类
 *
 * @author Ivan
 * @date 2022/9/8 16:27
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    private SpringUtil() {
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 获取context
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 获取bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}


