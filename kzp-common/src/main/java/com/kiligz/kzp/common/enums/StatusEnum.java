package com.kiligz.kzp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态枚举<br/>
 * 第一级：
 *      A：auth  B：business G：gateway Z：global<br/>
 * 第二级：
 *      01：kzp-gateway     02：kzp-auth    03：kzp-dispatcher
 *      04：kzp-processor   05：kzp-pusher  06：kzp-scheduler
 *      07：kzp-data-house  08：kzp-admin   99：global<br/>
 * 第三级：
 *      A：认证授权    B：业务错误    C：缓存错误
 *      D：数据库错误  F：文件IO错误  H：HTTP错误
 *      M：中间件错误
 *      N：网络错误    R：RPC错误    S：系统错误
 *      Z：global<br/>
 * 第四级：
 *      Z99：global
 *
 * @author Ivan
 * @since 2022/11/9
 */
@AllArgsConstructor
@Getter
public enum StatusEnum {

    SUCCESS("000000", "请求成功"),
    FAIL("999999", "请求失败"),

    NOT_FOUND("H01", "未找到uri"),
    PARAM_CHECK("H02", "参数校验失败"),

    NOT_LOGIN("A01", "未登录"),
    ACCESS_BY_GATEWAY("A02", "需要通过网关访问服务"),
    USERNAME_OR_PASSWORD_ERROR("A03", "用户名或密码错误"),
    NO_THIS_ROLE("A04", "不存在该角色"),
    NO_PERMISSION("A05", "无权限"),

    NACOS_ERROR("M01", "连接或读取nacos配置文件错误"),

    UNKNOWN("Z99", "未知异常"),
    NO_PROVIDER("R01", "没有服务提供方"),
    CANNOT_SERIALIZABLE("R02", "不能序列化"),

    CONVERT_ERROR("B01", "转换错误"),
    CHANNEL_ERROR("B02", "渠道错误"),
    QUOTA_LACK("B03", "渠道错误"),

    NULL_POINTER("S01", "空指针异常"),
    BEAN_COPY("S02", "Bean复制错误")



    ;

    private final String code;
    private final String msg;

    public static final String GATEWAY = "G01";
    public static final String AUTH = "G02";
    public static final String DISPATCHER = "B01";
    public static final String PROCESSOR = "B02";
    public static final String PUSHER = "B03";
    public static final String SCHEDULER = "B04";
    public static final String ADMIN = "A01";
    public static final String GLOBAL = "Z99";
}
