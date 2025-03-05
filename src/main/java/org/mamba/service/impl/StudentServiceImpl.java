package org.mamba.service.impl;

import org.mamba.entity.Room;
import org.mamba.entity.Student;
import org.mamba.mapper.StudentMapper;
import org.mamba.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    /**
     * Obtains the record list based on the conditions given.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     * @param size       the size of each page
     * @param page       the page No.
     */
    @Override
    public Map<String, Object> getStudents(String email, Integer uid, String name, String phone, Integer breakTimer, Integer size, Integer page) {
        // Calculate offset
        Integer offset = (page - 1) * size;
        List<Student> studentList = studentMapper.getStudents(email, uid, name, phone, breakTimer, size, offset);
        Map<String, Object> map = new HashMap<>();
        int total = studentList.size();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("students", studentList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
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
    @Override
    public void createStudent(String email, Integer uid, String name, String phone, Integer breakTimer) {
        studentMapper.createStudent(email, uid, name, phone, breakTimer);
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
    @Override
    public void updateStudentByEmail(String email, Integer uid, String name, String phone, Integer breakTimer) {
        studentMapper.updateStudentByEmail(email, uid, name, phone, breakTimer);
    }

    /**
     * Deletes the student specified by email
     *
     * @param email the provided email
     */
    @Override
    public void deleteStudent(String email) {
        studentMapper.deleteStudentByEmail(email);
    }
}