package com.kiligz.kzp.rpc.dto.biz;

import com.kiligz.kzp.entity.user.User;
import lombok.Data;

import java.util.concurrent.Future;

/**
 * 消息数据传输类
 *
 * @author Ivan
 * @since 2022/12/30
 */
@Data
public class MessageDTO {
    Future<User> userFuture;
    String channel;
    String pattern;
    String to;
    String content;
}
