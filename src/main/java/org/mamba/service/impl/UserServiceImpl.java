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
     * Retrieves a list of users based on the given conditions.
     *
     * @param pageSize the number of records per page
     * @param offset the offset for pagination
     * @return a list of users
     */
    @Override
    public List<User> getUsers(Integer uid, String microsoftId, String email, String name, String role, Integer pageSize, Integer offset) {
        return userMapper.getUsers(uid, microsoftId, email, name, role, pageSize, offset);
    }

    /**
     * Updates user information based on the user ID.
     *
     * @param uid the user ID
     * @param role the user role
     */
    @Override
    public void updateUserByUid(Integer uid, String role) {
        userMapper.updateUserByUid(uid, role);
    }

    /**
     * Adds a new user.
     */
    @Override
    public void createUser(String microsoftId, String email, String name, String role) {
        userMapper.createUser(microsoftId, email, name, role);
    }

    /**
     * Deletes a user by their UID.
     *
     * @param uid the UID of the user to delete
     */
    @Override
    public void deleteUserByUid(Integer uid) {
        userMapper.deleteUserByUid(uid);
    }

    @Override
    public User getUserByUid(Integer uid) {
        return userMapper.getUserByUid(uid);
    }

}