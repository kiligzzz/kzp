package com.kiligz.kzp.auth.biz;

import cn.dev33.satoken.stp.StpUtil;
import com.kiligz.kzp.rpc.dto.user.UserDTO;
import com.kiligz.kzp.rpc.remote.user.RemoteUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 认证业务
 *
 * @author Ivan
 * @since 2022/11/24
 */
@Service
public class AuthBiz {

    @DubboReference
    RemoteUserService remoteUserService;

    /**
     * 登录
     */
    public boolean login(UserDTO userDTO) {
        Integer userId = remoteUserService.verify(userDTO);
        if (userId != null) {
            StpUtil.login(userId);
            return true;
        }
        return false;
    }
}
