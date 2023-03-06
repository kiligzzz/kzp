package com.kiligz.kzp.common.vo;

import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 响应结果vo
 *
 * @author Ivan
 * @since 2022/11/9
 */
@Data
@AllArgsConstructor
public class RespVO<T> {
    /**
     * 状态码
     */
    private String code;

    /**
     * 响应信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public RespVO(Status status, T data) {
        this(status.getCode(), status.getMsg(), data);
    }

    public static RespVO<Void> success() {
        return new RespVO<>(Status.success(), null);
    }

    public static RespVO<Void> success(String msg) {
        return new RespVO<>(Status.success().getCode(), msg, null);
    }


    public static <T> RespVO<T> data(T data) {
        return new RespVO<>(Status.success(), data);
    }


    public static RespVO<Void> fail() {
        return fail(Status.fail());
    }

    public static RespVO<Void> fail(Status status) {
        return fail(status, null);
    }

    public static <T> RespVO<T> fail(Status status, T data) {
        return new RespVO<>(status, data);
    }


    public static RespVO<Void> notFound() {
        return fail(Status.global(StatusEnum.NOT_FOUND));
    }

    @Override
    public String toString() {
        return "{"
                + "\"code\": \"" + code
                + "\", \"msg\": \"" + msg
                + "\", \"data\": \"" + data
                + "\"}";
    }
}
