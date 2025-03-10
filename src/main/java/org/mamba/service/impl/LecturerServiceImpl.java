package org.mamba.service.impl;

import org.mamba.entity.Lecturer;
import org.mamba.mapper.LecturerMapper;
import org.mamba.service.LecturerService;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for Lecturer entity.
 */
@Service
public class LecturerServiceImpl implements LecturerService {
    @Autowired
    private LecturerMapper lecturerMapper;
    @Autowired
    private MessageService messageService;

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
    @Override
    public Map<String, Object> getLecturers(String email, Integer uid, String name, String phone, Integer size, Integer page) {
        // Calculate offset
        Integer offset = (page - 1) * size;
        List<Lecturer> lecturerList = lecturerMapper.getLecturers(email, uid, name, phone, size, offset);
        Map<String, Object> map = new HashMap<>();
        int total = lecturerMapper.count();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("lecturers", lecturerList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
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
    public void createLecturer(String email, Integer uid, String name, String phone) {
        lecturerMapper.createLecturer(email, uid, name, phone);

        messageService.createMessage(
                uid,
                "Welcome to the System",
                "Dear " + name + ", your lecturer account has been created successfully.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );

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
    public void updateLecturerByEmail(String email, Integer uid, String name, String phone) {
        lecturerMapper.updateLecturerByEmail(email, uid, name, phone);

        messageService.createMessage(
                uid,
                "Student Information Updated",
                "Dear " + name + ", your lecturer information has been successfully updated.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    /**
     * Deletes the lecturer specified by email.
     *
     * @param email the provided email
     */
    @Override
    public void deleteLecturer(String email) {
        lecturerMapper.deleteLecturerByEmail(email);

        messageService.createMessage(
                1,
                "Lecturer Account Deletion",
                "Lecturer account associated with the email " + email + " has been successfully deleted.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    @Override
    public int getTotalLecturers() {
        return lecturerMapper.count();
    }
}
