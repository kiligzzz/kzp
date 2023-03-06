package com.kiligz.kzp.entity.user;

import com.kiligz.kzp.common.constant.Consts;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.exception.KzpException;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * 用户实体
 *
 * @author Ivan
 * @since 2022/11/18
 */
@Data
public class User implements Serializable {
    Integer id;
    Integer roleId;
    String username;
    String password;
    String name;
    String phone;
    String email;
    String wechat;
    Integer smsQuota;
    Integer emailQuota;
    Integer wechatQuota;
    Integer feiQuota;
    Integer dingQuota;
    Integer deleteFlag;
    Timestamp createdTime;
    Timestamp updatedTime;

    /**
     * 对应渠道的配额
     */
    public Integer quota(String channel, int... val) {
        int sum = Arrays.stream(val).sum();

        if (Consts.SMS.equals(channel)) {
            smsQuota += sum;
            return smsQuota;
        }
        else if (Consts.EMAIL.equals(channel)) {
            emailQuota += sum;
            return emailQuota;
        }
        else if (Consts.WECHAT.equals(channel)) {
            wechatQuota += sum;
            return wechatQuota;
        }
        else if (Consts.FEI.equals(channel)) {
            feiQuota += sum;
            return feiQuota;
        }
        else if (Consts.DING.equals(channel)) {
            dingQuota += sum;
            return dingQuota;
        }
        else {
            throw new KzpException(Status.global(StatusEnum.CHANNEL_ERROR));
        }
    }
}
