package com.kiligz.kzp.scheduler.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * xxl-job配置
 *
 * @author Ivan
 * @since 2022/12/2
 */
@Slf4j
@Configuration
public class XxlJobConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${xxl-job.admin.address}")
    private String adminAddresses;

    @Value("${xxl-job.accessToken}")
    private String accessToken;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath("./logs/xxl-job");
        xxlJobSpringExecutor.setLogRetentionDays(1);
        return xxlJobSpringExecutor;
    }
}
