package org.mamba.service.impl;

import org.mamba.entity.Lecturer;
import org.mamba.entity.Record;
import org.mamba.mapper.RecordMapper;
import org.mamba.mapper.RoomMapper;
import org.mamba.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    private RecordMapper recordMapper;

    /**
     * Obtains the record list based on the conditions given.
     *
     * @param roomId       the room id
     * @param userId       the user id
     * @param startTime    the start time (the query result should be later than this)
     * @param endTime      the end time (the query result should be earlier than this)
     * @param hasCheckedIn whether the user has checked in
     * @param size         the size of each page
     * @param page         the page No.
     */
    @Override
    public Map<String, Object> getRecords(Integer id, Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn, Integer size, Integer page) {
        // Calculate offset
        Integer offset = (page - 1) * size;
        List<Record> recordList = recordMapper.getRecords(id, roomId, userId, startTime, endTime, hasCheckedIn, size, offset);
        Map<String, Object> map = new HashMap<>();
        int total = recordList.size();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("records", recordList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
    }

    /**
     * Insert a new record.
     *
     * @param roomId       Room ID
     * @param userId       User ID
     * @param startTime    Start Time
     * @param endTime      End Time
     * @param hasCheckedIn whether the user has checked in
     */
    @Override
    public void createRecord(Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn) {
        // Obtain the current time
        LocalDateTime recordTime = LocalDateTime.now();
        recordMapper.createRecord(roomId, userId, startTime, endTime, recordTime, hasCheckedIn);
    }

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void deleteRecordById(Integer id) {
        recordMapper.deleteRecordById(id);
    }
}


///**
// * Obtains the record specified by ID given.
// *
// * @param id the provided id
// * @return the corresponding record, could be null
// */
//@Override
//public Record getRecordById(int id) {
//    return recordMapper.getRecordById(id);
//}
//
///**
// * Obtains the list of records specified by the User ID given.
// *
// * @param userId the provided User ID
// * @return the corresponding record list
// */
//@Override
//public List<Record> getRecordsByUserId(int userId) {
//    return recordMapper.getRecordsByUserId(userId);
//}
//
///**
// * Obtains the list of records specified by the Room ID given.
// *
// * @param roomId the provided Room ID
// * @return the corresponding record list
// */
//@Override
//public List<Record> getRecordsByRoomId(int roomId) {
//    return recordMapper.getRecordsByRoomId(roomId);
//}
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
//@Override
//public List<Record> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
//    return recordMapper.getRecordsByTimeRange(startTime, endTime);
//}
