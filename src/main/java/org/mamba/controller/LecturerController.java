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
     * Creates a new Lecturer record.
     * @param lecturer Lecturer entity object
     * @return Success result
     */
    @PostMapping
    public Result createLecturer(@RequestBody Lecturer lecturer) {
        lecturerService.createLecturer(lecturer);
        return Result.success();
    }

    /**
     * Retrieves a Lecturer record by email.
     * @param email Lecturer's email
     * @return Success result with Lecturer object
     */
    @GetMapping("/{email}")
    public Result getLecturer(@PathVariable String email) {
        Lecturer lecturer = lecturerService.getLecturer(email);
        return Result.success(lecturer);
    }

    /**
     * Retrieves all Lecturer records.
     * @return Success result with list of Lecturers
     */
    @GetMapping
    public Result getAllLecturers() {
        List<Lecturer> lecturers = lecturerService.getAllLecturers();
        return Result.success(lecturers);
    }

    /**
     * Updates an existing Lecturer record.
     * @param lecturer Lecturer entity object
     * @return Success result
     */
    @PutMapping
    public Result updateLecturer(@RequestBody Lecturer lecturer) {
        lecturerService.updateLecturer(lecturer);
        return Result.success();
    }

    /**
     * Deletes a Lecturer record by email.
     * @param email Lecturer's email
     * @return Success result
     */
    @DeleteMapping("/{email}")
    public Result deleteLecturer(@PathVariable String email) {
        lecturerService.deleteLecturer(email);
        return Result.success();
    }
}
