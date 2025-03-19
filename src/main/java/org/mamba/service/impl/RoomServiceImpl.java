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
                List<List<LocalDateTime>> maintenanceTimes = getMaintenanceTimesById(conditionRoomId);
                for (List<LocalDateTime> maintenanceTimeSlots : maintenanceTimes) {
                    if (start.isBefore(maintenanceTimeSlots.get(1)) && start.isAfter(maintenanceTimeSlots.get(0))) {
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
                List<List<LocalDateTime>> maintenanceTimes = getMaintenanceTimesById(conditionRoomId);
                for (List<LocalDateTime> maintenanceTimeSlots : maintenanceTimes) {
                    if (start.isBefore(maintenanceTimeSlots.get(1)) && end.isAfter(maintenanceTimeSlots.get(0))) {
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
                "1;JinhaoZhang",
                roomMapper.getRoomByName(roomName).getId(),
                3
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
                "1;JinhaoZhang",
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
                "1;JinhaoZhang",
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
    public List<List<LocalDateTime>> getMaintenanceTimesById(Integer id) {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay(); // to 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay(); // 7 days later

        // Get all the maintenance records of the next 7 days
        Map<String, Object> maintenanceResult = maintenanceService.getMaintenance(null, id, startOfDay, endOfDay, 10, 1);
        List<Maintenance> maintenances = (List<Maintenance>) maintenanceResult.get("maintenanceList");

        List<List<LocalDateTime>> maintenanceTimes = new ArrayList<>();

        // Put all the maintenance time periods into the maintenance times list (each contains start/end time)
        for (Maintenance maintenance : maintenances) {
            maintenanceTimes.add(Arrays.asList(maintenance.getScheduledStart(), maintenance.getScheduledEnd()));
        }
        return maintenanceTimes;
    }
    /**
     * Cancel the room by id.
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
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay(); // to 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay(); // 7 days later

        // Get all the records of the next 7 days
        List<Record> records = roomMapper.getFutureRecords(id, startOfDay, endOfDay);
        // Get busy times first
        List<List<LocalDateTime>> busyTimes = new ArrayList<>();

        // Put all the record time periods into the busy times list (each contains start/end time)
        for (Record record : records) {
            busyTimes.add(Arrays.asList(record.getStartTime(), record.getEndTime()));
        }

        Map<LocalDate, Set<LocalDateTime>> busyMap = new HashMap<>();
        for (List<LocalDateTime> busyTimeSlot : busyTimes) {
            if (busyTimeSlot.size() != 2) continue;
            LocalDateTime start = busyTimeSlot.get(0);
            LocalDate date = start.toLocalDate();
            busyMap.computeIfAbsent(date, k -> new HashSet<>()).add(start);
        }

        List<List<LocalDateTime>> freeResult = new ArrayList<>();

        // 7 consecutive days
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = now.toLocalDate().plusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.of(DAILY_START_HOUR, 0));
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.of(DAILY_END_HOUR, 0));

            for (LocalDateTime slotStart = dayStart; !slotStart.plusMinutes(PERIOD_MINUTE).isAfter(dayEnd); slotStart = slotStart.plusMinutes(PERIOD_MINUTE)) {
                if (!busyMap.getOrDefault(currentDate, Collections.emptySet()).contains(slotStart)) {
                    List<LocalDateTime> freeSlot = new ArrayList<>();
                    freeSlot.add(slotStart);
                    freeSlot.add(slotStart.plusMinutes(PERIOD_MINUTE));
                    freeResult.add(freeSlot);
                }
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
                if (isAvailable) {
                    return room.getId() + "," + startTime + "," + endTime;
                }
            }

            // If no available room is found, adjust the time slot by adding 30 minutes
            startTime = startTime.plusMinutes(30);
            endTime = endTime.plusMinutes(30);

            if (startTime.isAfter(startRecord.plusDays(7))) {
                return null;
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
    public Room findNearestAvailableRoom(Integer currentRoomId, LocalDateTime startTime, LocalDateTime endTime,
                                         Integer uid) {
        // Get the list of all rooms
        List<Room> allRooms = roomMapper.getAllRooms();

        LocalDateTime startRecord = startTime;

        // Sort rooms by capacity in ascending order to find the closest match
        allRooms.sort(Comparator.comparingInt(Room::getCapacity));

        // Loop to keep searching for an available room until one is found
        while (true) {

            if (startTime.isAfter(startRecord.plusDays(7))) {
                return null;
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

                // Check if the room is under maintenance during the requested time slot
                Map<String, Object> maintenanceResult = maintenanceService.getMaintenance(null, room.getId(), startTime, endTime, 10, 1);
                List<Maintenance> maintenanceList = (List<Maintenance>) maintenanceResult.get("maintenanceList");
                // Check if there is any maintenance that overlaps with the requested time period
                boolean isUnderMaintenance = false;
                for (Maintenance maintenance : maintenanceList) {
                    // If the maintenance start time is before the requested end time,
                    // and the maintenance end time is after the requested start time, there is an overlap
                    if (maintenance.getScheduledStart().isBefore(endTime) && maintenance.getScheduledEnd().isAfter(startTime)) {
                        isUnderMaintenance = true;
                        break;  // If there's an overlap, mark the room as under maintenance
                    }
                }
                // Skip the room if it is under maintenance during the requested time period
                if (isUnderMaintenance) {
                    continue;
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

                if (isAvailable) {
                    return room;  // Return the first available room
                }
            }

            // If no available room is found, adjust the time slot by adding 30 minutes
            startTime = startTime.plusMinutes(PERIOD_MINUTE);
            endTime = endTime.plusMinutes(PERIOD_MINUTE);
        }
    }

    @Override
    public Map<String, Double> calculateRoomUtilization() {
        List<Room> rooms = roomMapper.getAllRooms();
        Map<String, Double> utilizationMap = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime endOfDay = today.atStartOfDay(); // today
        LocalDateTime startOfDay = today.minusDays(7).atStartOfDay(); // 7 days before

        for (Room room : rooms) {
            List<Record> pastRecords = roomMapper.getPastRecords(room.getId(), startOfDay, endOfDay);
            double totalBusyMinutes = 0;

            for (Record record : pastRecords) {
                if (record == null || record.getStartTime() == null || record.getEndTime() == null) {
                    continue; // Skip invalid data to avoid NullPointerException
                }
                LocalDateTime start = record.getStartTime();
                LocalDateTime end = record.getEndTime();
                totalBusyMinutes += Duration.between(start, end).toMinutes();
            }

            double totalMinutesInWeek = 7 * 24 * 60;
            double utilization = (totalBusyMinutes / totalMinutesInWeek) * 100;
            utilizationMap.put(room.getRoomName(), utilization);
        }

        return utilizationMap;
    }

}
