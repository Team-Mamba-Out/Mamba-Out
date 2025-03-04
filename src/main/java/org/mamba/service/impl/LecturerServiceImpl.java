package org.mamba.service.impl;

import org.mamba.entity.Lecturer;
import org.mamba.entity.Student;
import org.mamba.mapper.LecturerMapper;
import org.mamba.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Lecturer entity.
 */
@Service
public class LecturerServiceImpl implements LecturerService {
    @Autowired
    private LecturerMapper lecturerMapper;

    /**
     * Obtains the lecturer list based on the conditions given.
     *
     * @param email    lecturer's email
     * @param uid      the lecturer's uid
     * @param name     the lecturer's name
     * @param phone    the lecturer's phone number
     * @param pageSize the size of each page
     * @param offset   the offset
     * @return the list of all the lecturers
     */
    @Override
    public List<Student> getLecturers(String email, Integer uid, String name, String phone, Integer pageSize, Integer offset) {
        return lecturerMapper.getLecturers(email, uid, name, phone, pageSize, offset);
    }

    /**
     * Insert a new lecturer.
     *
     * @param email lecturer's email
     * @param uid   the lecturer's uid
     * @param name  the lecturer's name
     * @param phone the lecturer's phone number
     */
    @Override
    public void createLecturer(String email, int uid, String name, String phone) {
        lecturerMapper.createLecturer(email, uid, name, phone);
    }

    /**
     * Update the information of a lecturer by email.
     *
     * @param email lecturer's email
     * @param uid   the lecturer's uid
     * @param name  the lecturer's name
     * @param phone the lecturer's phone number
     */
    @Override
    public void updateLecturerByEmail(String email, int uid, String name, String phone) {
        lecturerMapper.updateLecturerByEmail(email, uid, name, phone);
    }

    /**
     * Deletes the lecturer specified by email.
     *
     * @param email the provided email
     */
    @Override
    public void deleteLecturer(String email) {
        lecturerMapper.deleteLecturerByEmail(email);
    }
}
