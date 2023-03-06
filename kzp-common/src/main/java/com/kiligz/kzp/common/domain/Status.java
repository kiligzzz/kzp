package com.kiligz.kzp.common.domain;

import com.kiligz.kzp.common.constant.Consts;
import com.kiligz.kzp.common.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 状态
 *
 * @author Ivan
 * @since 2022/11/10
 */
@Data
@AllArgsConstructor
public class Status {
    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态信息
     */
    private final String msg;

    private Status(StatusEnum statusEnum) {
        this(statusEnum, "");
    }

    private Status(StatusEnum statusEnum, String prefix) {
        this.code = prefix + statusEnum.getCode();
        this.msg = statusEnum.getMsg();
    }

    public static Status success() {
        return new Status(StatusEnum.SUCCESS);
    }

    public static Status fail() {
        return new Status(StatusEnum.FAIL);
    }

    public static Status gateway(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.GATEWAY);
    }

    public static Status auth(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.AUTH);
    }

    public static Status dispatcher(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.DISPATCHER);
    }

    public static Status processor(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.PROCESSOR);
    }

    public static Status pusher(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.PUSHER);
    }

    public static Status scheduler(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.SCHEDULER);
    }

    public static Status admin(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.ADMIN);
    }

    public static Status global(StatusEnum statusEnum) {
        return new Status(statusEnum, StatusEnum.GLOBAL);
    }

    @Override
    public String toString() {
        return code + Consts.Hyphen + msg;
    }
}
