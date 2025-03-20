package org.mamba.service;

import org.mamba.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    /**
     * Obtains the student list based on the conditions given.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     * @param size       the size of each page
     * @param page       the page No.
     */
    Map<String, Object> getStudents(String email, Integer uid, String name, String phone, Integer breakTimer, Integer size, Integer page);

    /**
     * Insert a new student.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     */
    void createStudent(String email, Integer uid, String name, String phone, Integer breakTimer);

    /**
     * Update the information of a student by email.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     */
    void updateStudentByEmail(String email, Integer uid, String name, String phone, Integer breakTimer);

    /**
     * Deletes the student specified by email.
     *
     * @param email the provided email
     */
    void deleteStudent(String email);

    /**
     * Returns the total number of students.
     *
     * @return the total number of students
     */
    int getTotalStudents();
    /**
     * update the student break times.
     *
     * @param uid user id
     * @param breakTimer student break times
     */
    void updateBreakTimer(Integer uid,Integer breakTimer);


    Student getStudentByUid(Integer uid);
}