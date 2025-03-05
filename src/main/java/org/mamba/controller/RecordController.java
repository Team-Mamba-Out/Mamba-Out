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
    @GetMapping
    public Result getRecords(@RequestParam(required = false) Integer id,
                             @RequestParam(required = false) Integer roomId,
                             @RequestParam(required = false) Integer userId,
                             @RequestParam(required = false) LocalDateTime startTime,
                             @RequestParam(required = false) LocalDateTime endTime,
                             @RequestParam(required = false) Boolean hasCheckedIn,
                             @RequestParam(required = false) Integer size,
                             @RequestParam(required = false) Integer page) {
        Map<String, Object> recordsResult = recordService.getRecords(id, roomId, userId, startTime, endTime, hasCheckedIn, size, page);
        return Result.success(recordsResult);
    }

    /**
     * Insert a new record.
     *
     * @param record the record to be created
     * @return the result of the creation operation
     */
    @PostMapping
    public Result createRecord(@RequestBody Record record) {
        recordService.createRecord(record.getRoomId(), record.getUserId(), record.getStartTime(), record.getEndTime(), record.isHasCheckedIn());
        return Result.success();
    }

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     * @return the result of the deletion operation
     */
    @DeleteMapping("/{id}")
    public Result deleteRecordById(@PathVariable Integer id) {
        recordService.deleteRecordById(id);
        return Result.success();
    }
}