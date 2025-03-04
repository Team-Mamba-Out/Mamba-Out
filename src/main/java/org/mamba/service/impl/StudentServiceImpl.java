package org.mamba.service.impl;

import org.mamba.entity.Student;
import org.mamba.mapper.StudentMapper;
import org.mamba.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public void createStudent(Student student) {
        studentMapper.insert(student);
    }

    @Override
    public Student getStudent(String email) {
        return studentMapper.findByEmail(email);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentMapper.findAll();
    }

    @Override
    public void updateStudent(Student student) {
        studentMapper.update(student);
    }

    @Override
    public void deleteStudent(String email) {
        studentMapper.delete(email);
    }
}