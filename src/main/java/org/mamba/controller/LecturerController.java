package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.Lecturer;
import org.mamba.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Lecturer entities.
 */
@RestController
@RequestMapping("/lecturers")
public class LecturerController {
    @Autowired
    private LecturerService lecturerService;

    /**
     * Obtains the lecturer list based on the conditions given.
     *
     * @param email      lecturer's email
     * @param uid        the lecturer's uid
     * @param name       the lecturer's name
     * @param phone      the lecturer's phone number
     * @param pageSize   the size of each page
     * @param offset     the offset
     * @return the list of all the lecturers
     */
    // TODO 注脚，参数传递
    public Result getLecturers(String email, Integer uid, String name, String phone, Integer pageSize, Integer offset) {
        List<Lecturer> students = lecturerService.getLecturers(email, uid, name, phone, pageSize, offset);
        return Result.success(students);
    }

    /**
     * Insert a new lecturer.
     *
     * @param email      lecturer's email
     * @param uid        the lecturer's uid
     * @param name       the lecturer's name
     * @param phone      the lecturer's phone number
     */
    // TODO 注脚，参数传递
    public Result createLecturer(String email, int uid, String name, String phone) {
        lecturerService.createLecturer(email, uid, name, phone);
        return Result.success();
    }

    /**
     * Update the information of a lecturer by email.
     *
     * @param email      lecturer's email
     * @param uid        the lecturer's uid
     * @param name       the lecturer's name
     * @param phone      the lecturer's phone number
     */
    // TODO 注脚，参数传递
    public Result updateLecturerByEmail(String email, int uid, String name, String phone) {
        lecturerService.updateLecturerByEmail(email, uid, name, phone);
        return Result.success();
    }

    /**
     * Deletes the lecturer specified by email.
     *
     * @param email the provided email
     */
    // TODO 注脚，参数传递
    public Result deleteLecturer(String email) {
        lecturerService.deleteLecturer(email);
        return Result.success();
    }
}
