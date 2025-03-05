package org.mamba.service;

import org.mamba.entity.Lecturer;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Lecturer entity operations.
 */
public interface LecturerService {
    /**
     * Obtains the lecturer list based on the conditions given.
     *
     * @param email lecturer's email
     * @param uid   the lecturer's uid
     * @param name  the lecturer's name
     * @param phone the lecturer's phone number
     * @param size  the size of each page
     * @param page  the page No.
     */
    Map<String, Object> getLecturers(String email, Integer uid, String name, String phone, Integer size, Integer page);

    /**
     * Insert a new lecturer.
     *
     * @param email lecturer's email
     * @param uid   the lecturer's uid
     * @param name  the lecturer's name
     * @param phone the lecturer's phone number
     */
    void createLecturer(String email, Integer uid, String name, String phone);

    /**
     * Update the information of a lecturer by email.
     *
     * @param email lecturer's email
     * @param uid   the lecturer's uid
     * @param name  the lecturer's name
     * @param phone the lecturer's phone number
     */
    void updateLecturerByEmail(String email, Integer uid, String name, String phone);

    /**
     * Deletes the lecturer specified by email.
     *
     * @param email the provided email
     */
    void deleteLecturer(String email);
}
