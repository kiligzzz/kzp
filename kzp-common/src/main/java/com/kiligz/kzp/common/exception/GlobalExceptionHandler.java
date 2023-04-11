package com.kiligz.kzp.common.exception;

import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.vo.RespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 统一异常处理
 *
 * @author Ivan
 * @since 2022/11/10
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Kzp异常处理
     */
    @ExceptionHandler({KzpException.class})
    public RespVO<?> handleException(KzpException e) {
        log.error("KzpException: ", e);
        return RespVO.fail(e.getStatus());
    }

    /**
     * Rpc异常处理
     */
//    @ExceptionHandler({RpcException.class})
//    public RespVO<?> handlerException(RpcException e) {
//        StatusEnum statusEnum;
//        // 序列化异常处理
//        if (e.getMessage().contains("EncoderException")) {
//            statusEnum = StatusEnum.CANNOT_SERIALIZABLE;
//        }
//        // 其它Rpc异常处理
//        else {
//            statusEnum = StatusEnum.NO_PROVIDER;
//        }
//        Status status = Status.global(statusEnum);
//        log.error("RpcException: {}", status, e);
//        return RespVO.fail(status);
//    }


    /**
     * 其它异常处理
     */
    @ExceptionHandler({Exception.class})
    public RespVO<?> handleException(Exception e) {
        Status status;
        // 参数校验异常处理
        if (e instanceof HttpMessageConversionException
                || e instanceof MethodArgumentNotValidException) {
            status = Status.global(StatusEnum.PARAM_CHECK);
            log.warn("Warn: {}", status);
        }
        // 其它异常处理
        else {
            status = Status.fail();
            log.error("Exception: {}", status, e);
        }
        return RespVO.fail(status);
    }

    /**
     * 404异常处理
     */
    public static class NotFoundController implements ErrorController {
        @GetMapping("/error")
        public RespVO<?> error(HttpServletRequest request) {
            // 获取error转发前uri
            ServletRequest fromRequest = ((HttpServletRequestWrapper) request).getRequest();
            String reqUri = ((RequestFacade) fromRequest).getRequestURI();

            RespVO<Void> respVO = RespVO.notFound();
            log.error("{}: {}", respVO.getMsg(), reqUri);
            return respVO;
        }
    }
}
