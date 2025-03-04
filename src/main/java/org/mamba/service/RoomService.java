package org.mamba.service;

import org.mamba.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {
    /**
     * Obtains the room specified by ID given.
     *
     * @param id              the provided id
     * @param roomName        the room name
     * @param capacity        the capacity (the query result has to be bigger than or equal to this)
     * @param multimedia      if the room has multimedia facilities or not
     * @param projector       if the room has a projector or not
     * @param requireApproval if the room requires approval from the admin when trying to book or not
     * @param isRestricted    if the room is only available to lecturers or not
     * @param pageSize        the size of each page
     * @param offset          the offset
     * @return the list of all the rooms satisfying the condition(s)
     */
    List<Room> getRooms(Integer id, String roomName, Integer capacity, boolean multimedia, boolean projector, boolean requireApproval, boolean isRestricted, int pageSize, int offset);

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
     * @param url             the description photo url of the room
     */
    void createRoom(String roomName, Integer capacity, boolean isBusy, String location, boolean multimedia, boolean projector, boolean requireApproval, boolean isRestricted, String url);

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
     * @param url             the description photo url of the room
     */
    void updateRoomById(Integer id, String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, String url);

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     */
    void deleteRoomById(Integer id);

    /**
     * Get the BUSY times of the room by id.
     * Returns all the BUSY time periods of the room in the next 7 days.
     * (including the current day)
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    List<List<LocalDateTime>> getBusyTimesById(Integer id);

    /**
     * Get the FREE times of the room by id.
     * Returns all the FREE time periods of the room in the next 7 days.
     * (including the current day)
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    List<List<LocalDateTime>> getFreeTimesById(Integer id);

}
