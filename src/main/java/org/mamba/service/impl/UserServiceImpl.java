package org.mamba.service.impl;

import org.mamba.entity.Admin;
import org.mamba.entity.Lecturer;
import org.mamba.entity.Student;
import org.mamba.entity.User;
import org.mamba.mapper.AdminMapper;
import org.mamba.mapper.LecturerMapper;
import org.mamba.mapper.StudentMapper;
import org.mamba.mapper.UserMapper;
import org.mamba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private LecturerMapper lecturerMapper;
    @Autowired
    private AdminMapper adminMapper;

    /**
     * Retrieves a list of users based on the given conditions.
     *
     * @param pageSize the number of records per page
     * @param offset   the offset for pagination
     * @return a list of users
     */
    @Override
    public List<User> getUsers(Integer uid, String role, Integer pageSize, Integer offset) {
        return userMapper.getUsers(uid, role, pageSize, offset);
    }

    /**
     * Updates user information based on the user ID.
     *
     * @param uid  the user ID
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
    public void createUser(String role) {
        userMapper.createUser(role);
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

    @Override
    public Map<String, Object> getUserInfo(Integer uid) {
        Map<String, Object> result = new HashMap<>();
        String role = userMapper.getUsers(uid, null, null, null).get(0).getRole();
        String roleId = role.split("-")[1];

        switch (roleId) {
            case "001":
                result.put("role", "Student");
                Student s = studentMapper.getStudents(null, uid, null, null, null, null, null).get(0);
                result.put("name", s.getName());
                result.put("email", s.getEmail());
                result.put("phone", s.getPhone());
                result.put("uid", s.getUid());
                result.put("breakTimer", s.getBreakTimer());
                break;
            case "002":
                result.put("role", "Lecturer");
                Lecturer l = lecturerMapper.getLecturers(null, uid, null, null, null, null).get(0);
                result.put("name", l.getName());
                result.put("email", l.getEmail());
                result.put("phone", l.getPhone());
                result.put("uid", l.getUid());
                break;
            case "003":
                result.put("role", "admin");
                Admin a = adminMapper.getAdmins(null, uid, null, null, null, null).get(0);
                result.put("name", a.getName());
                result.put("email", a.getEmail());
                result.put("phone", a.getPhone());
                result.put("uid", a.getUid());
            default:
                return null;
        }
        return result;
    }

    @Override
    public String getUserName(Integer uid) {
        String role = userMapper.getUsers(uid, null, null, null).get(0).getRole();
        String roleId = role.split("-")[1];
        switch (roleId) {
            case "001":
                return studentMapper.getStudents(null, uid, null, null, null, null, null).get(0).getName();
            case "002":
                return lecturerMapper.getLecturers(null, uid, null, null, null, null).get(0).getName();
            case "003":
                return adminMapper.getAdmins(null, uid, null, null, null, null).get(0).getName();
            default:
                return null;
        }
    }

    @Override
    public Integer getUserId(String email) {
        Integer result = userMapper.getUserIdByEmail(email);
        if (result == null) {
            //this means that it is the first time of the user login
            userMapper.createUser(email + "001");
            result = userMapper.getUserIdByEmail(email);
            userMapper.createStudent(result, email);
        }
        return result;

    }

}