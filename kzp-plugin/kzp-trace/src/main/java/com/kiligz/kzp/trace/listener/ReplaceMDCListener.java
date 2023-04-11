package com.kiligz.kzp.trace.listener;

import com.kiligz.kzp.common.listener.KzpStartupListener;
import org.slf4j.TtlMDCAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 替换MDC的监听器
 *
 * @author Ivan
 * @since 2023/3/31
 */
@Component
public class ReplaceMDCListener extends KzpStartupListener {
    @Override
    public void withStartup(ApplicationContext context) {
        TtlMDCAdapter.replace();
    }
}
