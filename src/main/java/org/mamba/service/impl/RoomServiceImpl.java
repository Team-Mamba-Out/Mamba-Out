package org.mamba.service.impl;

import org.mamba.entity.Record;
import org.mamba.entity.Room;
import org.mamba.mapper.RecordMapper;
import org.mamba.mapper.RoomMapper;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // Calculate offset
        Integer offset = (page - 1) * size;

        // Get the list of rooms that satisfy other search conditions first
        List<Room> conditionRoomList = roomMapper.getRooms(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, roomType, size, offset);

        List<Room> roomList = new ArrayList<>();

        if (start != null && end != null) {
            // This search has time limit conditions
            boolean roomBusy = false;

            // Check for the busy times of each room.
            // If a busy period exists inside the provided time period (start/end),
            // Then this room cannot be considered available.
            for (Room conditionRoom : conditionRoomList) {
                roomBusy = false;
                Integer conditionRoomId = conditionRoom.getId();
                List<List<LocalDateTime>> conditionRoomBusyTimeList = getBusyTimesById(conditionRoomId);
                for (List<LocalDateTime> busyTimeSlots : conditionRoomBusyTimeList) {
                    // Check if each busy time slot occupies the given period
                    if (!busyTimeSlots.get(0).isBefore(start) && !busyTimeSlots.get(1).isAfter(end)) {
                        roomBusy = true;
                        break;
                    }
                }
                // If this room is available
                if (!roomBusy) {
                    roomList.add(conditionRoom);
                }
            }
        } else {
            // This search has no time limit conditions, obtain the direct result
            roomList = conditionRoomList;
        }

        Map<String, Object> map = new HashMap<>();
        int total = roomList.size();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
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
    public void createRoom(String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, String url) {
        roomMapper.createRoom(roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url);
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
    public void updateRoomById(Integer id, String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, String url) {
        roomMapper.updateRoomById(id, roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url);
    }

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     */
    @Override
    public void deleteRoomById(Integer id) {
        roomMapper.deleteRoomById(id);
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
    public List<List<LocalDateTime>> getBusyTimesById(Integer id) {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();
        // Get all the records of the next 7 days
        List<Record> records = roomMapper.getFutureRecords(id, now);

        List<List<LocalDateTime>> busyTimes = new ArrayList<>();

        // Put all the record time periods into the busy times list (each contains start/end time)
        for (Record record : records) {
            busyTimes.add(Arrays.asList(record.getStartTime(), record.getEndTime()));
        }

        return busyTimes;
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
    public List<List<LocalDateTime>> getRecordPeriodsById (Integer id) {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Obtain all the records
        List<Record> roomRecords = recordMapper.getRecords(null, id, null, now, now.plusDays(7), null, null, null, null);
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
        // Get all the records of the next 7 days
        List<Record> records = roomMapper.getFutureRecords(id, now);
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

    /**
     * Finds the nearest available room for the given time period, considering the user's role.
     *
     * @param currentRoomId the ID of the current room
     * @param startTime the start time of the desired period
     * @param endTime the end time of the desired period
     * @param userRole the role of the user (e.g., "Student", "Lecturer")
     * @return the nearest available room that meets the criteria, or null if none found
     */
    @Override
    public Room findNearestAvailableRoom(Integer currentRoomId, LocalDateTime startTime, LocalDateTime endTime, String userRole) {
        // Get the list of all rooms
        List<Room> allRooms = roomMapper.getAllRooms();

        // Loop to keep searching for an available room until one is found
        while (true) {
            // Iterate over all rooms
            for (Room room : allRooms) {
                // Skip the current room or rooms with insufficient capacity
                if (room.getId().equals(currentRoomId) || room.getCapacity() < roomMapper.getRoomById(currentRoomId).getCapacity()) {
                    continue; // Skip this room
                }

                // If the room is restricted and the user is a student, skip this room
                if (room.isRestricted() && userRole.equals("Student")) {
                    continue; // Student cannot book restricted rooms
                }

                // Get all busy times for this room
                List<List<LocalDateTime>> busyTimes = getBusyTimesById(room.getId());
                boolean isAvailable = true;

                // Check if the room is available during the requested time slot
                for (List<LocalDateTime> busyTime : busyTimes) {
                    if (startTime.isBefore(busyTime.get(1)) && endTime.isAfter(busyTime.get(0))) {
                        isAvailable = false;
                        break;
                    }
                }

                // If the room is available, return it
                if (isAvailable) {
                    return room;
                }
            }

            // If no available room is found, adjust the time slot by adding 30 minutes
            startTime = startTime.plusMinutes(PERIOD_MINUTE);
            endTime = endTime.plusMinutes(PERIOD_MINUTE);
        }
    }
}
