package org.mamba.service;

import org.mamba.entity.Student;
import java.util.List;

public interface StudentService {
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
    List<Student> getStudents(String email, Integer uid, String name, String phone, Integer breakTimer, Integer pageSize, Integer offset);

    /**
     * Insert a new student.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     */
    void createStudent(String email, int uid, String name, String phone, int breakTimer);

    /**
     * Update the information of a student by email.
     *
     * @param email      student's email
     * @param uid        the student's uid
     * @param name       the student's name
     * @param phone      the student's phone number
     * @param breakTimer the time that the student breaks the rules
     */
    void updateStudentByEmail(String email, int uid, String name, String phone, int breakTimer);

    /**
     * Deletes the student specified by email.
     *
     * @param email the provided email
     */
    void deleteStudent(String email);
}