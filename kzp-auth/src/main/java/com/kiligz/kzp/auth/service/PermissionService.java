package com.kiligz.kzp.auth.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.collect.Lists;
import com.kiligz.kzp.rpc.dto.user.RoleDTO;
import com.kiligz.kzp.rpc.remote.user.RemoteUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限业务
 *
 * @author Ivan
 * @since 2022/11/24 23:13
 */
@Service
public class PermissionService implements StpInterface {

    @DubboReference
    RemoteUserService remoteUserService;

    /**
     * 获取权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        RoleDTO role = getRole(loginId);
        SaSession roleSession = SaSessionCustomUtil.getSessionById(
                "role-" + role.getId());
        return roleSession.get(
                "Permission_List",
                Lists.newArrayList(role.getPermission().split(",")));
    }

    /**
     * 获取角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        return session.get("Role_List", Lists.newArrayList(
                getRole(loginId).getRole()));
    }

    /**
     * 获取用户的角色
     */
    private RoleDTO getRole(Object userId) {
        return remoteUserService.getRoleByUserId(
                Integer.parseInt(userId.toString()));
    }
}
