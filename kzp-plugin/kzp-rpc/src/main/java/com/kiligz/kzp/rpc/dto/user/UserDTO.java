package com.kiligz.kzp.rpc.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * User数据传输类
 *
 * @author Ivan
 * @since 2022/11/18
 */
@Data
public class UserDTO implements Serializable {
    String account;
    String password;
}
