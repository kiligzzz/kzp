package com.kiligz.kzp.common.domain;

import lombok.Data;

/**
 * 访问权限控制
 *
 * @author Ivan
 * @since 2022/11/25
 */
@Data
public class Access {
    String uri;
    String type;
    String accessible;

    public boolean isRole() {
        return "role".equals(type);
    }
}
