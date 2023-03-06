package com.kiligz.kzp.common.exception;

import com.kiligz.kzp.common.vo.RespVO;

/**
 * 异常处理器接口
 *
 * @author Ivan
 * @date 2022/12/5 12:27
 */
public interface KzpExceptionHandler {
    /**
     * 处理对应类型的异常
     */
    <T extends Exception> RespVO<?> handleException(T e);
}
