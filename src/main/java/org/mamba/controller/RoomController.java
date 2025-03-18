package org.mamba.controller;

import org.mamba.entity.Result;
import org.mamba.entity.Room;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    /**
     * Obtains the room list based on the conditions given.
     *
     * @param id              the provided id
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
     * @return all the rooms satisfying the condition(s)
     */
    @RequestMapping("/getRooms")
    public Result getRooms(@RequestParam(required = false) Integer id,
                           @RequestParam(required = false) String roomName,
                           @RequestParam(required = false) Integer capacity,
                           @RequestParam(required = false) Boolean multimedia,
                           @RequestParam(required = false) Boolean projector,
                           @RequestParam(required = false) Boolean requireApproval,
                           @RequestParam(required = false) Boolean isRestricted,
                           @RequestParam(required = false) Integer roomType,
                           @RequestParam(required = false) LocalDateTime start,
                           @RequestParam(required = false) LocalDateTime end,
                           @RequestParam(required = false) Integer size,
                           @RequestParam(required = false) Integer page) {
        Map<String, Object> roomsResult = roomService.getRooms(id, roomName, capacity, multimedia, projector, requireApproval, isRestricted, roomType, start, end, size, page);
        return Result.success(roomsResult);
    }

    /**
     * Insert a new room.
     *
     * @param room the room to be created
     * @return the result of the creation operation
     */
    @PostMapping("/addRoom")
    public Result createRoom(@RequestBody Room room) {
        roomService.createRoom(room.getRoomName(), room.getCapacity(), room.isBusy(), room.getLocation(), room.isMultimedia(), room.isProjector(), room.isRequireApproval(), room.isRestricted(), room.getRoomType(), room.getUrl(), room.getDescription(), room.getMaxBookingDuration());
        return Result.success();
    }

    /**
     * Update the information of a room by id.
     *
     * @param id   the id of the room with information to be updated (used for query)
     * @param room the room with updated information
     * @return the result of the update operation
     */
    @PutMapping("/{id}")
    public Result updateRoomById(@PathVariable Integer id, @RequestBody Room room) {
        roomService.updateRoom(id, room.getRoomName(), room.getCapacity(), room.isBusy(), room.getLocation(), room.isMultimedia(), room.isProjector(), room.isRequireApproval(), room.isRestricted(), room.getRoomType(), room.getUrl(), room.getDescription(), room.getMaxBookingDuration());
        return Result.success();
    }

    /**
     * Set the permission of the user in the room.
     *
     * @return the result of the operation
     */
    @PostMapping("/setPermissionUser")
    public Result setPermissionUser(@RequestBody Map<String,Object> request) {
        Integer room_id = Integer.parseInt(request.get("room_id").toString());
        List<Integer> uids = (List<Integer>) request.get("uids");
        uids.forEach(uid->roomService.setPermissionUser(room_id, uid));
        return Result.success();
    }

    /**
     * Get the permission of the user in the room.
     *
     * @param room_id the room id
     * @return the result of the operation
     */
    @GetMapping("/getPermissionUser")
    public Result getPermissionUser(@RequestParam Integer room_id) {

        return Result.success(roomService.getPermissionUser(room_id));
    }

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     * @return the result of the deletion operation
     */
    @DeleteMapping("/{id}")
    public Result deleteRoomById(@PathVariable Integer id) {
        roomService.deleteRoomById(id);
        return Result.success();
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
    @GetMapping("/getBusyTime")
    public Result getBusyTimesById(Integer id) {
        List<List<LocalDateTime>> busyTimesList = roomService.getBusyTimesById(id);
        return Result.success(busyTimesList);
    }

    /**
     * Get the maintenance times of the room by id.
     * Returns all the maintenance time periods of the room in the next 7 days.
     * (including the current day)
     * The format: the list of minimal unit of time period.
     *
     * @param id the room id
     * @return the list containing several lists, each of which contains start time and end time
     */
    @GetMapping("/getMaintenance")
    public Result getMaintenanceById(Integer id) {
        List<List<LocalDateTime>> maintenanceTimes = roomService.getMaintenanceTimesById(id);
        return Result.success(maintenanceTimes);
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
    @GetMapping("/getRecordPeriods/{id}")
    public Result getRecordPeriodsById (Integer id) {
        List<List<LocalDateTime>> roomRecordPeriods = roomService.getRecordPeriodsById(id);
        return Result.success(roomRecordPeriods);
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
    @GetMapping("/getFreeTime/{id}")
    public Result getFreeTimesById(Integer id) {
        List<List<LocalDateTime>> freeTimesList = roomService.getFreeTimesById(id);
        return Result.success(freeTimesList);
    }

    /**
     * Counts the total number of rooms.
     *
     * @return the total number of rooms
     */
    @GetMapping("/count")
    public Result countRooms() {
        int count = roomService.countRooms();
        return Result.success(count);
    }

}