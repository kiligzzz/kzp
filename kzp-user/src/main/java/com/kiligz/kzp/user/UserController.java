package com.kiligz.kzp.user;

import com.kiligz.kzp.common.vo.RespVO;
import com.kiligz.kzp.entity.user.User;
import com.kiligz.kzp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author Ivan
 * @since 2022/11/18
 */
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 添加用户
     */
    @PostMapping("add")
    public RespVO<?> add(@RequestBody User user) {
        userService.add(user);
        return RespVO.success();
    }

    /**
     * test
     */
    @GetMapping("get")
    public RespVO<?> get(Integer id) {
        User user = userService.get(id);
        return RespVO.data(user);
    }
}
