package org.mamba.service.impl;

import org.mamba.entity.Record;
import org.mamba.entity.Room;
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
     * @param size            the size of each page
     * @param page            the page No.
     */
    @Override
    public Map<String, Object> getRooms(Integer id, String roomName, Integer capacity, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, Integer size, Integer page) {
        // Calculate offset
        Integer offset = (page - 1) * size;
        List<Room> roomList = roomMapper.getRooms(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, roomType, size, offset);
        Map<String, Object> map = new HashMap<>();
        int total = roomList.size();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("rooms", roomList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
    }

    /**
     * Obtains the all the available rooms in a time period
     * or obtains some available rooms based on the conditions given.
     *
     * @param start           (MUST PROVIDE) the start of the time period
     * @param end             (MUST PROVIDE) the end of the time period
     * @param id              the room id
     * @param roomName        the room name
     * @param capacity        the capacity (the query result has to be bigger than or equal to this)
     * @param multimedia      if the room has multimedia facilities or not
     * @param projector       if the room has a projector or not
     * @param requireApproval if the room requires approval from the admin when trying to book or not
     * @param isRestricted    if the room is only available to lecturers or not
     * @param roomType        the type of the room
     * @param size            the size of each page
     * @param page            the page No.
     */
    @Override
    public Map<String, Object> getFreeRooms(LocalDateTime start, LocalDateTime end, Integer id, String roomName, Integer capacity, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, Integer size, Integer page) {
        // Calculate offset
        Integer offset = (page - 1) * size;

        // Get the list of rooms that satisfy other search conditions first
        List<Room> conditionRoomList = roomMapper.getRooms(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, roomType, size, offset);

        List<Room> freeRoomList = new ArrayList<>();
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
                freeRoomList.add(conditionRoom);
            }
        }

        Map<String, Object> map = new HashMap<>();
        int total = freeRoomList.size();
        int totalPage = total % size == 0 ? total / size : total / size + 1;
        map.put("freeRooms", freeRoomList);
        map.put("totalPage", totalPage);
        map.put("total", total);
        map.put("pageNumber", page);
        return map;
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
     * Get the FREE times of the room by id.
     * Returns all the FREE time periods of the room in the next 7 days.
     * (including the current day)
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
}
