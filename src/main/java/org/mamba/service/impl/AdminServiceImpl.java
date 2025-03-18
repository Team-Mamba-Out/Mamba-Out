package org.mamba.service.impl;

import org.mamba.Utils.EmailManager;
import org.mamba.entity.*;
import org.mamba.entity.Record;
import org.mamba.mapper.AdminMapper;
import org.mamba.service.AdminService;
import org.mamba.service.UserService;
import org.mamba.service.MaintenanceService;
import org.mamba.service.MessageService;
import org.mamba.service.RecordService;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private MaintenanceService maintenanceService;

    @Override
    public Map<String, Object> getAdmins(String email, Integer uid, String name, String phone, Integer size, Integer page) {
        Integer offset = (page - 1) * size;
        List<Admin> adminList = adminMapper.getAdmins(email, uid, name, phone, size, offset);
        Map<String, Object> map = new HashMap<>();
        int total = adminMapper.count();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("admins", adminList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
    }

    @Override
    public String getNameByUid(Integer uid) {
        String information = adminMapper.getRoleByUid(uid);
        String[] result = parseEmailAndCode(information);
        return adminMapper.getNameByEmailAndRole(result[0], result[1]);
    }

    /**
     * Cancel a record and reassign the user to a new room.
     *
     * @param recordId the ID of the record to cancel
     * @param reason   the reason for the cancellation
     */
    @Override
    public void cancelRecordAndReassign(Integer recordId, String reason) {
        Record record = recordService.getRecordById(recordId);
        User user = userService.getUserByUid(record.getUserId());
        Integer userID = user.getUid();
        LocalDateTime oldStartTime = record.getStartTime();
        String nearestRoomInfo = roomService.findNearAvailableRoom(record.getRoomId(),record.getStartTime(), record.getEndTime(), user.getUid());

        if (nearestRoomInfo == null) {
            // email logic: cancelled because reassignment failed
            EmailManager.sendRecordCancelledEmail(userService.getUserByUid(userID).getRole().split("-")[0], false);

            throw new IllegalStateException("No available room found for user " + userID);
        }

        String[] roomInfo = parseRoomInfo(nearestRoomInfo);

        Integer roomId = Integer.parseInt(roomInfo[0]);

        Room nearestRoom = roomService.getRoomById(roomId);
        LocalDateTime newStartTime = LocalDateTime.parse(roomInfo[1]);
        LocalDateTime newEndTime = LocalDateTime.parse(roomInfo[2]);

        recordService.deleteRecordById(recordId);
        recordService.createRecord(nearestRoom.getId(), userID, newStartTime, newEndTime, false, null);

        // Send a notification message about the reassignment
        messageService.createMessage(
                userID,
                "Room Reassignment Notification",
                "Your reserved room at " + oldStartTime + " is no longer available. " +
                        "You have been reassigned to room " + roomId + " from " + newStartTime + " to " + newEndTime +
                        ". The reason is: " + reason + ". Please check your reservation details.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );

        // email logic: cancel and reassign
        EmailManager.sendRecordCancelledEmail(userService.getUserByUid(userID).getRole().split("-")[0], true);

    }

    /**
     * Parse the room information.
     * @param roomInfo the room information
     * @return the room information
     */
    public static String[] parseRoomInfo(String roomInfo) {
        return roomInfo.split(",");
    }

    /**
     * Parse the email and code.
     * @param role the role
     * @return the email and code
     */
    public static String[] parseEmailAndCode(String role) {
        String regex = "(.+)-([0-9]{3})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(role);

        if (matcher.find()) {
            String email = matcher.group(1);
            String code = matcher.group(2);
            return new String[]{email, code};
        } else {
            throw new IllegalArgumentException("Invalid role format");
        }
    }

    /**
     * Approve a record.
     *
     * @param id the id of the record to approve
     */
    @Override
    public void approveRestrictedRoomRecord(Integer id) {
        recordService.approveRestrictedRoomRecord(id);
    }

    /**
     * Reject a record.
     *
     * @param id the id of the record to reject
     */
    @Override
    public void rejectRestrictedRoomRecord(Integer id) {
        recordService.rejectRestrictedRoomRecord(id);
    }


    @Override
    public void createAdmin(String email, Integer uid, String name, String phone) {
        adminMapper.createAdmin(email, uid, name, phone);

        messageService.createMessage(
                uid,
                "Welcome to the System",
                "Dear " + name + ", your admin account has been created successfully.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    @Override
    public void updateAdminByEmail(String email, Integer uid, String name, String phone) {
        adminMapper.updateAdminByEmail(email, uid, name, phone);

        messageService.createMessage(
                uid,
                "Admin Information Updated",
                "Dear " + name + ", your admin information has been successfully updated.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    @Override
    public int getUserAccount() {
        return adminMapper.userAccount();
    }


    @Override
    public void occupyAndReassignRoom(String roomName, LocalDateTime occupyStartTime, LocalDateTime occupyEndTime, String reason) {
        // Retrieve the list of records within the specified time range for the given room
        List<Record> list = recordService.findRecordsByRoomAndTimeRange(roomName, occupyStartTime, occupyEndTime);
        Integer roomId = roomService.getRoomByName(roomName).getId();  // Get the room ID

        // Create a list to store extracted reservation data
        List<Map<String, Object>> extractedRecords = new ArrayList<>();

        for (Record record : list) {
            Map<String, Object> recordData = new HashMap<>();
            recordData.put("userID", record.getUserId());
            recordData.put("startTime", record.getStartTime());
            recordData.put("endTime", record.getEndTime());

            // Retrieve the user's role
            String userRole = userService.getUserByUid(record.getUserId()).getRole();
            recordData.put("role", userRole);

            extractedRecords.add(recordData);
            recordService.deleteRecordById(record.getId());
        }

        maintenanceService.createMaintenance(roomId,occupyStartTime,occupyEndTime,reason);

        // Iterate through extracted records and reassign each user to a new room
        for (Map<String, Object> recordData : extractedRecords) {
            Integer userID = (Integer) recordData.get("userID");
            LocalDateTime oldStartTime = (LocalDateTime) recordData.get("startTime");
            LocalDateTime oldEndTime = (LocalDateTime) recordData.get("endTime");

            // Find the nearest available room for reassignment
            Room nearestRoom = roomService.findNearestAvailableRoom(roomId, oldStartTime, oldEndTime, userID);
            if (nearestRoom == null) {
                // email logic: cancelled because reassignment failed
                EmailManager.sendRecordCancelledEmail(userService.getUserByUid(userID).getRole().split("-")[0], false);

                throw new IllegalStateException("No available room found for user " + userID);
            }

            // Create a new reservation for the reassigned room
            recordService.createRecord(nearestRoom.getId(), userID, oldStartTime, oldEndTime, false, null);

            // Retrieve the newly assigned record
            Record newRecord = recordService.getRecordById(nearestRoom.getId());

            // Send a notification message about the reassignment
            messageService.createMessage(
                    userID,
                    "Room Reassignment Notification",
                    "Your reserved room at " + oldStartTime + " is no longer available. " +
                            "You have been reassigned to room " + newRecord.getRoomId() + " from " + newRecord.getStartTime() + " to " + newRecord.getEndTime() +
                            ". The reason is: " + reason + ". Please check your reservation details.",
                    LocalDateTime.now(),
                    false,
                    "System Notification"
            );

            // email logic: cancel and reassign
            EmailManager.sendRecordCancelledEmail(userService.getUserByUid(userID).getRole().split("-")[0], true);
        }
    }

    @Override
    public List<Map<String, Object>> getAllRecordsWithRoomNames() {
        Map<String, Object> recordsMap = recordService.getRecords(
                null,  // id
                null,  // roomId
                null,  // userId
                null,  // startTime
                null,  // endTime
                null,  // hasCheckedIn
                null,  // isCancelled
                Integer.MAX_VALUE,
                1,
                null  //isApproved
        );

        List<Record> records = (List<Record>) recordsMap.get("records");

        List<Map<String, Object>> recordsWithRoomNames = new ArrayList<>();

        if (records != null) {
            for (Record record : records) {
                Map<String, Object> recordData = new HashMap<>();
                recordData.put("record", record);
                Room room = roomService.getRoomById(record.getRoomId());
                recordData.put("roomName", room.getRoomName());
                recordData.put("roomType", room.getRoomType());
                recordsWithRoomNames.add(recordData);
            }
        }

        return recordsWithRoomNames;
    }


}