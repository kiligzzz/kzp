package com.kiligz.kzp.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kiligz.kzp.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 *
 * @author Ivan
 * @since 2022/11/22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectIdAndPasswordByUsernameOrEmail(@Param("account") String account);
}
