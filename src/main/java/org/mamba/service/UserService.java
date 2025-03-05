package org.mamba.service;

import org.mamba.entity.User;

import java.util.List;

public interface UserService {
    /**
     * 获取用户列表，根据给定的条件进行筛选。
     *
     * @param uid 用户ID
     * @param role 用户角色
     * @param pageSize 每页大小
     * @param offset 偏移量
     * @return 用户列表
     */
    List<User> getUsers(Integer uid, String role, Integer pageSize, Integer offset);



    /**
     * 根据用户ID更新用户信息。
     *
     * @param uid 用户ID
     * @param role 用户角色
     */
    void updateUserByUid(Integer uid, String role);

}