package com.kiligz.kzp.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.kiligz.kzp.auth.service.AuthService;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.rpc.dto.user.UserDTO;
import com.kiligz.kzp.common.enums.StatusEnum;
import com.kiligz.kzp.common.vo.RespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Ivan
 * @since 2022/11/15
 */
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @PostMapping("login")
    public RespVO<?> login(@RequestBody UserDTO userDTO) {
        return authService.login(userDTO) ?
                RespVO.success("登录成功") :
                RespVO.fail(Status.auth(StatusEnum.USERNAME_OR_PASSWORD_ERROR));
    }

    /**
     * 是否登录
     */
    @GetMapping("isLogin")
    public RespVO<?> isLogin() {
        return RespVO.success("是否登录：" + StpUtil.isLogin());
    }

    /**
     * token信息
     */
    @GetMapping("tokenInfo")
    public RespVO<?> tokenInfo() {
        System.out.println(StpUtil.getRoleList());
        System.out.println(StpUtil.getPermissionList());
        return RespVO.data(StpUtil.getTokenInfo());
    }

    /**
     * 登出
     */
    @GetMapping("logout")
    public RespVO<?> logout() {
        StpUtil.logout();
        return RespVO.success("登出成功");
    }

    /**
     * 获取权限列表
     */
    @GetMapping("getPermission")
    public RespVO<?> getPermission() {
        return RespVO.data(StpUtil.getPermissionList());
    }

    /**
     * 获取角色
     */
    @GetMapping("getRole")
    public RespVO<?> getRole() {
        return RespVO.data(StpUtil.getRoleList());
    }
}
