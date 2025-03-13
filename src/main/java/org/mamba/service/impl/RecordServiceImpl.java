package org.mamba.service.impl;

import org.mamba.entity.Lecturer;
import org.mamba.entity.Record;
import org.mamba.entity.Room;
import org.mamba.entity.User;
import org.mamba.mapper.RecordMapper;
import org.mamba.mapper.RoomMapper;
import org.mamba.service.*;
import org.mamba.service.RoomService;
import org.mamba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;


    @Override
    public void approveRestrictedRoomRecord(Integer id){
        Record record = recordMapper.getRecords(id, null, null,null,null,null,null,null,null,null).get(0);
        recordMapper.approveRestrictedRoomRecord(id);
        Room room = roomMapper.getRoomById(record.getRoomId());
        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Approved",
                "Your reservation for room " + room.getRoomName() + " has been approved.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    @Override
    public void rejectRestrictedRoomRecord(Integer id){
        Record record = recordMapper.getRecords(id, null, null,null,null,null,null,null,null,null).get(0);
        recordMapper.deleteRecordById(id);
        Room room = roomMapper.getRoomById(record.getRoomId());
        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Rejected",
                "Your reservation for room " + room.getRoomName() + " has been rejected.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    /**
     * Obtains the record list based on the conditions given.
     *
     * @param roomId       the room id
     * @param userId       the user id
     * @param startTime    the start time (the query result should be later than this)
     * @param endTime      the end time (the query result should be earlier than this)
     * @param hasCheckedIn whether the user has checked in
     * @param size         the size of each page
     * @param page         the page No.
     */
    @Override
    public Map<String, Object> getRecords(Integer id, Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn,String status, Integer size, Integer page, Boolean isApproved) {
        // Calculate offset
        Integer offset = (page - 1) * size;
        Integer statusId = null;
        if (status!=null){
            switch (status){
                case "Pending":
                    statusId = 1;
                    break;
                case "Ongoing":
                    statusId = 2;
                    break;
                case "Done":
                    statusId = 3;
                    break;
                case "Cancelled":
                    statusId = 3;
                    break;
                case "Overdue":
                    statusId = 3;
                    break;
                default:
                    statusId = 1;
            }
        }
        List<Record> recordList = recordMapper.getRecords(id, roomId, userId, startTime, endTime, hasCheckedIn,statusId, size, offset,isApproved);

        // Obtain the corresponding room of each record
        for (Record record : recordList) {
            int recordRoomId = record.getRoomId();
            List<Room> recordRoomList = roomMapper.getRooms(recordRoomId, null, null, null, null, null, null, null, null, null);
            // (This list should contain only one room)
            Room recordRoom = recordRoomList.get(0);
            record.setCorrespondingRoom(recordRoom);
            record.setStatus(convertStatus(record.getStatusId()));
        }

        Map<String, Object> map = new HashMap<>();

        // TODO
        int total = recordMapper.count(id, roomId, userId, startTime, endTime, hasCheckedIn,statusId);
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("records", recordList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
    }

    @Override
    public List<Record> getRestrictedRecords() {
        return recordMapper.getRecords(null,null,null,null,null,null,null,null,null,false);
    }

    private String convertStatus(Integer statusId) {
        switch (statusId) {
            case 1:
                return "Pending";
            case 2:
                return "Ongoing";
            case 3:
                return "Done";
            case 4:
                return "Cancelled";
            case 5:
                return "Overdue";
            default:
                return "Unknown";
        }
    }
    /**
     * Obtains the record specified by ID given.
     *
     * @param id the provided id
     * @return the corresponding record, could be null
     */
    @Override
    public Record getRecordById(int id) {
        return recordMapper.getRecords(id, null, null,null,null,null,null,null,null,null).get(0);
    }

    /**
     * Insert a new record.
     *
     * @param roomId       Room ID
     * @param userId       User ID
     * @param startTime    Start Time
     * @param endTime      End Time
     * @param hasCheckedIn whether the user has checked in
     */
    @Override
    public void createRecord(Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn) {
        User user = userService.getUserByUid(userId);
        String role = user.getRole();
        Room room = roomService.getRoomById(roomId);

        LocalDateTime recordTime = LocalDateTime.now();

        //if the room requires admin's approval and the user is not the admin
        if(room.isRequireApproval() && !role.contains("003")){
            recordMapper.createRecord(roomId, userId, startTime, endTime, recordTime, hasCheckedIn,false);
            throw new IllegalArgumentException("Booking this room needs to get the approval from the admin, please wait for the admin to approve your request.");
        }

        // Obtain the current time
        recordMapper.createRecord(roomId, userId, startTime, endTime, recordTime, hasCheckedIn, true);
        messageService.createMessage(
                userId,
                "Reserve Room successfully",
                "Your room reservation for: " + room.getRoomName() + " has been successfully created. The reservation is from " + startTime + " to " + endTime + ". Please check in on time.",
                recordTime,
                false,
                "System Notification"
        );
    }

    /**
     * Deletes the record specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void deleteRecordById(Integer id) {
        recordMapper.deleteRecordById(id);
    }

    /**
     * Cancel the record specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void cancelRecordById(Integer id) {
        Record record = recordMapper.getRecords(id, null, null,null,null,null,null,null,null,null).get(0);
        Room room = roomMapper.getRoomById(record.getRoomId());
        recordMapper.cancelRecordById(id);

        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Cancellation",
                "Your room reservation for: " + room.getRoomName() + " has been successfully cancelled.",
                LocalDateTime.now(),
                false,
                "System Notification"
        );
    }

    @Override
    public List<Record> findRecordsByRoomAndTimeRange(String roomName, LocalDateTime occupyStartTime, LocalDateTime occupyEndTime) {
        Room room = roomMapper.getRoomByName(roomName);
        return recordMapper.findRecordsByRoomAndTimeRange(room.getId(), occupyStartTime, occupyEndTime);
    }

    @Override
    public void checkIn(Integer id) {
        recordMapper.checkIn(id);
    }

    /**
     * Checks if the user is allowed to reserve the room.
     * @param roomId
     * @param userId
     * @return
     */
    @Override
    public boolean allowReserve(Integer roomId, Integer userId) {
        Room room = roomMapper.getRoomById(roomId);
        if (room.isRestricted()){
            List<Integer> permissionUsers = roomMapper.getPermissionUser(roomId);
            if (permissionUsers.contains(userId)) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Counts the total number of records.
     *
     * @return the total number of records
     */
    @Override
    public int countRecords() {
        return recordMapper.countRecords();
    }


    @Override
    public int countTeacherOrders() {
        return recordMapper.countTeacherOrders();
    }

    @Override
    public int countStudentOrders() {
        return recordMapper.countStudentOrders();
    }

    @Override
    public int countCompletedOrders() {
        return recordMapper.countCompletedOrders();
    }

    @Override
    public int countIncompleteOrders() {
        return recordMapper.countIncompleteOrders();
    }

    @Override
    public Map<String, Object> getRecordsByRoomAndTime(String roomName, LocalDateTime startTime, LocalDateTime endTime) {
        Room room = roomMapper.getRoomByName(roomName);
        return getRecords(null,room.getId(),null,startTime,endTime,null,null,null,null,null);
    }

    @Override
    public void createAdminRecord(String roomName, LocalDateTime startTime, LocalDateTime endTime) {
        Room room = roomMapper.getRoomByName(roomName);
        createRecord(room.getId(),1,startTime,endTime,true);
    }

    /**
     * automatically updating
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    @Transactional
    @Override
    public void updateStatus() {
        recordMapper.updateRecordStatus();
    }
}


///**
// * Obtains the record specified by ID given.
// *
// * @param id the provided id
// * @return the corresponding record, could be null
// */
//@Override
//public Record getRecordById(int id) {
//    return recordMapper.getRecordById(id);
//}
//
///**
// * Obtains the list of records specified by the User ID given.
// *
// * @param userId the provided User ID
// * @return the corresponding record list
// */
//@Override
//public List<Record> getRecordsByUserId(int userId) {
//    return recordMapper.getRecordsByUserId(userId);
//}
//
///**
// * Obtains the list of records specified by the Room ID given.
// *
// * @param roomId the provided Room ID
// * @return the corresponding record list
// */
//@Override
//public List<Record> getRecordsByRoomId(int roomId) {
//    return recordMapper.getRecordsByRoomId(roomId);
//}
//
///**
// * Obtains the list of records specified by the start time & end time range.
// * Only returns records that have the start time later than the time provided
// * and the end time earlier than the time provided.
// *
// * @param startTime the provided start time
// * @param endTime   the provided end time
// * @return the corresponding record list
// */
//@Override
//public List<Record> getRecordsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
//    return recordMapper.getRecordsByTimeRange(startTime, endTime);
//}
