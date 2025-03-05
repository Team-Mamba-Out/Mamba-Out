package org.mamba.service;

import org.mamba.entity.User;

import java.util.List;

public interface UserService {
    /**
     * Retrieves a list of users based on the given conditions.
     *
     * @param uid the user ID
     * @param role the user role
     * @param pageSize the number of records per page
     * @param offset the offset for pagination
     * @return a list of users
     */
    List<User> getUsers(Integer uid, String role, Integer pageSize, Integer offset);

    /**
     * Updates user information based on the user ID.
     *
     * @param uid the user ID
     * @param role the user role
     */
    void updateUserByUid(Integer uid, String role);

    /**
     * Adds a new user.
     *
     * @param uid the user ID
     * @param role the user role
     */
    void createUser(Integer uid, String role);

    /**
     * Deletes a user by their UID.
     *
     * @param uid the UID of the user to delete
     */
    void deleteUserByUid(Integer uid);
}