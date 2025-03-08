package org.mamba.controller;

import org.mamba.entity.Admin;
import org.mamba.entity.Result;
import org.mamba.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;

    /**
     * Obtains the admin list.
     *
     * @return the list of all admins
     */
    @GetMapping
    public Result getAdmins() {
        List<Admin> admins = adminService.getAdmins();
        return Result.success(admins);
    }

    /**
     * Insert a new admin.
     *
     * @param admin the admin to be created
     * @return the result of the creation operation
     */
    @PostMapping
    public Result createAdmin(@RequestBody Admin admin) {
        adminService.createAdmin(admin.getEmail(), admin.getUid(), admin.getName(), admin.getPhone());
        return Result.success();
    }

    /**
     * Update the information of an admin by email.
     *
     * @param email the admin's email
     * @param admin the admin with updated information
     * @return the result of the update operation
     */
    @PutMapping("/{email}")
    public Result updateAdminByEmail(@PathVariable String email, @RequestBody Admin admin) {
        adminService.updateAdminByEmail(email, admin.getUid(), admin.getName(), admin.getPhone());
        return Result.success();
    }

    /**
     * Queries records based on room name, start time, and end time.
     *
     * @param roomName  the name of the room
     * @param startTime the start time
     * @param endTime   the end time
     * @return the list of records matching the criteria
     */
//    @GetMapping("/records")
//    public Result getRecordsByRoomNameAndTime(@RequestParam String roomName,
//                                              @RequestParam LocalDateTime startTime,
//                                              @RequestParam LocalDateTime endTime) {
//        Map<String, Object> recordsResult = recordService.getRecords(null, null, null, startTime, endTime, null, null, null, null);
//        return Result.success(recordsResult);
//    }

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