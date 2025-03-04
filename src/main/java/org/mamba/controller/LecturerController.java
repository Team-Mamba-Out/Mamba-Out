package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.Lecturer;
import org.mamba.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lecturers")
public class LecturerController {
    @Autowired
    private LecturerService lecturerService;

    /**
     * Obtains the lecturer list based on the conditions given.
     *
     * @param email    lecturer's email
     * @param uid      the lecturer's uid
     * @param name     the lecturer's name
     * @param phone    the lecturer's phone number
     * @param pageSize the size of each page
     * @param offset   the offset
     * @return the list of all the lecturers
     */
    @GetMapping
    public Result getLecturers(@RequestParam(required = false) String email,
                               @RequestParam(required = false) Integer uid,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) Integer offset) {
        List<Lecturer> lecturers = lecturerService.getLecturers(email, uid, name, phone, pageSize, offset);
        return Result.success(lecturers);
    }

    /**
     * Insert a new lecturer.
     *
     * @param lecturer the lecturer to be created
     * @return the result of the creation operation
     */
    @PostMapping
    public Result createLecturer(@RequestBody Lecturer lecturer) {
        lecturerService.createLecturer(lecturer.getEmail(), lecturer.getUid(), lecturer.getName(), lecturer.getPhone());
        return Result.success();
    }

    /**
     * Update the information of a lecturer by email.
     *
     * @param email    lecturer's email
     * @param lecturer the lecturer with updated information
     * @return the result of the update operation
     */
    @PutMapping("/{email}")
    public Result updateLecturerByEmail(@PathVariable String email, @RequestBody Lecturer lecturer) {
        lecturerService.updateLecturerByEmail(email, lecturer.getUid(), lecturer.getName(), lecturer.getPhone());
        return Result.success();
    }

    /**
     * Deletes the lecturer specified by email.
     *
     * @param email the provided email
     * @return the result of the deletion operation
     */
    @DeleteMapping("/{email}")
    public Result deleteLecturer(@PathVariable String email) {
        lecturerService.deleteLecturer(email);
        return Result.success();
    }
}