package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.User;
import org.mamba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Retrieves a list of users based on the given conditions.
     *
     * @param pageSize the number of records per page
     * @param offset the offset for pagination
     * @return a result object containing the list of users
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
     * Updates user information based on the user ID.
     * @param request request body from front-end
     * @return a result object for the update operation
     */
    @PutMapping("/update")
    public Result updateUserByUid(@RequestBody Map<String, Object> request) {
        Integer uid =  Integer.parseInt(request.get("uid").toString());
        String phone = (String) request.get("phone");
        String name =  (String) request.get("name");
        String role = (String) request.get("role");
        System.out.println(role);
        userService.updateUserByUid(uid, phone, name,role);
        return Result.success();
    }

    /**
     * Adds a new user.
     *
     * @param user the user to be created
     * @return a success result
     */
    @PostMapping("/create")
    public Result createUser(@RequestBody User user) {
        userService.createUser(user.getRole());
        return Result.success();
    }

    /**
     * Deletes a user by their UID.
     *
     * @param uid the UID of the user to delete
     * @return a success result
     */
    @DeleteMapping("/delete/{uid}")
    public Result deleteUserByUid(@PathVariable Integer uid) {
        userService.deleteUserByUid(uid);
        return Result.success();
    }
}