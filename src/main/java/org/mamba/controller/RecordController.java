package org.mamba.controller;

import org.mamba.entity.Record;
import org.mamba.entity.Result;
import org.mamba.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/records") // TODO 确认
public class RecordController {
    @Autowired
    private RecordService recordService;

    /**
     * Obtains the record list based on the conditions given.
     *
     * @param roomId    the room id
     * @param userId    the user id
     * @param startTime the start time (the query result should be later than this)
     * @param endTime   the end time (the query result should be earlier than this)
     * @param pageSize  the size of each page
     * @param offset    the offset
     * @return the list of all the records
     */
    // TODO 注脚，参数传递
    public Result getRecords(int id, int roomId, int userId, LocalDateTime startTime, LocalDateTime endTime, int pageSize, Integer offset) {
        List<Record> records = recordService.getRecords(id, roomId, userId, startTime, endTime, pageSize, offset);
        return Result.success(records);
    }

    /**
     * Insert a new record.
     *
     * @param roomId    Room ID
     * @param userId    User ID
     * @param startTime Start Time
     * @param endTime   End Time
     */
    // TODO 注脚，参数传递
    public Result createRecord(int roomId, int userId, LocalDateTime startTime, LocalDateTime endTime) {
        recordService.createRecord(roomId, userId, startTime, endTime);
        return Result.success();
    }

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     */
    // TODO 注脚，参数传递
    public Result deleteRecordById(int id) {
        recordService.deleteRecordById(id);
        return Result.success();
    }
}


///**
// * Obtains the record specified by ID given.
// *
// * @param id the provided id
// */
//public Result getRecordById(int id) {
//    Record record = recordService.getRecordById(id);
//
//    // Check if it is null or not
//    if (record != null) {
//        return Result.success(record);
//    } else {
//        return Result.error("Record with id: " + id + " not found!");
//    }
//}
//
///**
// * Obtains the list of records specified by the User ID given.
// *
// * @param userId the provided User ID
// */
//public Result getRecordsByUserId(int userId) {
//    List<Record> records = recordService.getRecordsByUserId(userId);
//    return Result.success(records);
//}
//
///**
// * Obtains the list of records specified by the Room ID given.
// *
// * @param roomId the provided Room ID
// */
//public Result getRecordsByRoomId(int roomId) {
//    List<Record> records = recordService.getRecordsByRoomId(roomId);
//    return Result.success(records);
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
//public Result getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
//    List<Record> records = recordService.getRecordsByTimeRange(startTime, endTime);
//    return Result.success(records);
//}
