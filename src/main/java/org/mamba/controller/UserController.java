package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.User;
import org.mamba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 获取用户列表，根据给定的条件进行筛选。
     *
     * @param uid      用户ID
     * @param role     用户角色
     * @param pageSize 每页大小
     * @param offset   偏移量
     * @return 包含用户列表的结果对象
     */
    @GetMapping
    public Result getUsers(@RequestParam(required = false) Integer uid,
                           @RequestParam(required = false) String role,
                           @RequestParam(required = false) Integer pageSize,
                           @RequestParam(required = false) Integer offset) {
        List<User> users = userService.getUsers(uid, role, pageSize, offset);
        return Result.success(users);
    }

    /**
     * 根据用户ID更新用户信息。
     *
     * @param uid  用户ID
     * @param user 包含更新信息的用户对象
     * @return 更新操作的结果对象
     */
    @PutMapping("/{uid}")
    public Result updateUserByUid(@PathVariable Integer uid, @RequestBody User user) {
        userService.updateUserByUid(uid, user.getRole());
        return Result.success();
    }

}