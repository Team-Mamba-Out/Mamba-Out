package org.mamba.service.impl;

import org.mamba.Utils.EmailManager;
import org.mamba.entity.*;
import org.mamba.entity.Record;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    private StudentService studentService;
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
    public void approveRestrictedRoomRecord(Integer id) {
        Record record = recordMapper.getRecords(id, null, null, null, null, null, null, null, null, null).get(0);
        recordMapper.approveRestrictedRoomRecord(id);
        Room room = roomMapper.getRoomById(record.getRoomId());
        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Approved",
                "Your reservation for room " + room.getRoomName() + " has been approved.",
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                record.getRoomId()
        );
        Integer userId = record.getUserId();
        String email = userService.getUserByUid(userId).getRole().split("-")[0];
        // Send email: approval
        EmailManager.sendRequestApprovedEmail(email, record);
    }

    @Override
    public void rejectRestrictedRoomRecord(Integer id) {
        Record record = recordMapper.getRecords(id, null, null, null, null, null, null, null, null, null).get(0);

        Integer userId = record.getUserId();
        String email = userService.getUserByUid(userId).getRole().split("-")[0];
        // Send email: reject
        EmailManager.sendRequestRejectedEmail(email, record);
        Room room = roomMapper.getRoomById(record.getRoomId());


        recordMapper.deleteRecordById(id);

        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Rejected",
                "Your reservation for room " + room.getRoomName() + " has been rejected.",
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                id
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
    public Map<String, Object> getRecords(Integer id, Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn, String status, Integer size, Integer page, Boolean isApproved) {
        // Calculate offset
        Integer offset = (page - 1) * size;
        Integer statusId = null;
        if (status != null) {
            switch (status) {
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
                    statusId = 4;
                    break;
                case "Overdue":
                    statusId = 5;
                    break;
                default:
                    statusId = 1;
            }
        }
        List<Record> recordList = recordMapper.getRecords(id, roomId, userId, startTime, endTime, hasCheckedIn, statusId, size, offset, isApproved);

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
        int total = recordMapper.count(id, roomId, userId, startTime, endTime, hasCheckedIn, statusId);
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("records", recordList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
    }

    @Override
    public List<Record> getRestrictedRecords(Integer room_id) {
        return recordMapper.getRecords(null, room_id, null, null, null, null, null, null, null, false);
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
        return recordMapper.getRecords(id, null, null, null, null, null, null, null, null, null).get(0);
    }

    /**
     * Insert a new record.
     *
     * @param roomId       Room ID
     * @param userId       User ID
     * @param startTime    Start Time
     * @param endTime      End Time
     * @param hasCheckedIn whether the user has checked in
     * @param comment      the user's request message
     */
    @Override
    public void createRecord(Integer roomId, Integer userId, LocalDateTime startTime, LocalDateTime endTime, Boolean hasCheckedIn, String comment) {
        User user = userService.getUserByUid(userId);
        String role = user.getRole();
        Room room = roomService.getRoomById(roomId);

        String email = role.split("-")[0];
        Record temp = new Record();
        temp.setCorrespondingRoom(room);
        temp.setStartTime(startTime);
        temp.setEndTime(endTime);

        LocalDateTime recordTime = LocalDateTime.now();

        //if the room requires admin's approval and the user is not the admin
        if (room.isRequireApproval() && !role.contains("003")) {
            recordMapper.createRecord(roomId, userId, startTime, endTime, recordTime, hasCheckedIn, false, comment);

            // Send email: booking successful, pending approval
            EmailManager.sendBookSuccessfulEmail(email, temp, true);

            throw new IllegalArgumentException("Booking this room needs to get the approval from the admin, please wait for the admin to approve your request.");
        }

        // Obtain the current time
        recordMapper.createRecord(roomId, userId, startTime, endTime, recordTime, hasCheckedIn, true, comment);
        messageService.createMessage(
                userId,
                "Reserve Room successfully",
                "Your room reservation for: " + room.getRoomName() + " has been successfully created. The reservation is from " + startTime + " to " + endTime + ". Please check in on time.",
                recordTime,
                false,
                "1;Jinhao Zhang",
                0,
                roomId
        );

        // Send email on booking created
        EmailManager.sendBookSuccessfulEmail(email, temp, false);
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


    @Override
    public void updateRecordUserId(Integer roomId, Integer newUserId, LocalDateTime startTime, LocalDateTime endTime) {
        Record record = recordMapper.getRecords(null, roomId, 0, startTime, endTime, null, null, null, null, null).get(0);
        record.setUserId(newUserId);
        recordMapper.updateRecordUserId(record.getId(), newUserId);
    }

    @Override
    public void reject(Integer roomId, Integer newUserId, LocalDateTime startTime, LocalDateTime endTime) {
        Record record = recordMapper.getRecords(null, roomId, 0, startTime, endTime, null, null, null, null, null).get(0);
        recordMapper.deleteRecordById(record.getId());
    }

    /**
     * Cancel the record specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void cancelRecordById(Integer id, String reason) {
        Record record = recordMapper.getRecords(id, null, null, null, null, null, null, null, null, null).get(0);
        Room room = roomMapper.getRoomById(record.getRoomId());
        recordMapper.cancelRecordById(id,reason);
        Integer userId = record.getUserId();
        String role = userService.getUserByUid(userId).getRole().split("-")[1];

        if (role.equals("001")) {
            Integer oldBreakTimer = studentService.getStudentByUid(userId).getBreakTimer();
            Integer newBreakTimer = oldBreakTimer < 4 ? oldBreakTimer + 1 : oldBreakTimer;
            studentService.updateBreakTimer(userId, newBreakTimer);
        }
        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Cancellation",
                "Your room reservation for: " + room.getRoomName() + " has been successfully cancelled.",
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                room.getId()
        );
    }

    /**
     * Cancel the record specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void cancelRecordByIdAdmin(Integer id,String comment) {
        Record record = recordMapper.getRecords(id, null, null, null, null, null, null, null, null, null).get(0);
        Room room = roomMapper.getRoomById(record.getRoomId());
        recordMapper.cancelRecordById(id,comment);
        Integer userId = record.getUserId();
        messageService.createMessage(
                record.getUserId(),
                "Room Reservation Cancelled By Admin",
                "Your room reservation for: " + room.getRoomName() + " has been successfully cancelled. " + "The reason is: " + comment,
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                room.getId()
        );
    }

    @Override
    public List<Record> findRecordsByRoomAndTimeRange(Integer roomId, LocalDateTime occupyStartTime, LocalDateTime occupyEndTime) {
        return recordMapper.findRecordsByRoomAndTimeRange(roomId, occupyStartTime, occupyEndTime);
    }

    @Override
    public void checkIn(Integer id) {
        recordMapper.checkIn(id);
    }

    /**
     * Checks if the user is allowed to reserve the room.
     *
     * @param roomId
     * @param userId
     * @return
     */
    @Override
    public boolean allowReserve(Integer roomId, Integer userId) {
        Room room = roomMapper.getRoomById(roomId);
        if (room.isRestricted()) {
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
        return getRecords(null, room.getId(), null, startTime, endTime, null, null, null, null, null);
    }

    @Override
    public void createAdminRecord(String roomName, LocalDateTime startTime, LocalDateTime endTime) {
        Room room = roomMapper.getRoomByName(roomName);
        createRecord(room.getId(), 1, startTime, endTime, true, null);
    }

    @Override
    public List<Map<String, Object>> countOrdersByDayOfWeek() {
        return recordMapper.countOrdersByDayOfWeek();
    }

    /**
     * automatically updating
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    @Override
    public void updateStatus() {
        List<Record> getNewStarts = recordMapper.getNewStarts();
        if (!getNewStarts.isEmpty()) {
            for (Record record : getNewStarts) {
                messageService.createMessage(
                        record.getUserId(),
                        "Room Reservation Beginning Soon",
                        "Your reservation for " + roomService.getRoomById(record.getRoomId()).getRoomName() + " will begin in 10 minutes. Please arrive in time and complete the check-in",
                        LocalDateTime.now(),
                        false,
                        "1;Jinhao Zhang",
                        0,
                        record.getRoomId()
                );
                Room room = roomMapper.getRoomById(record.getRoomId());
                record.setCorrespondingRoom(room);
                // Email Notification - Booking begins soon
                EmailManager.sendCheckInEmail(userService.getUserByUid(record.getUserId()).getRole().split("-")[0], record);
            }

        }

        List<Record> getNewEnds = recordMapper.getNewEnds();
        if (!getNewEnds.isEmpty()) {
            for (Record record : getNewEnds) {
                messageService.createMessage(
                        record.getUserId(),
                        "Room Reservation Ending Soon",
                        "Your reservation for " + roomService.getRoomById(record.getRoomId()).getRoomName() + " will end in 10 minutes. Please wrap up your activities and vacate the room on time. If you need to extend your reservation, please do so before it ends.",
                        LocalDateTime.now(),
                        false,
                        "1;Jinhao Zhang",
                        0,
                        record.getRoomId()
                );
                // Email Notification - Booking time almost up
                EmailManager.sendCheckInEmail(userService.getUserByUid(record.getUserId()).getRole().split("-")[0], record);
            }

        }
        List<Integer> updatedUids = recordMapper.getUsersWithNewStatus5();
        recordMapper.updateRecordStatus();
        if (!updatedUids.isEmpty()) {
            updatedUids.forEach(item -> {
                String role = userService.getUserByUid(item).getRole().split("-")[1];
                if (role.equals("001")) {
                    Integer oldBreakTimer = studentService.getStudentByUid(item).getBreakTimer();
                    Integer newBreakTimer = oldBreakTimer < 4 ? oldBreakTimer + 1 : oldBreakTimer;
                    studentService.updateBreakTimer(item, newBreakTimer);
                }
            });
        }
    }

    @Override
    public Map<String, Double> getCancellationReasonPercentagesByRoomAndTime(Integer roomId, Integer rangeType) {
        LocalDateTime startTime;

        if (rangeType.equals(1)) {
            startTime = LocalDateTime.now().minusMonths(1);
        } else if (rangeType.equals(2)) {
            startTime = LocalDateTime.now().minusMonths(2);
        } else if (rangeType.equals(3)) {
            startTime = LocalDateTime.now().minusMonths(3);
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }

        List<String> allReasons = Arrays.asList("Schedule Change", "Mistake", "Equipment Failure", "Other", "User Request");

        List<Map<String, Object>> counts = recordMapper.countCancellationReasonsByRoomAndTime(roomId, startTime);

        int totalCancellations = counts.stream()
                .mapToInt(map -> ((Number) map.get("count")).intValue())
                .sum();

        Map<String, Double> percentages = new HashMap<>();
        for (String reason : allReasons) {
            percentages.put(reason, 0.0);
        }

        for (Map<String, Object> count : counts) {
            String reason = count.containsKey("reason") ? (String) count.get("reason") : "UNKNOWN_REASON";
            if ("UNKNOWN_REASON".equals(reason)) {
                continue;
            }
            int reasonCount = ((Number) count.get("count")).intValue();
            double percentage = totalCancellations > 0 ? Math.round(((double) reasonCount / totalCancellations * 100) * 100.0) / 100.0 : 0.0;
            percentages.put(reason, percentage);
        }

        return percentages;
    }


    @Override
    public int countCancellationsByRoomAndTime(Integer roomId, Integer rangeType) {
        LocalDateTime startTime;

        if (rangeType.equals(1)) {
            startTime = LocalDateTime.now().minusMonths(1);
        } else if (rangeType.equals(2)) {
            startTime = LocalDateTime.now().minusMonths(2);
        } else if (rangeType.equals(3)) {
            startTime = LocalDateTime.now().minusMonths(3);
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }
        return recordMapper.countCancellationsByRoomAndTime(roomId, startTime);
    }

    @Override
    public double calculateCancellationRateByRoomAndTime(Integer roomId, Integer rangeType) {
        LocalDateTime startTime;

        if (rangeType.equals(1)) {
            startTime = LocalDateTime.now().minusMonths(1);
        } else if (rangeType.equals(2)) {
            startTime = LocalDateTime.now().minusMonths(2);
        } else if (rangeType.equals(3)) {
            startTime = LocalDateTime.now().minusMonths(3);
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }

        int totalRecords = recordMapper.countTotalRecordsByRoomAndTime(roomId, startTime);
        int cancellations = recordMapper.countCancellationsByRoomAndTime(roomId, startTime);

        if (totalRecords == 0) {
            return 0.0;
        }

        return Math.round(((double) cancellations / totalRecords * 100) * 100.0) / 100.0;
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
