package org.mamba.service.impl;

import org.mamba.entity.Message;
import org.mamba.entity.Room;
import org.mamba.entity.Record;
import org.mamba.service.AdminService;
import org.mamba.service.UserService;
import org.mamba.service.MessageService;
import org.mamba.service.RecordService;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

    /**
     * Deletes the specified record by ID and reassigns the user to a new room.
     *
     * @param recordId    the ID of the record to be deleted
     * @param newStartTime the new start time for the updated reservation
     * @param newEndTime   the new end time for the updated reservation
     */
    @Override
    public void deleteAndReassignRoom(Integer recordId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        // Retrieve the original reservation record
        Record record = recordService.getRecordById(recordId);
//        if (record == null) {
//            throw new IllegalArgumentException("Record not found");
//        }

        Integer roomID = record.getRoomId();
        Integer userID = record.getUserId();
        LocalDateTime oldStartTime = record.getStartTime();

        // Retrieve the user's role
        String userRole = userService.getUserByUid(userID).getRole();

        // Delete the old reservation record and create a new one with updated time
        recordService.deleteRecordById(recordId);
        recordService.createRecord(roomID, 1, newStartTime, newEndTime, true);

        // Find the nearest available room
        Room nearestRoom = roomService.findNearestAvailableRoom(roomID, oldStartTime, record.getEndTime(), userRole, newStartTime, newEndTime);
//        if (nearestRoom == null) {
//            throw new IllegalStateException("No available room found");
//        }

        // Create a new reservation for the reassigned room
        recordService.createRecord(nearestRoom.getId(), userID, record.getStartTime(), record.getEndTime(), record.isHasCheckedIn());

        // Retrieve the newly assigned record
        Record newRecord = recordService.getRecordById(nearestRoom.getId());

        // Send a notification message about the reassignment
        messageService.createMessage(
                userID,
                "Room Reassignment Notification",
                String.format(
                        "Your reserved room %d at %s is no longer available. " +
                                "You have been reassigned to room %d from %s to %s. Please check your reservation details.",
                        roomID, oldStartTime,
                        newRecord.getRoomId(), newRecord.getStartTime(), newRecord.getEndTime()
                ),
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }


}