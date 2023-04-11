package com.kiligz.kzp.common.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 程序终止监听器
 *
 * @author Ivan
 * @since 2023/3/14
 */
public abstract class KzpStopListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        withStop(event.getApplicationContext());
    }

    /**
     * 随着程序终止的操作
     */
    public abstract void withStop(ApplicationContext context);
}
