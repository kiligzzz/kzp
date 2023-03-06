package com.kiligz.kzp.user.utils;

import cn.hutool.crypto.digest.BCrypt;
import com.kiligz.kzp.rpc.dto.user.UserDTO;
import com.kiligz.kzp.entity.user.User;

/**
 * 加解密用户密码工具类
 *
 * @author Ivan
 * @since 2022/11/18
 */
public class BCryptUtil {
    /**
     * 加密用户密码
     */
    public static User encrypt(User user) {
        String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(password);
        return user;
    }

    /**
     * 校验用户密码
     */
    public static boolean check(UserDTO userDTO, User user) {
        return BCrypt.checkpw(userDTO.getPassword(), user.getPassword());
    }
}
