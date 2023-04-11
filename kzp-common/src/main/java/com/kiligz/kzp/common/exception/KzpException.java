package com.kiligz.kzp.common.exception;

import com.kiligz.kzp.common.domain.Status;
import lombok.Getter;

/**
 * Kzp异常
 *
 * @author Ivan
 * @since 2022/11/10
 */
@Getter
public class KzpException extends RuntimeException {
    /**
     * 状态
     */
    private Status status;

    /**
     * 错误信息
     */
    private String msg;

    public KzpException(Status status) {
        this(status, null);
    }

    public KzpException(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public KzpException(Exception e, Status status) {
        this(e, status, null);
    }

    public KzpException(Exception e, Status status, String msg) {
        super(e.getMessage());
        this.status = status;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "KzpException: " + "status["+ status + "] msg[" + msg + "]";
    }
}
