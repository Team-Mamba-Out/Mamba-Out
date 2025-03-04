package org.mamba.service.impl;

import org.mamba.entity.Lecturer;
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
     * Creates a new Lecturer record.
     * @param lecturer Lecturer entity object
     */
    @Override
    public void createLecturer(Lecturer lecturer) {
        lecturerMapper.insert(lecturer);
    }

    /**
     * Retrieves a Lecturer record by email.
     * @param email Lecturer's email
     * @return Corresponding Lecturer entity object
     */
    @Override
    public Lecturer getLecturer(String email) {
        return lecturerMapper.findByEmail(email);
    }

    /**
     * Retrieves all Lecturer records.
     * @return List of Lecturer entity objects
     */
    @Override
    public List<Lecturer> getAllLecturers() {
        return lecturerMapper.findAll();
    }

    /**
     * Updates an existing Lecturer record.
     * @param lecturer Lecturer entity object to be updated
     */
    @Override
    public void updateLecturer(Lecturer lecturer) {
        lecturerMapper.update(lecturer);
    }

    /**
     * Deletes a Lecturer record by email.
     * @param email Lecturer's email
     */
    @Override
    public void deleteLecturer(String email) {
        lecturerMapper.delete(email);
    }
}
