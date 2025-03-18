package org.mamba.controller;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.mamba.entity.Admin;
import org.mamba.entity.Result;
import org.mamba.service.impl.AdminServiceImpl;
import org.mamba.service.impl.RecordServiceImpl;
import org.mamba.service.impl.RoomServiceImpl;
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
    @Autowired
    private RecordServiceImpl recordService;
    @Autowired
    private RoomServiceImpl roomService;

    /**
     * Obtains the admin list.
     *
     * @return the list of all admins
     */
    @GetMapping
    public Result getAdmins(@RequestParam(required = false) String email,
                            @RequestParam(required = false) Integer uid,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String phone,
                            @RequestParam(required = false) Integer size,
                            @RequestParam(required = false) Integer page) {
        Map<String, Object> adminsResult = adminService.getAdmins(email, uid, name, phone, size, page);
        return Result.success(adminsResult);
    }

    @DeleteMapping("/cancelRecordAndReassign/{id}")
    public Result cancelRecordAndReassign(@PathVariable Integer id, @RequestBody String reason) {
        adminService.cancelRecordAndReassign(id, reason);
        return Result.success();
    }

    /**
     * Get the permission of the user in the room.
     *
     * @param room_id the room id
     * @return the result of the operation
     */
    @GetMapping("/getPermissionUser")
    public Result getPermissionUser(@RequestParam Integer room_id) {
        return Result.success(roomService.getPermissionUser(room_id));
    }

    /**
     * Update the information of a room by id.
     *
     * @param request the room with updated information
     * @return the result of the update operation
     */
    @PutMapping("/updateRoomPermission")
    public Result updateRoomPermission(@RequestBody Map<String,Object> request) {
        Integer room_id = Integer.parseInt(request.get("id").toString());
        List<Integer> permissionUsers = (List<Integer>) request.get("permissionUsers");
        roomService.updateRoomPermission(room_id,permissionUsers);
        return Result.success();
    }

    /**
     * Get the account of the users;
     * @return the corresponding count.
     */
    @PostMapping("/{id}/addUsers")
    public Result addUsersToRoom(@PathVariable Integer id, @RequestBody List<Integer> userIds) {
        roomService.updateRoomPermission(id, userIds);
        return Result.success();
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
     * Find restricted records.
     *
     * @return the result of the approval operation
     */
    @GetMapping("/getApprovalResources")
    public Result getApprovalResources(@RequestParam Integer roomId) {
        return Result.success(recordService.getRestrictedRecords(roomId));
    }

    /**
     * Get the name of the user by uid.
     * @param uid
     * @return
     */
    @GetMapping("/getNameByUid/{uid}")
    public Result getNameByUid(@PathVariable Integer uid) {
        return Result.success(adminService.getNameByUid(uid));
    }
    /**
     * Approve a record.
     *
     * @param id the id of the record to approve
     * @return the result of the approval operation
     */
    @PutMapping("/approveApprovalResource")
    public Result approveApprovalResource(@RequestParam Integer id) {
        adminService.approveRestrictedRoomRecord(id);
        return Result.success();
    }

    /**
     * Reject a record.
     *
     * @param id the id of the record to reject
     * @return the result of the rejection operation
     */
    @DeleteMapping("/rejectRestrictedResource")
    public Result rejectRestrictedResource(@RequestParam Integer id) {
        adminService.rejectRestrictedRoomRecord(id);
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
    @GetMapping("/records")
    public Result getRecordsByRoomNameAndTime(@RequestParam String roomName,
                                              @RequestParam LocalDateTime startTime,
                                              @RequestParam LocalDateTime endTime) {
        Map<String, Object> recordsResult = recordService.getRecordsByRoomAndTime(roomName, startTime, endTime);
        return Result.success(recordsResult);
    }

    @GetMapping("/getUserAccount")
    public Result getUserAccount() {
        return Result.success(adminService.getUserAccount());
    }
    /**
     * Deletes records for a specified room within a given time range and reassigns users to new rooms.
     *
     * @param roomName    the name of the room
     * @param newStartTime the new start time for the reassignment
     * @param newEndTime   the new end time for the reassignment
     * @param reason       the reason for the reassignment
     * @return a success result indicating the operation was completed
     */
    @DeleteMapping("/roomRecord/{roomName}")
    public Result deleteRecordAndReassignRoom(@PathVariable String roomName,
                                              @RequestParam LocalDateTime newStartTime,
                                              @RequestParam LocalDateTime newEndTime,
                                              @RequestParam String reason) {
        adminService.deleteAndReassignRoom(roomName, newStartTime, newEndTime, reason);
        return Result.success();
    }

    /**
     * Retrieves all records with their corresponding room names.
     *
     * @return a list of records with room names
     */
    @GetMapping("/recordsWithRoomNames")
    public Result getAllRecordsWithRoomNames() {
        List<Map<String, Object>> recordsWithRoomNames = adminService.getAllRecordsWithRoomNames();
        return Result.success(recordsWithRoomNames);
    }

    /**
     * Get the room utilization report
     *
     * @return Contains results for room utilization
     */
    @GetMapping("/roomUtilization")
    public Result getRoomUtilization() {
        Map<String, Double> utilizationReport = roomService.calculateRoomUtilization();
        return Result.success(utilizationReport);
    }
}