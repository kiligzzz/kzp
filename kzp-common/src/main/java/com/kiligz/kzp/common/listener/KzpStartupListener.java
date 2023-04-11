package com.kiligz.kzp.common.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 程序启动监听器
 *
 * @author Ivan
 * @since 2023/3/14
 */
public abstract class KzpStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        withStartup(event.getApplicationContext());
    }

    /**
     * 随着程序启动的操作
     */
    public abstract void withStartup(ApplicationContext context);
}
