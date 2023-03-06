package com.kiligz.kzp.rpc.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Role数据传输类
 *
 * @author Ivan
 * @since 2022/12/9
 */
@Data
public class RoleDTO implements Serializable {
    Integer id;
    String role;
    String permission;
}
