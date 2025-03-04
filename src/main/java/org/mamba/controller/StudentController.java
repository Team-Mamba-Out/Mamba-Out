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

    @PostMapping
    public Result createStudent(@RequestBody Student student) {
        studentService.createStudent(student);
        return Result.success();
    }

    @GetMapping("/{email}")
    public Result getStudent(@PathVariable String email) {
        Student student = studentService.getStudent(email);
        return Result.success(student);
    }

    @GetMapping
    public Result getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return Result.success(students);
    }

    @PutMapping
    public Result updateStudent(@RequestBody Student student) {
        studentService.updateStudent(student);
        return Result.success();
    }

    @DeleteMapping("/{email}")
    public Result deleteStudent(@PathVariable String email) {
        studentService.deleteStudent(email);
        return Result.success();
    }

}