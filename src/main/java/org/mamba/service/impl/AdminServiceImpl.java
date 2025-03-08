package org.mamba.service.impl;

import org.mamba.entity.Admin;
import org.mamba.entity.Message;
import org.mamba.entity.Room;
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
    public List<Admin> getAdmins() {
        return adminMapper.getAdmins();
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
            Room nearestRoom = roomService.findNearestAvailableRoom(roomId,oldStartTime, oldEndTime, userRole, occupyStartTime, occupyEndTime);
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
}