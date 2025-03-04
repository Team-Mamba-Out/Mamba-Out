package org.mamba.service;

import org.mamba.entity.Lecturer;
import java.util.List;

/**
 * Service interface for Lecturer entity operations.
 */
public interface LecturerService {
    /**
     * Creates a new Lecturer record.
     * @param lecturer Lecturer entity object
     */
    void createLecturer(Lecturer lecturer);

    /**
     * Retrieves a Lecturer record by email.
     * @param email Lecturer's email
     * @return Corresponding Lecturer entity object
     */
    Lecturer getLecturer(String email);

    /**
     * Retrieves all Lecturer records.
     * @return List of Lecturer entity objects
     */
    List<Lecturer> getAllLecturers();

    /**
     * Updates an existing Lecturer record.
     * @param lecturer Lecturer entity object to be updated
     */
    void updateLecturer(Lecturer lecturer);

    /**
     * Deletes a Lecturer record by email.
     * @param email Lecturer's email
     */
    void deleteLecturer(String email);
}
