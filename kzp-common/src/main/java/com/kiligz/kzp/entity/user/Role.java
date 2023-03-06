package com.kiligz.kzp.entity.user;

import lombok.Data;

/**
 * @author Ivan
 * @since 2022/11/28
 */
@Data
public class Role {
    Integer id;
    String role;
    String permission;
}
