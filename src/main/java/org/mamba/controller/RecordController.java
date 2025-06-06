package org.mamba.controller;

import org.mamba.entity.Record;
import org.mamba.entity.Result;
import org.mamba.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/records")
public class RecordController {
    @Autowired
    private RecordService recordService;

    /**
     * Obtains the record list based on the conditions given.
     *
     * @param id           the record id
     * @param roomId       the room id
     * @param userId       the user id
     * @param startTime    the start time (the query result should be later than this)
     * @param endTime      the end time (the query result should be earlier than this)
     * @param hasCheckedIn whether the user has checked in
     * @param size         the size of each page
     * @param page         the page No.
     * @return the list of all the records
     */
    @RequestMapping("/getRecords")
    public Result getRecords(@RequestParam(required = false) Integer id,
                             @RequestParam(required = false) Integer roomId,
                             @RequestParam(required = false) Integer userId,
                             @RequestParam(required = false) LocalDateTime startTime,
                             @RequestParam(required = false) LocalDateTime endTime,
                             @RequestParam(required = false) Boolean hasCheckedIn,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) Integer size,
                             @RequestParam(required = false) Integer page,
                             @RequestParam(required = false) Boolean isApproved) {
        Map<String, Object> recordsResult = recordService.getRecords(id, roomId, userId, startTime, endTime,hasCheckedIn,status, size, page, isApproved);
        return Result.success(recordsResult);
    }

    @PostMapping("/extend")
    public Result extendRecord(@RequestBody Map<String, Object> request) {
        Integer recordId = Integer.parseInt(request.get("id").toString());
        LocalDateTime newEndTime = LocalDateTime.parse(request.get("endTime").toString());
        recordService.extend(recordId,newEndTime);
        return Result.success();
    }

    @RequestMapping("/allowReserve")
    public Result allowReserve(Integer roomId, Integer userId) {
        boolean result = recordService.allowReserve(roomId, userId);
        if (result) {
            return Result.success("allow");
        }else {
            return Result.error("reserve failed");
        }
    }

    /**
     * Approves the record specified by ID.
     *
     * @return the result of the approval operation
     */
    @PutMapping("/updateUserId")
    public Result updateRecordUserId(@RequestBody Map<String, Object> request) {
        Integer roomId = (Integer) request.get("roomId");
        Integer newUserId = (Integer) request.get("newUserId");
        LocalDateTime startTime = LocalDateTime.parse(request.get("startTime").toString());
        LocalDateTime endTime = LocalDateTime.parse(request.get("endTime").toString());
        recordService.updateRecordUserId(roomId, newUserId, startTime, endTime);
        return Result.success();
    }

    @DeleteMapping("/reject")
    public Result reject(@RequestBody Map<String, Object> request) {
        Integer roomId = (Integer) request.get("roomId");
        LocalDateTime startTime = LocalDateTime.parse(request.get("startTime").toString());
        LocalDateTime endTime = LocalDateTime.parse(request.get("endTime").toString());
        recordService.reject(roomId, 0, startTime, endTime);
        return Result.success();
    }
    /**
     * Insert a new record.
     *
     * @param record the record to be created
     * @return the result of the creation operation
     */
    @PostMapping
    public Result createRecord(@RequestBody Record record) {
        recordService.createRecord(record.getRoomId(), record.getUserId(), record.getStartTime(), record.getEndTime(), record.isHasCheckedIn(), record.getComment());
        return Result.success();
    }

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     * @return the result of the deletion operation
     */
    @DeleteMapping("/delete")
    public Result deleteRecordById(Integer id) {
        recordService.deleteRecordById(id);
        return Result.success();
    }

    /**
     * Cancel the record specified by id.
     *
     * @param id  the provided id
     * @return the result of the cancellation operation
     */
    @PutMapping("/cancel")
    public Result cancelRecordById(Integer id, String reason) {
        recordService.cancelRecordById(id,reason);
        return Result.success();
    }
    @PutMapping("/checkin")
    public Result checkedIn(Integer id) {
        recordService.checkIn(id);
        return Result.success();
    }

    /**
     * Counts the total number of records.
     *
     * @return the total number of records
     */
    @GetMapping("/count")
    public Result countRecords() {
        int count = recordService.countRecords();
        return Result.success(count);
    }

    /**
     * Counts the total number of orders for teachers.
     *
     * @return the total number of orders for teachers
     */
    @GetMapping("/count/teachers")
    public Result countTeacherOrders() {
        int count = recordService.countTeacherOrders();
        return Result.success(count);
    }

    /**
     * Counts the total number of orders for students.
     *
     * @return the total number of orders for students
     */
    @GetMapping("/count/students")
    public Result countStudentOrders() {
        int count = recordService.countStudentOrders();
        return Result.success(count);
    }

    /**
     * Counts the total number of completed orders.
     *
     * @return the total number of completed orders
     */
    @GetMapping("/count/completed")
    public Result countCompletedOrders() {
        int count = recordService.countCompletedOrders();
        return Result.success(count);
    }

    /**
     * Counts the total number of incomplete orders.
     *
     * @return the total number of incomplete orders
     */
    @GetMapping("/count/incomplete")
    public Result countIncompleteOrders() {
        int count = recordService.countIncompleteOrders();
        return Result.success(count);
    }

    @GetMapping("/countOrdersByDayOfWeek")
    public Result countOrdersByDayOfWeek() {
        List<Map<String, Object>> ordersCount = recordService.countOrdersByDayOfWeek();
        return Result.success(ordersCount);
    }
}