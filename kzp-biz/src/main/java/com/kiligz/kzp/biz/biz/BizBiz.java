package com.kiligz.kzp.biz.biz;

import cn.dev33.satoken.stp.StpUtil;
import com.kiligz.kzp.entity.user.User;
import com.kiligz.kzp.rpc.dto.biz.MessageDTO;
import com.kiligz.kzp.rpc.remote.user.RemoteUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 调度业务
 *
 * @author Ivan
 * @since 2022/12/30
 */
@Service
public class BizBiz {

    @DubboReference
    RemoteUserService remoteUserService;

    public void send(MessageDTO messageDTO) {
        Future<User> userFuture = remoteUserService.asyncGet(
                Integer.parseInt(StpUtil.getLoginId() + ""));
        messageDTO.setUserFuture(userFuture);
    }
}
