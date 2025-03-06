package org.mamba.service.impl;

import org.mamba.entity.Room;
import org.mamba.entity.Record;
import org.mamba.service.AdminService;
import org.mamba.service.RecordService;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RecordService recordService;

    /**
     * Deletes the record specified by id and reassigns the room.
     *
     * @param recordId the provided record id
     */
    @Override
    public void reassignRoomForRecord(Integer recordId) {
        Record record = recordService.getRecordById(recordId);
//        if (record == null) {
//            throw new IllegalArgumentException("Record not found");
//        }

        Room nearestRoom = roomService.findNearestAvailableRoom(record.getRoomId(),record.getStartTime(), record.getEndTime());
//        if (nearestRoom != null) {
//            recordService.createRecord(nearestRoom.getId(), record.getUserId(), record.getStartTime(), record.getEndTime(), record.isHasCheckedIn());
//            recordService.deleteRecordById(recordId);
//        } else {
//            throw new IllegalStateException("No available room found");
//        }
        recordService.createRecord(nearestRoom.getId(), record.getUserId(), record.getStartTime(), record.getEndTime(), record.isHasCheckedIn());
        recordService.deleteRecordById(recordId);
    }
}