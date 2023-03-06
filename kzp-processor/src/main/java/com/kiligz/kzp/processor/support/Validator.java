package com.kiligz.kzp.processor.support;

import com.kiligz.kzp.common.domain.Msg;
import com.kiligz.kzp.common.domain.MsgWrapper;
import com.kiligz.kzp.common.domain.PushWrapper;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.exception.KzpException;
import com.kiligz.kzp.entity.user.User;

import java.util.List;

/**
 * @author Ivan
 * @since 2023/1/11
 */
public class Validator {
    /**
     * 检查是否可用
     */
    public static void check(PushWrapper pw) {
        checkQuota(pw);
    }

    /**
     * 检查配额
     */
    public static void checkQuota(PushWrapper pw) {
        MsgWrapper msgWrapper = pw.getMsgWrapper();
        String channel = msgWrapper.getChannel();
        User user = msgWrapper.getUser();
        List<Msg> msgList = msgWrapper.getMsgList();

        if (user.quota(channel) < msgList.size()) {
            throw new KzpException(Status.processor(StatusEnum.QUOTA_LACK), channel);
        }
    }

}
