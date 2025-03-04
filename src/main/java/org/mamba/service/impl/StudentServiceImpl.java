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

    /**
     * Obtains the record list based on the conditions given.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     * @param pageSize   the size of each page
     * @param offset     the offset
     * @return the list of all the records
     */
    @Override
    public List<Student> getStudents(String email, Integer uid, String name, String phone, Integer breakTimer, Integer pageSize, Integer offset) {
        return studentMapper.getStudents(email, uid, name, phone, breakTimer, pageSize, offset);
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
    public void createStudent(String email, int uid, String name, String phone, int breakTimer) {
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
    public void updateStudentByEmail(String email, int uid, String name, String phone, int breakTimer) {
        studentMapper.updateStudentByEmail(email, uid, name, phone, breakTimer);
    }

    /**
     * Deletes the student specified by email
     * @param email the provided email
     */
    @Override
    public void deleteStudent(String email) {
        studentMapper.deleteStudentByEmail(email);
    }
}