package com.kiligz.kzp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kiligz.kzp.entity.user.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Ivan
 * @since 2022/11/28
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    Role selectOneByUserId(@Param("id") Integer id);
}
