package com.kiligz.kzp.satoken.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.same.SaSameUtil;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.vo.RespVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * gateway下游服务的sa-token配置，禁止直接访问下游服务
 * gateway不使用该配置
 *
 * @author Ivan
 * @since 2022/11/11
 */
@Configuration
public class CovertSaTokenConfig implements WebMvcConfigurer {
    // 注册Sa-Token全局过滤器
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                // 放行接口文档uri
                .addExclude("/v3/api-docs")
                .setAuth(obj -> SaSameUtil.checkCurrentRequestToken())
                .setError(e -> RespVO.fail(Status.global(StatusEnum.ACCESS_BY_GATEWAY)));
    }
}