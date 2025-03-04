package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.Room;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms") // TODO 确认
public class RoomController {
    @Autowired
    private RoomService roomService;

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
    // TODO 注脚，参数传递
    public Result getRooms(Integer id, String roomName, Integer capacity, boolean multimedia, boolean projector, boolean requireApproval, boolean isRestricted, int pageSize, int offset) {
        List<Room> rooms = roomService.getRooms(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, pageSize, offset);
        return Result.success(rooms);
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
     * @param url             the description photo url of the room
     */
    // TODO 注脚，参数传递
    public Result createRoom(String roomName, Integer capacity, boolean isBusy, String location, boolean multimedia, boolean projector, boolean requireApproval, boolean isRestricted, String url) {
        roomService.createRoom(roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, url);
        return Result.success();
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
     * @param url             the description photo url of the room
     */
    // TODO 注脚，参数传递
    public Result updateRoomById(Integer id, String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, String url) {
        roomService.updateRoomById(id, roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, url);
        return Result.success();
    }

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     */
    // TODO 注脚，参数传递
    public Result deleteRoomById(Integer id) {
        roomService.deleteRoomById(id);
        return Result.success();
    }

}
