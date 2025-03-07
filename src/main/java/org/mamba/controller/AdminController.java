package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;

    /**
     * Deletes the record specified by id and reassigns the room.
     *
     * @param recordId the provided record id
     * @return a success result
     */
    @DeleteMapping("/record/{recordId}")
    public Result deleteRecordAndReassignRoom(@PathVariable Integer recordId,
                                              @RequestParam LocalDateTime newStartTime,
                                              @RequestParam LocalDateTime newEndTime) {
        adminService.deleteAndReassignRoom(recordId, newStartTime, newEndTime);
        return Result.success();
    }
}