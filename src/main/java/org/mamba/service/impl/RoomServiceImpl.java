package org.mamba.service.impl;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.mamba.entity.Maintenance;
import org.mamba.entity.Record;
import org.mamba.entity.Room;
import org.mamba.mapper.RecordMapper;
import org.mamba.mapper.RoomMapper;
import org.mamba.service.RoomService;
import org.mamba.service.MaintenanceService;
import org.mamba.service.RecordService;
import org.mamba.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MaintenanceService maintenanceService;

    /* IMPORTANT NUMBERS - DO NOT MODIFY */
    private final int DAILY_START_HOUR = 8;
    private final int DAILY_END_HOUR = 22;
    private final long PERIOD_MINUTE = 30;

    /**
     * Obtains the room specified by the information given.
     *
     * @param id              the room id
     * @param roomName        the room name
     * @param capacity        the capacity (the query result has to be bigger than or equal to this)
     * @param multimedia      if the room has multimedia facilities or not
     * @param projector       if the room has a projector or not
     * @param requireApproval if the room requires approval from the admin when trying to book or not
     * @param isRestricted    if the room is only available to lecturers or not
     * @param roomType        the type of the room
     * @param start           the start of the desired time period
     * @param end             the end of the desired time period
     * @param size            the size of each page
     * @param page            the page No.
     */
    @Override
    public Map<String, Object> getRooms(Integer id, String roomName, Integer capacity, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, LocalDateTime start, LocalDateTime end, Integer size, Integer page) {
        // Calculate pagination offset
        Integer offset = null;
        if (size != null && page != null) {
            offset = (page - 1) * size;
        }

        // Retrieve the list of rooms that meet the search conditions
        List<Room> conditionRoomList = roomMapper.getRooms(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, roomType, size, offset);

        List<Room> roomList = new ArrayList<>();
        if (start != null && end != null) {
            for (Room conditionRoom : conditionRoomList) {
                Integer conditionRoomId = conditionRoom.getId();
                boolean roomBusy = false;

                // Check if the room is occupied due to existing bookings (time overlap check)
                List<Map<String, Object>> conditionRoomBusyTimeList = getBusyTimesById(conditionRoomId);
                for (Map<String, Object> busyTimeSlots : conditionRoomBusyTimeList) {
                    // Check if each busy time slot occupies the given period
                    if (start.isBefore((LocalDateTime) busyTimeSlots.get("endTime")) && end.isAfter((LocalDateTime) busyTimeSlots.get("startTime"))) {
                        roomBusy = true;
                        break;
                    }
                }

                // Add the room to the list if it is not occupied
                if (!roomBusy) {
                    roomList.add(conditionRoom);
                }
            }
        } else {
            roomList = conditionRoomList;
        }
        if (start == null && end == null) {
            start = LocalDateTime.now();
            for (Room conditionRoom : roomList) {
                Integer conditionRoomId = conditionRoom.getId();
                boolean isUnderMaintenance = false;
                // Check if the room is under maintenance (time overlap check)
                List<Map<String, Object>> maintenanceTimes = getMaintenanceTimesById(conditionRoomId);
                for (Map<String, Object> maintenanceTimeSlots : maintenanceTimes) {
                    if (start.isBefore((LocalDateTime) maintenanceTimeSlots.get("endTime")) && start.isAfter((LocalDateTime) maintenanceTimeSlots.get("startTime"))) {
                        isUnderMaintenance = true;  // The room is under maintenance
                        break;
                    }
                }
                conditionRoom.setMaintenance(isUnderMaintenance);
            }
        } else {
            for (Room conditionRoom : roomList) {
                Integer conditionRoomId = conditionRoom.getId();
                boolean isUnderMaintenance = false;
                // Check if the room is under maintenance (time overlap check)
                List<Map<String, Object>> maintenanceTimes = getMaintenanceTimesById(conditionRoomId);
                for (Map<String, Object> maintenanceTimeSlots : maintenanceTimes) {
                    if (start.isBefore((LocalDateTime) maintenanceTimeSlots.get("endTime")) && end.isAfter((LocalDateTime) maintenanceTimeSlots.get("startTime"))) {
                        isUnderMaintenance = true;  // The room is under maintenance
                        break;
                    }
                }
                conditionRoom.setMaintenance(isUnderMaintenance);
            }
        }

        // Calculate pagination information
        Map<String, Object> map = new HashMap<>();
        int total = roomMapper.count(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, roomType);
        Integer totalPage = (size != null) ? (total % size == 0 ? total / size : total / size + 1) : null;

        map.put("rooms", roomList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
    }

    @Override
    public List<Room> getAllRooms() {
        return roomMapper.getAllRooms();
    }

    /**
     * Insert a new room.
     *
     * @param roomName        the room's name
     * @param capacity        the capacity
     * @param isBusy          if the room is currently (for the time being) busy or not
     * @param location        the location of the room
     * @param multimedia      if the room has multimedia facilities or not
     * @param projector       if the room has a projector or not
     * @param requireApproval if the room requires approval from the admin when trying to book or not
     * @param isRestricted    if the room is only available to lecturers or not
     * @param roomType        the type of the room
     * @param url             the description photo url of the room
     */
    @Override
    public void createRoom(String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, String url, String description, Integer maxBookingDuration) {
        roomMapper.createRoom(roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url, description, maxBookingDuration);

        messageService.createMessage(
                1,
                "New Room Created",
                "A new room named '" + roomName + "' has been successfully created. Details: Capacity: " + capacity + ", Location: " + location,
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                roomMapper.getRoomByName(roomName).getId()
        );
    }

    /**
     * Set the permission of a user for a room.
     *
     * @param roomid the room id
     * @param uid    the user id
     */
    @Override
    public void setPermissionUser(Integer roomid, Integer uid) {
        roomMapper.deletePermissionUsers(roomid);
        roomMapper.createPermissionUser(roomid, uid);
    }

    /**
     * Get the permission of a user for a room.
     *
     * @param roomid the room id
     */
    @Override
    public List<Integer> getPermissionUser(Integer roomid) {
        return roomMapper.getPermissionUser(roomid);
    }


    /**
     * Update the information of a room by id.
     *
     * @param id              the id of the room with information to be updated (used for query)
     * @param roomName        the room's name
     * @param capacity        the capacity
     * @param isBusy          if the room is currently (for the time being) busy or not
     * @param location        the location of the room
     * @param multimedia      if the room has multimedia facilities or not
     * @param projector       if the room has a projector or not
     * @param requireApproval if the room requires approval from the admin when trying to book or not
     * @param isRestricted    if the room is only available to lecturers or not
     * @param roomType        the type of the room
     * @param url             the description photo url of the room
     */
    @Override
    public void updateRoom(Integer id, String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, String url, String description, Integer maxBookingDuration) {
        roomMapper.updateRoom(id, roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url, description, maxBookingDuration);

        messageService.createMessage(
                1,
                "Room Information Updated",
                "The room with ID " + id + " has been successfully updated. New details: Name: " + roomName +
                        ", Capacity: " + capacity + ", Location: " + location + ", Multimedia: " + multimedia +
                        ", Projector: " + projector + ", Requires Approval: " + requireApproval +
                        ", Restricted: " + isRestricted + ", Room Type: " + roomType + ", URL: " + url +
                        ", Description: " + description,
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                id
        );
    }

    /**
     * Update the permission of a room by id.
     *
     * @param id              the id of the room with permission to be updated
     * @param permissionUsers the list of users with permission
     */
    @Override
    public void updateRoomPermission(Integer id, List<Integer> permissionUsers) {
        roomMapper.deletePermissionUsers(id);
        for (Integer uid : permissionUsers) {
            roomMapper.createPermissionUser(id, uid);
        }
    }

    @Override
    public List<Room> getRestrictedRooms() {
        return roomMapper.getRooms(null, null, null, null, null, null, true, null, null, null);
    }

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void deleteRoomById(Integer id) {
        roomMapper.deleteRoomById(id);

        messageService.createMessage(
                1,
                "Room Deleted",
                "The room with ID " + id + " has been successfully deleted.",
                LocalDateTime.now(),
                false,
                "1;Jinhao Zhang",
                0,
                id
        );
    }

    /**
     * Get the BUSY times of the room by id.
     * Returns all the BUSY time periods of the room in the next 7 days.
     * (including the current day)
     * The format: the list of minimal unit of time period.
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    @Override
    public List<Map<String, Object>> getBusyTimesById(Integer id) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay(); // 当天 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay(); // 7 天后 00:00

        // 获取未来 7 天的所有记录
        List<Record> records = roomMapper.getFutureRecords(id, startOfDay, endOfDay);

        List<Map<String, Object>> busyTimes = new ArrayList<>();

        // 将每个预订记录的 id、开始时间和结束时间放入列表
        for (Record record : records) {
            Map<String, Object> recordInfo = new HashMap<>();
            recordInfo.put("uid", record.getUserId());
            recordInfo.put("startTime", record.getStartTime());
            recordInfo.put("endTime", record.getEndTime());

            busyTimes.add(recordInfo);
        }

        return busyTimes;
    }

    @Override
    public List<List<LocalDateTime>> get7DaysAfterTime(Integer roomId){

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();  // 00:00
        LocalDateTime endOfDay = today.plusDays(60).atStartOfDay();  // 7 天后

        // 获取未来 7 天的所有预约记录
        List<Record> records = roomMapper.getFutureRecords(roomId, startOfDay, endOfDay);

        // 使用 TreeMap 记录每天的忙碌时间段，方便按日期查找
        Map<LocalDate, List<List<LocalDateTime>>> busyMap = new TreeMap<>();

        for (Record record : records) {
            LocalDate date = record.getStartTime().toLocalDate();
            busyMap.computeIfAbsent(date, k -> new ArrayList<>())
                    .add(Arrays.asList(record.getStartTime(), record.getEndTime()));
        }

        List<List<LocalDateTime>> freeResult = new ArrayList<>();

        // 遍历未来 7 天
        for (int i = 0; i < 60; i++) {
            LocalDate currentDate = today.plusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.of(DAILY_START_HOUR, 0));
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.of(DAILY_END_HOUR, 0));

            List<List<LocalDateTime>> busySlots = busyMap.getOrDefault(currentDate, new ArrayList<>());

            // 按时间排序，确保 busySlots 是有序的
            busySlots.sort(Comparator.comparing(slot -> slot.get(0)));

            LocalDateTime slotStart = dayStart;

            for (List<LocalDateTime> busySlot : busySlots) {
                LocalDateTime busyStart = busySlot.get(0);
                LocalDateTime busyEnd = busySlot.get(1);

                // 如果当前 slotStart 早于 busyStart，则这个时间段是空闲的
                if (slotStart.isBefore(busyStart)) {
                    while (slotStart.plusMinutes(PERIOD_MINUTE).isBefore(busyStart) || slotStart.plusMinutes(PERIOD_MINUTE).equals(busyStart)) {
                        freeResult.add(Arrays.asList(slotStart, slotStart.plusMinutes(PERIOD_MINUTE)));
                        slotStart = slotStart.plusMinutes(PERIOD_MINUTE);
                    }
                }

                // 更新 slotStart 为当前 busy 结束时间，继续找下一个空闲时间
                if (slotStart.isBefore(busyEnd)) {
                    slotStart = busyEnd;
                }
            }

            // 检查 busySlots 之后是否还有空闲时间
            while (slotStart.plusMinutes(PERIOD_MINUTE).isBefore(dayEnd) || slotStart.plusMinutes(PERIOD_MINUTE).equals(dayEnd)) {
                freeResult.add(Arrays.asList(slotStart, slotStart.plusMinutes(PERIOD_MINUTE)));
                slotStart = slotStart.plusMinutes(PERIOD_MINUTE);
            }
        }

        return freeResult;
    }



    @Override
    public List<Map<String, Object>> getMaintenanceTimesById(Integer id) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay(); // 当天 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay(); // 7 天后 00:00

        // 获取未来 7 天的所有维护记录
        List<Maintenance> maintenances = roomMapper.getFutureMaintenances(id, startOfDay, endOfDay);

        List<Map<String, Object>> maintenanceTimes = new ArrayList<>();

        // 将每个维护记录的开始时间和结束时间放入列表
        for (Maintenance maintenance : maintenances) {
            Map<String, Object> maintenanceInfo = new HashMap<>();
            maintenanceInfo.put("startTime", maintenance.getScheduledStart());
            maintenanceInfo.put("endTime", maintenance.getScheduledEnd());

            maintenanceTimes.add(maintenanceInfo);
        }

        return maintenanceTimes;
    }

    /**
     * Cancel the room by id.
     *
     * @param id the provided id
     */
    @Override
    public void cancelRoomById(Integer id) {
        roomMapper.cancelRoomById(id);
    }

    /**
     * Get all the RECORD PERIODS times of the room by id.
     * Returns all the RECORD PERIODS of the room in the next 7 days.
     * (including the current day)
     * The format: the list of each order's start time & end time
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    @Override
    public List<List<LocalDateTime>> getRecordPeriodsById(Integer id) {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Obtain all the records
        List<Record> roomRecords = recordMapper.getRecords(null, id, null, now, now.plusDays(7), null, null, null, null, null);
        List<List<LocalDateTime>> roomRecordPeriods = new ArrayList<>();

        // Iterate through all the record periods
        for (Record roomRecord : roomRecords) {
            LocalDateTime recordStart = roomRecord.getStartTime();
            LocalDateTime recordEnd = roomRecord.getEndTime();

            List<LocalDateTime> recordPeriod = new ArrayList<>();
            recordPeriod.add(recordStart);
            recordPeriod.add(recordEnd);

            // Add to the result
            roomRecordPeriods.add(recordPeriod);
        }

        return roomRecordPeriods;
    }

    /**
     * Get the FREE times of the room by id.
     * Returns all the FREE time periods of the room in the next 7 days.
     * (including the current day)
     * The format: the list of minimal unit of time period.
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    @Override
    public List<List<LocalDateTime>> getFreeTimesById(Integer id) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();  // 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay();  // 7 天后

        // 获取未来 7 天的所有预约记录
        List<Record> records = roomMapper.getFutureCourses(id, startOfDay, endOfDay);

        // 使用 TreeMap 记录每天的忙碌时间段，方便按日期查找
        Map<LocalDate, List<List<LocalDateTime>>> busyMap = new TreeMap<>();

        for (Record record : records) {
            LocalDate date = record.getStartTime().toLocalDate();
            busyMap.computeIfAbsent(date, k -> new ArrayList<>())
                    .add(Arrays.asList(record.getStartTime(), record.getEndTime()));
        }

        List<List<LocalDateTime>> freeResult = new ArrayList<>();

        // 遍历未来 7 天
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = today.plusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.of(DAILY_START_HOUR, 0));
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.of(DAILY_END_HOUR, 0));

            List<List<LocalDateTime>> busySlots = busyMap.getOrDefault(currentDate, new ArrayList<>());

            // 按时间排序，确保 busySlots 是有序的
            busySlots.sort(Comparator.comparing(slot -> slot.get(0)));

            LocalDateTime slotStart = dayStart;

            for (List<LocalDateTime> busySlot : busySlots) {
                LocalDateTime busyStart = busySlot.get(0);
                LocalDateTime busyEnd = busySlot.get(1);

                // 如果当前 slotStart 早于 busyStart，则这个时间段是空闲的
                if (slotStart.isBefore(busyStart)) {
                    while (slotStart.plusMinutes(PERIOD_MINUTE).isBefore(busyStart) || slotStart.plusMinutes(PERIOD_MINUTE).equals(busyStart)) {
                        freeResult.add(Arrays.asList(slotStart, slotStart.plusMinutes(PERIOD_MINUTE)));
                        slotStart = slotStart.plusMinutes(PERIOD_MINUTE);
                    }
                }

                // 更新 slotStart 为当前 busy 结束时间，继续找下一个空闲时间
                if (slotStart.isBefore(busyEnd)) {
                    slotStart = busyEnd;
                }
            }

            // 检查 busySlots 之后是否还有空闲时间
            while (slotStart.plusMinutes(PERIOD_MINUTE).isBefore(dayEnd) || slotStart.plusMinutes(PERIOD_MINUTE).equals(dayEnd)) {
                freeResult.add(Arrays.asList(slotStart, slotStart.plusMinutes(PERIOD_MINUTE)));
                slotStart = slotStart.plusMinutes(PERIOD_MINUTE);
            }
        }

        return freeResult;
    }


    @Override
    public Room getRoomById(Integer id) {
        return roomMapper.getRoomById(id);
    }

    @Override
    public Room getRoomByName(String roomName) {
        return roomMapper.getRoomByName(roomName);
    }

    @Override
    public int countRooms() {
        return roomMapper.countRooms();
    }

    @Override
    public String findNearAvailableRoom(Integer currentRoomId, LocalDateTime startTime, LocalDateTime endTime, Integer uid) {
        // Get the list of all rooms
        List<Room> allRooms = roomMapper.getAllRooms();

        LocalDateTime startRecord = startTime;

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        // Loop to keep searching for an available room until one is found
        while (true) {
            // Iterate over all rooms
            for (Room room : allRooms) {
                // Skip rooms with insufficient capacity
                if (room.getCapacity() < roomMapper.getRoomById(currentRoomId).getCapacity()) {
                    continue;
                }

                // If the room is restricted and the user is not permitted, skip this room
                if (room.isRestricted() && !roomMapper.getPermissionUser(room.getId()).contains(uid)) {
                    continue;
                }

//                // If the room is the current room, check its availability based on the new unavailable time slot
//                if (room.getId().equals(currentRoomId) &&
//                        !(endTime.isBefore(startRecord) || startTime.isAfter(endRecord))) {
//                    continue;
//                }

                List<Map<String, Object>> maintenance = getMaintenanceTimesById(room.getId());

                // Check if the room is available based on busy times
                boolean flag = true;
                for (Map<String, Object> busyTime : maintenance) {
                    if (startTime.isBefore((LocalDateTime) busyTime.get("endTime")) && endTime.isAfter((LocalDateTime) busyTime.get("startTime"))) {
                        flag = false;
                        break;
                    }
                }

                // Get all busy times for this room
                List<Map<String, Object>> busyTimes = getBusyTimesById(room.getId());
                boolean isAvailable = true;

                // Check if the room is available during the requested time slot
                for (Map<String, Object> busyTime : busyTimes) {
                    if (startTime.isBefore((LocalDateTime) busyTime.get("endTime")) && endTime.isAfter((LocalDateTime) busyTime.get("startTime"))) {
                        isAvailable = false;
                        break;
                    }
                }

                // If the room is available, return it
                if (isAvailable && flag) {
                    return room.getId() + "," + startTime + "," + endTime;
                }
            }

            // If no available room is found, adjust the time slot by adding 30 minutes
            startTime = startTime.plusMinutes(30);
            endTime = endTime.plusMinutes(30);

            if (startTime.isAfter(startRecord.plusDays(7))) {
                return null;
            }

            if (startTime.toLocalTime().isBefore(LocalTime.of(8, 0))) {
                startTime = startTime.toLocalDate().atTime(8, 0);
                endTime = endTime.plusMinutes(durationMinutes);
                continue;
            } else if (endTime.toLocalTime().isAfter(LocalTime.of(22, 0))) {
                startTime = startTime.toLocalDate().plusDays(1).atTime(8, 0);
                endTime = endTime.plusMinutes(durationMinutes);
                continue;
            }
        }
    }

    /**
     * Finds the nearest available room for the given time period, considering the user's role.
     *
     * @param currentRoomId the ID of the current room
     * @param startTime     the start time of the desired period
     * @param endTime       the end time of the desired period
     * @param uid           userid
     * @return the nearest available room that meets the criteria, or null if none found
     */
    @Override
    public String findNearestAvailableRoom(Integer currentRoomId, LocalDateTime startTime, LocalDateTime endTime,
                                           Integer uid) {
        // Get the list of all rooms
        List<Room> allRooms = roomMapper.getAllRooms();

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        LocalDateTime startRecord = startTime;

        // Loop to keep searching for an available room until one is found
        while (true) {

            if (startTime.isAfter(startRecord.plusDays(7))) {
                return null;
            }

            if (startTime.toLocalTime().isBefore(LocalTime.of(8, 0))) {
                startTime = startTime.toLocalDate().atTime(8, 0);
                endTime = startTime.plusMinutes(durationMinutes);
                continue;
            } else if (endTime.toLocalTime().isAfter(LocalTime.of(22, 0))) {
                startTime = startTime.toLocalDate().plusDays(1).atTime(8, 0);
                endTime = startTime.plusMinutes(durationMinutes);
                continue;
            }

            for (Room room : allRooms) {
                // Skip rooms with insufficient capacity
                if (room.getCapacity() < roomMapper.getRoomById(currentRoomId).getCapacity()) {
                    continue;
                }

                // If the room is restricted and the user is not permitted, skip this room
                if (room.isRestricted() && !roomMapper.getPermissionUser(room.getId()).contains(uid)) {
                    continue;
                }

                List<Map<String, Object>> maintenance = getMaintenanceTimesById(room.getId());

                // Check if the room is available based on busy times
                boolean flag = true;
                for (Map<String, Object> busyTime : maintenance) {
                    if (startTime.isBefore((LocalDateTime) busyTime.get("endTime")) && endTime.isAfter((LocalDateTime) busyTime.get("startTime"))) {
                        flag = false;
                        break;
                    }
                }

                // Get all busy times for this room
                List<Map<String, Object>> busyTimes = getBusyTimesById(room.getId());

                // Check if the room is available based on busy times
                boolean isAvailable = true;
                for (Map<String, Object> busyTime : busyTimes) {
                    if (startTime.isBefore((LocalDateTime) busyTime.get("endTime")) && endTime.isAfter((LocalDateTime) busyTime.get("startTime"))) {
                        isAvailable = false;
                        break;
                    }
                }

                if (isAvailable && flag) {
                    return room.getId() + "," + startTime + "," + endTime;  // Return the first available room
                }
            }

            // If no available room is found, adjust the time slot by adding 30 minutes
            startTime = startTime.plusMinutes(PERIOD_MINUTE);
            endTime = endTime.plusMinutes(PERIOD_MINUTE);
        }
    }

    @Override
    public Map<String, Double> calculateRoomUtilization(Integer roomId, Integer rangeType) {
        LocalDateTime startTime;
        long totalMinutes;
        Map<String, Double> utilizationMap = new HashMap<>();

        if (rangeType.equals(1)) {
            startTime = LocalDateTime.now().minusMonths(1);
            totalMinutes = 30 * 24 * 60;
        } else if (rangeType.equals(2)) {
            startTime = LocalDateTime.now().minusMonths(2);
            totalMinutes = 60 * 24 * 60;
        } else if (rangeType.equals(3)) {
            startTime = LocalDateTime.now().minusMonths(3);
            totalMinutes = 90 * 24 * 60;
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }

        List<Record> pastRecords = roomMapper.getRecordsFromStartTime(roomId, startTime);
        double totalBusyMinutes = 0;
        double totalClassTime = 0;
        double totalBookingTime = 0;

        for (Record record : pastRecords) {
            if (record == null || record.getStartTime() == null || record.getEndTime() == null) {
                continue; // Skip invalid data to avoid NullPointerException
            }

            LocalDateTime start = record.getStartTime();
            LocalDateTime end = record.getEndTime();
            double periodTime = Duration.between(start, end).toMinutes();

            totalBusyMinutes += periodTime;
            if (record.getUserId() == 1) {
                totalClassTime += periodTime;
            } else {
                totalBookingTime += periodTime;
            }
        }

        double totalUtilization = (totalBusyMinutes / totalMinutes) * 100;
        double classUtilization = (totalClassTime / totalMinutes) * 100;
        double bookingUtilization = (totalBookingTime / totalMinutes) * 100;

        utilizationMap.put("totalUtilization", Math.round(totalUtilization * 100.0) / 100.0);
        utilizationMap.put("classUtilization", Math.round(classUtilization * 100.0) / 100.0);
        utilizationMap.put("bookingUtilization", Math.round(bookingUtilization * 100.0) / 100.0);

        return utilizationMap;
    }

    /**
     * Generates automated suggestions based on room utilization and cancellation statistics.
     *
     * @param roomReport the room report containing various statistics
     * @return a map containing improvement suggestions
     */
    public Map<String, Object> generateSuggestions(Map<String, Object> roomReport, Integer rangeType) {
        Map<String, Object> suggestions = new LinkedHashMap<>();

        double period;
        if (rangeType.equals(1)) {
            period = 30;
        } else if (rangeType.equals(2)) {
            period = 60;
        } else if (rangeType.equals(3)) {
            period = 90;
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }

        double totalUtilization = ((Number) ((Map<String, Object>) roomReport.get("Utilization")).get("totalUtilization")).doubleValue();
        double cancellationRate = ((Number) roomReport.get("cancellationRate")).doubleValue();
        int maintenanceTime = ((Number) roomReport.get("maintenanceTime")).intValue();
        double maintenanceDuration = ((Number) roomReport.get("maintenanceDuration")).doubleValue();
        int cancelTime = ((Number) roomReport.get("cancelTime")).intValue();
        Map<String, Double> cancellationReasons = (Map<String, Double>) roomReport.get("CancellationReasons");

        if (totalUtilization < 20) {
            suggestions.put("Increase Utilization", "Utilization is low (" + String.format("%.2f", totalUtilization) + "%). Optimize scheduling to improve usage.");
        } else if (totalUtilization > 60 && totalUtilization <= 80) {
            suggestions.put("Optimize Scheduling", "Utilization is high (" + String.format("%.2f", totalUtilization) + "%). Check for overuse and optimize scheduling.");
        } else if (totalUtilization > 80) {
            suggestions.put("Consider Expansion", "Utilization is very high (" + String.format("%.2f", totalUtilization) + "%). Consider expanding facilities or adding backup options.");
        } else {
            suggestions.put("Utilization in Moderate Range", "Utilization is within a moderate range (" + String.format("%.2f", totalUtilization) + "%). Keep ensuring balance between availability and usage.");
        }

        if (cancellationRate > 20) {
            Map<String, String> cancellationSuggestions = new LinkedHashMap<>();
            cancellationSuggestions.put("Summary", "Cancellation rate is high (" + String.format("%.2f", cancellationRate) + "%) and the cancel times is " + cancelTime + ". Please check the room facilities to ensure the quality of the room.");

            double maxCancellationPercentage = 0.0;
            for (Map.Entry<String, Double> entry : cancellationReasons.entrySet()) {
                double currentPercentage = entry.getValue();
                if (currentPercentage > maxCancellationPercentage) {
                    maxCancellationPercentage = currentPercentage;
                }
            }

            // Suggest based on cancellation reasons with 5% tolerance
            for (Map.Entry<String, Double> entry : cancellationReasons.entrySet()) {
                double currentPercentage = entry.getValue();
                if (Math.abs(currentPercentage - maxCancellationPercentage) <= 5) {
                    switch (entry.getKey()) {
                        case "Schedule Change":
                            cancellationSuggestions.put("Schedule Change", "The cancellation reason 'Schedule Change' is significant (" + String.format("%.2f", currentPercentage) + "%). Consider offering more flexible booking options or better communication with users.");
                            break;
                        case "Equipment Failure":
                            cancellationSuggestions.put("Equipment Failure", "The cancellation reason 'Equipment Failure' is significant (" + String.format("%.2f", currentPercentage) + "%). Ensure better maintenance and timely equipment checks to prevent failures.");
                            break;
                        case "Mistake":
                            cancellationSuggestions.put("User Mistake ", "The cancellation reason 'Mistake' is significant (" + String.format("%.2f", currentPercentage) + "%). Improve the booking confirmation process to avoid user errors.");
                            break;
                        case "Emergency":
                            cancellationSuggestions.put("Emergency", "The cancellation reason 'Emergency' is significant (" + String.format("%.2f", currentPercentage) + "%). Consider building a more flexible emergency response system for last-minute cancellations.");
                            break;
                        case "Other":
                            cancellationSuggestions.put("Other", "The cancellation reason 'Other' is significant (" + String.format("%.2f", currentPercentage) + "%). Review 'Other' cancellations to identify patterns and take proactive measures.");
                            break;
                        default:
                            break;
                    }
                }
            }

            suggestions.put("Reduce Cancellations", cancellationSuggestions);
        }

        if (maintenanceTime / period > 0.2) {
            suggestions.put("Reduce Maintenance Time", "Maintenance time is " + maintenanceTime +
                    " and the total maintenance times is " + String.format("%.2f", maintenanceDuration) +
                    " hours during the " + (int) (period / 30) + " months. Check room facilities in detail to ensure room availability.");
        }

        return suggestions;
    }


}
