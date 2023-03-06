package com.kiligz.kzp.common.domain;

import com.kiligz.kzp.entity.user.User;
import lombok.Data;

import java.util.List;

/**
 * @author Ivan
 * @since 2022/12/30
 */
@Data
public class MsgWrapper {
    User user;
    String channel;
    String to;
    List<Msg> msgList;
}
