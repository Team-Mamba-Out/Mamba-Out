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
    // TODO 注脚，参数传递
    public Result getStudents(String email, Integer uid, String name, String phone, Integer breakTimer, Integer pageSize, Integer offset) {
        List<Student> students = studentService.getStudents(email, uid, name, phone, breakTimer, pageSize, offset);
        return Result.success(students);
    }

    /**
     * Insert a new student.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     */
    // TODO 注脚，参数传递
    public Result createStudent(String email, int uid, String name, String phone, int breakTimer) {
        studentService.createStudent(email, uid, name, phone, breakTimer);
        return Result.success();
    }

    /**
     * Update the information of a student by email.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     */
    // TODO 注脚，参数传递
    public Result updateStudentByEmail(String email, int uid, String name, String phone, int breakTimer) {
        studentService.updateStudentByEmail(email, uid, name, phone, breakTimer);
        return Result.success();
    }

    /**
     * Deletes the student specified by email.
     *
     * @param email the provided email
     */
    // TODO 注脚，参数传递
    public Result deleteStudent(String email) {
        studentService.deleteStudent(email);
        return Result.success();
    }
}