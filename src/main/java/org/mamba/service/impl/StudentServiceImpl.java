package org.mamba.service.impl;

import org.mamba.entity.Room;
import org.mamba.entity.Student;
import org.mamba.mapper.StudentMapper;
import org.mamba.service.StudentService;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private MessageService messageService;

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
        int total = studentMapper.count();
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

        messageService.createMessage(
                uid,
                "Welcome to the System",
                "Dear " + name + ", your student account has been created successfully.",
                LocalDateTime.now(),
                false,
                "1;JinhaoZhang",
                0,
                0
        );
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
        studentMapper.updateStudentByUid(email, uid, name, phone, breakTimer);

        messageService.createMessage(
                uid,
                "Student Information Updated",
                "Dear " + name + ", your student information has been successfully updated.",
                LocalDateTime.now(),
                false,
                "1;JinhaoZhang",
                0,
                0
        );
    }

    /**
     * Deletes the student specified by email
     *
     * @param email the provided email
     */
    @Override
    public void deleteStudent(String email) {
        studentMapper.deleteStudentByEmail(email);

        messageService.createMessage(
                1,
                "Student Account Deletion",
                "Student account associated with the email " + email + " has been successfully deleted.",
                LocalDateTime.now(),
                false,
                "1;JinhaoZhang",
                0,
                0
        );
    }

    @Override
    public int getTotalStudents() {
        return studentMapper.count();
    }
}