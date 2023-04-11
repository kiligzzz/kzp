package com.kiligz.kzp.user.service;

import com.kiligz.kzp.common.utils.BeanUtil;
import com.kiligz.kzp.common.utils.Concurrents;
import com.kiligz.kzp.common.utils.SpringUtil;
import com.kiligz.kzp.entity.user.Role;
import com.kiligz.kzp.entity.user.User;
import com.kiligz.kzp.rpc.dto.user.RoleDTO;
import com.kiligz.kzp.rpc.dto.user.UserDTO;
import com.kiligz.kzp.rpc.remote.user.RemoteUserService;
import com.kiligz.kzp.user.mapper.RoleMapper;
import com.kiligz.kzp.user.mapper.UserMapper;
import com.kiligz.kzp.user.utils.BCryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 用户服务
 *
 * @author Ivan
 * @since 2022/11/18
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class UserService implements RemoteUserService {

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    /**
     * 添加用户
     */
    public void add(User user) {
        userMapper.insert(BCryptUtil.encrypt(user));
    }

    /*------------------------ 可远程调用方法 ------------------------*/

    /**
     * 根据id获取用户
     */
    @Override
    @Cacheable(cacheNames = "kzp:user.user#10", key = "#id")
    public User get(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public Future<User> asyncGet(Integer id) {
        return Concurrents.getFixedThreadPool("test").submit(() -> get(id));
    }

    /**
     * 校验用户是否可用，校验成功返回用户id
     */
    @Override
    public Integer verify(UserDTO userDTO) {
        log.info("[user]" + userDTO.getAccount());
        User user = SpringUtil.getBean(UserService.class).selectUserByAccount(
                userDTO.getAccount());
        if (user != null && BCryptUtil.check(userDTO, user)) {
            return user.getId();
        }
        return null;
    }

    /**
     * 根据用户id获取角色
     */
    @Override
    @Cacheable(cacheNames = "kzp:user.role", key = "#userId")
    public RoleDTO getRoleByUserId(Integer userId) {
        Role role = roleMapper.selectOneByUserId(userId);
        return BeanUtil.copy(role, new RoleDTO());
    }

    /**
     * 根据account查询User
     */
    @Cacheable(cacheNames = "kzp:user.user", key = "#account")
    public User selectUserByAccount(String account) {
        log.info("get from db");
        return userMapper.selectIdAndPasswordByUsernameOrEmail(account);
    }
}
