package com.kiligz.kzp.common.exception;

import com.kiligz.kzp.common.domain.Status;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private final Status status;

    /**
     * 错误信息列表
     */
    private final List<String> errorMsgList = new ArrayList<>();

    public KzpException(Status status, String... errorMsgArr) {
        this.status = status;
        Collections.addAll(errorMsgList, errorMsgArr);
    }

    public KzpException(Exception e, Status status, String... errorMsgArr) {
        super(e.getMessage());
        this.status = status;
        Collections.addAll(errorMsgList, errorMsgArr);
    }
}
