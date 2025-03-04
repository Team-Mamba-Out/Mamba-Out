package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.Student;
import org.mamba.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    /**
     * Obtains the student list based on the conditions given.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     * @param pageSize   the size of each page
     * @param offset     the offset
     * @return the list of all the students
     */
    @GetMapping
    public Result getStudents(@RequestParam(required = false) String email,
                              @RequestParam(required = false) Integer uid,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) Integer breakTimer,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) Integer offset) {
        List<Student> students = studentService.getStudents(email, uid, name, phone, breakTimer, pageSize, offset);
        return Result.success(students);
    }

    /**
     * Insert a new student.
     *
     * @param student the student to be created
     * @return the result of the creation operation
     */
    @PostMapping
    public Result createStudent(@RequestBody Student student) {
        try {
            studentService.createStudent(student.getEmail(), student.getUid(), student.getName(), student.getPhone(), student.getBreakTimer());
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * Update the information of a student by email.
     *
     * @param email   student's email
     * @param student the student with updated information
     * @return the result of the update operation
     */
    @PutMapping("/{email}")
    public Result updateStudentByEmail(@PathVariable String email, @RequestBody Student student) {
        try {
            studentService.updateStudentByEmail(email, student.getUid(), student.getName(), student.getPhone(), student.getBreakTimer());
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * Deletes the student specified by email.
     *
     * @param email the provided email
     * @return the result of the deletion operation
     */
    @DeleteMapping("/{email}")
    public Result deleteStudent(@PathVariable String email) {
        studentService.deleteStudent(email);
        return Result.success();
    }
}