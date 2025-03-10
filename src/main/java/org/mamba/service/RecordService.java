package org.mamba.service;
import org.mamba.entity.Record;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RecordService {
    /**
     * Obtains the record list based on the conditions given.
     *
     * @param id           the id (record)
     * @param roomId       the room id
     * @param userId       the user id
     * @param startTime    the start time (the query result should be later than this)
     * @param endTime      the end time (the query result should be earlier than this)
     * @param hasCheckedIn whether the user has checked in
     * @param size         the size of each page
     * @param page         the page No.
     */
    Map<String, Object> getRecords(Integer id, Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn,String status, Integer size, Integer page);

    /**
     * Obtains the record specified by ID given.
     *
     * @param id the provided id
     * @return the corresponding record, could be null
     */
    Record getRecordById(int id);

    /**
     * Insert a new record.
     *
     * @param roomId       Room ID
     * @param userId       User ID
     * @param startTime    Start Time
     * @param endTime      End Time
     * @param hasCheckedIn whether the user has checked in
     */
    void createRecord(Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn);

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     */
    void deleteRecordById(Integer id);

    /**
     * Cancel the record specified by id.
     *
     * @param id  the provided id
     */
    void cancelRecordById(Integer id);
    /**
     * automatically updating
     */
    void updateStatus();

    /**
     * Counts the total number of orders for teachers.
     *
     * @return the total number of orders for teachers
     */
    int countTeacherOrders();

    /**
     * Counts the total number of orders for students.
     *
     * @return the total number of orders for students
     */
    int countStudentOrders();

    /**
     * Counts the total number of completed orders.
     *
     * @return the total number of completed orders
     */
    int countCompletedOrders();

    /**
     * Counts the total number of incomplete orders.
     *
     * @return the total number of incomplete orders
     */
    int countIncompleteOrders();

    /**
     * Counts the total number of records.
     *
     * @return the total number of records
     */
    int countRecords();

    /**
     * Queries records based on room ID, start time, and end time.
     *
     * @param roomName    the room ID
     * @param startTime the start time
     * @param endTime   the end time
     * @return the list of records matching the criteria
     */
    Map<String, Object> getRecordsByRoomAndTime(String roomName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Queries records based on room ID, start time, and end time.
     *
     * @param roomName    the room ID
     * @param startTime the start time
     * @param endTime   the end time
     */
    void createAdminRecord(String roomName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Finds records for a given room within a specified time range, including those that partially overlap.
     *
     * @param roomName the name of the room
     * @param occupyStartTime the start time of the range
     * @param occupyEndTime the end time of the range
     * @return a list of records that overlap with the specified time range
     */
    List<Record> findRecordsByRoomAndTimeRange(String roomName, LocalDateTime occupyStartTime, LocalDateTime occupyEndTime);

    void checkIn(Integer id);
}


///**
// * Obtains the record specified by ID given.
// *
// * @param id the provided id
// * @return the corresponding record, could be null
// */
//Record getRecordById(int id);
//
///**
// * Obtains the list of records specified by the User ID given.
// *
// * @param userId the provided User ID
// * @return the corresponding record list
// */
//List<Record> getRecordsByUserId(int userId);
//
///**
// * Obtains the list of records specified by the Room ID given.
// *
// * @param roomId the provided Room ID
// * @return the corresponding record list
// */
//List<Record> getRecordsByRoomId(int roomId);
//
///**
// * Obtains the list of records specified by the start time & end time range.
// * Only returns records that have the start time later than the time provided
// * and the end time earlier than the time provided.
// *
// * @param startTime the provided start time
// * @param endTime   the provided end time
// * @return the corresponding record list
// */
//List<Record> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
