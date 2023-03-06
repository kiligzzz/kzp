package com.kiligz.kzp.rpc.remote.user;

import com.kiligz.kzp.entity.user.User;
import com.kiligz.kzp.rpc.dto.user.RoleDTO;
import com.kiligz.kzp.rpc.dto.user.UserDTO;

import java.util.concurrent.Future;

/**
 * 远程用户服务接口
 *
 * @author Ivan
 * @since 2022/11/18
 */
public interface RemoteUserService {
    /**
     * 根据id获取用户
     */
    User get(Integer id);

    /**
     * 异步根据id获取用户
     */
    Future<User> asyncGet(Integer id);

    /**
     * 校验用户是否可用，校验成功返回用户id
     */
    Integer verify(UserDTO userDTO);

    /**
     * 根据用户id获取角色id
     */
    RoleDTO getRoleByUserId(Integer userId);
}
