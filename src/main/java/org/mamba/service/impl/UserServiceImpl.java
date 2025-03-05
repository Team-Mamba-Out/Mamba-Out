package org.mamba.service.impl;

import org.mamba.entity.User;
import org.mamba.mapper.UserMapper;
import org.mamba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户列表，根据给定的条件进行筛选。
     *
     * @param uid 用户ID
     * @param role 用户角色
     * @param pageSize 每页大小
     * @param offset 偏移量
     * @return 用户列表
     */
    @Override
    public List<User> getUsers(Integer uid, String role, Integer pageSize, Integer offset) {
        return userMapper.getUsers(uid, role, pageSize, offset);
    }


    /**
     * 根据用户ID更新用户信息。
     *
     * @param uid 用户ID
     * @param role 用户角色
     */
    @Override
    public void updateUserByUid(Integer uid, String role) {
        userMapper.updateUserByUid(uid, role);
    }

}