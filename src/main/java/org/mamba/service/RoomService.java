package org.mamba.service;

import org.apache.ibatis.annotations.Select;
import org.mamba.entity.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RoomService {
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
    Map<String, Object> getRooms(Integer id, String roomName, Integer capacity, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, LocalDateTime start, LocalDateTime end, Integer size, Integer page);

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
     * @param url             the description photo URL of the room
     * @param description     the description of the room
     * @param maxBookingDuration the maximum booking duration for the room in hours
     */
    void createRoom(String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, String url, String description, Integer maxBookingDuration);

    /**
     * Update the permission of a room by id.
     *
     * @param id              the id of the room with permission to be updated
     * @param permissionUsers the list of users with permission
     */
    void updateRoomPermission(Integer id, List<Integer> permissionUsers);
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
     * @param url             the description photo URL of the room
     * @param description     the description of the room
     * @param maxBookingDuration the maximum booking duration for the room in hours
     */
    void updateRoom(Integer id, String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer roomType, String url, String description, Integer maxBookingDuration);

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     */
    void deleteRoomById(Integer id);

    /**
     * Get the RESTRICTED rooms.
     *
     * @return the list of restricted rooms
     */
    List<Room> getRestrictedRooms();



    /**
     * Get the BUSY times of the room by id.
     * Returns all the BUSY time periods of the room in the next 7 days.
     * (including the current day)
     * The format: the list of minimal unit of time period.
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    List<List<LocalDateTime>> getBusyTimesById(Integer id);

    /**
     * Get all the RECORD PERIODS times of the room by id.
     * Returns all the RECORD PERIODS of the room in the next 7 days.
     * (including the current day)
     * The format: the list of each order's start time & end time
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    List<List<LocalDateTime>> getRecordPeriodsById(Integer id);

    /**
     * Get the FREE times of the room by id.
     * Returns all the FREE time periods of the room in the next 7 days.
     * (including the current day)
     * The format: the list of minimal unit of time period.
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    List<List<LocalDateTime>> getFreeTimesById(Integer id);

    /**
     * Finds the nearest available room for the given time period.
     *
     * @param startTime the start time of the desired period
     * @param endTime   the end time of the desired period
     * @return the nearest available room, or null if none found
     */
    Room findNearestAvailableRoom(Integer currentRoomId, LocalDateTime startTime, LocalDateTime endTime, Integer uid, LocalDateTime newStartTime, LocalDateTime newEndTime);

    /**
     * Set the permission of a user in a room.
     *
     * @param roomId         the room id
     * @param userId         the user id
     */
    void setPermissionUser(Integer roomId, Integer userId);

    /**
     * Get the permission of the user in the room.
     *
     * @param roomId the room id
     * @return the result of the operation
     */
    List<Integer> getPermissionUser(Integer roomId);

    /**
     * Counts the total number of rooms.
     *
     * @return the total number of rooms
     */
    int countRooms();

    /**
     * Retrieves all rooms.
     *
     * @return a list of all rooms
     */
    List<Room> getAllRooms();

    Room getRoomById(Integer id);

    /**
     * Queries a room based on its name.
     *
     * @param roomName the name of the room
     * @return the room matching the name
     */
    Room getRoomByName(String roomName);

    /**
     * Calculate room utilization
     *
     * @return Contains a map of room utilization
     */
    Map<String, Double> calculateRoomUtilization();
}
