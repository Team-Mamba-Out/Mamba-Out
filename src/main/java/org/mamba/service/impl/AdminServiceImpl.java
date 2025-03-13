package org.mamba.service.impl;

import org.mamba.entity.*;
import org.mamba.entity.Record;
import org.mamba.mapper.AdminMapper;
import org.mamba.service.AdminService;
import org.mamba.service.UserService;
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

    public static void main(String[] args) {
        String role = "2542737@dundee.ac.uk-001";
        String[] result = parseEmailAndCode(role);
        System.out.println("Email: " + result[0]);
        System.out.println("Code: " + result[1]);
    }

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
    public void deleteAndReassignRoom(String roomName, LocalDateTime occupyStartTime, LocalDateTime occupyEndTime, String reason) {
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

        // Create a new reservation for the occupied room
        recordService.createRecord(roomId, 1, occupyStartTime, occupyEndTime, true);

        // Iterate through extracted records and reassign each user to a new room
        for (Map<String, Object> recordData : extractedRecords) {
            Integer userID = (Integer) recordData.get("userID");
            LocalDateTime oldStartTime = (LocalDateTime) recordData.get("startTime");
            LocalDateTime oldEndTime = (LocalDateTime) recordData.get("endTime");
            String userRole = (String) recordData.get("role");

            // Find the nearest available room for reassignment
            Room nearestRoom = roomService.findNearestAvailableRoom(roomId, oldStartTime, oldEndTime, userRole, occupyStartTime, occupyEndTime);
            if (nearestRoom == null) {
                throw new IllegalStateException("No available room found for user " + userID);
            }

            // Create a new reservation for the reassigned room
            recordService.createRecord(nearestRoom.getId(), userID, oldStartTime, oldEndTime, false);

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