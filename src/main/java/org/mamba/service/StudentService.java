package org.mamba.service;

import org.mamba.entity.Student;
import java.util.List;

public interface StudentService {
    void createStudent(Student student);
    Student getStudent(String email);
    List<Student> getAllStudents();
    void updateStudent(Student student);
    void deleteStudent(String email);
}