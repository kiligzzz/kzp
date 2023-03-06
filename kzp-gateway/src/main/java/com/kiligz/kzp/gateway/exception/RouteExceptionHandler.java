package com.kiligz.kzp.gateway.exception;

import com.kiligz.kzp.common.utils.JsonUtil;
import com.kiligz.kzp.common.vo.RespVO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 路由转发异常处理
 *
 * @author Ivan
 * @since 2022/11/10
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RouteExceptionHandler implements WebExceptionHandler {
    @NonNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        return response.writeWith(
                Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    byte[] result = new byte[0];
                    try {
                        result = JsonUtil.getMapper().writeValueAsBytes(
                                RespVO.notFound());
                    } catch (Exception e) {
                        log.error("Error writing response", e);
                    }
                    return bufferFactory.wrap(result);
                }));
    }
}
