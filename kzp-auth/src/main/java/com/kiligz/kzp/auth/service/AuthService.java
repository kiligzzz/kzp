package com.kiligz.kzp.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.kiligz.kzp.rpc.dto.user.UserDTO;
import com.kiligz.kzp.rpc.remote.user.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 认证业务
 *
 * @author Ivan
 * @since 2022/11/24
 */
@Slf4j
@Service
public class AuthService {

    @DubboReference
    RemoteUserService remoteUserService;

    /**
     * 登录
     */
    public boolean login(UserDTO userDTO) {
        log.info("[auth]" + userDTO.getAccount());
        Integer userId = remoteUserService.verify(userDTO);
        if (userId != null) {
            StpUtil.login(userId);
            return true;
        }
        return false;
    }
}
