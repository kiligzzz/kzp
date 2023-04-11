package com.kiligz.kzp.trace.support;

import com.kiligz.kzp.trace.domain.Trace;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 添加全链路追踪的http请求拦截器
 *
 * @author Ivan
 * @since 2023/3/10
 */
@Configuration
public class TraceInterceptor implements WebMvcConfigurer, HandlerInterceptor {
    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceInterceptor())
                .addPathPatterns("/**");
    }

    /**
     * 初始化Trace存入TransmittableThreadLocal、MDC中
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Trace.init();
        return true;
    }

    /**
     * 清理TransmittableThreadLocal、MDC中的值
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Trace.remove();
    }
}
