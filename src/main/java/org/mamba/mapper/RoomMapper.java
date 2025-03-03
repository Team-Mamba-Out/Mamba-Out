package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.mamba.entity.Record;
import org.mamba.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RoomMapper {
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
    @Select({
            "<script>",
            "select * from mamba.room where 1=1",
            "<if test='id != null'>",
            "and id = #{id}",
            "</if>",
            "<if test='roomName != null and roomName != \"\"'>",
            "and roomName like CONCAT('%', #{roomName}, '%')",
            "</if>",
            "<if test='capacity != null'>",
            "and capacity >= #{capacity}",
            "</if>",
            "<if test='multimedia != null'>",
            "and multimedia = #{multimedia}",
            "</if>",
            "<if test='projector != null'>",
            "and projector = #{projector}",
            "</if>",
            "<if test='requireApproval != null'>",
            "and requireApproval = #{requireApproval}",
            "</if>",
            "<if test='isRestricted != null'>",
            "and isRestricted = #{isRestricted}",
            "</if>",
            "order by recordTime desc",
            "<if test='pageSize != null'>",
            "limit #{pageSize}",
            "</if>",
            "<if test='offset != null'>",
            "offset #{offset}",
            "</if>",
            "</script>"
    })
    List<Room> getRooms(Integer id, String roomName, Integer capacity, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, Integer pageSize, Integer offset);

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
    @Insert("insert into mamba.room(roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, url) values (#{roomName}, #{capacity}, #{isBusy}, #{location}, #{multimedia}, #{projector}, #{requireApproval}, #{isRestricted}, #{url})")
    void createRoom(String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, String url);

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
    @Update({
            "<script>",
            "update mamba.room",
            "<set>",
            "<if test='roomName != null and roomName != \"\"'> roomName = #{roomName}, </if>",
            "<if test='capacity != null'> capacity = #{capacity}, </if>",
            "<if test='isBusy != null'> isBusy = #{isBusy}, </if>",
            "<if test='location != null and location != \"\"'> location = #{location}, </if>",
            "<if test='multimedia != null'> multimedia = #{multimedia}, </if>",
            "<if test='projector != null'> projector = #{projector}, </if>",
            "<if test='requireApproval != null'> requireApproval = #{requireApproval}, </if>",
            "<if test='isRestricted != null'> isRestricted = #{isRestricted}, </if>",
            "<if test='url != null and url != \"\"'> url = #{url}, </if>",
            "</set>",
            "where id = #{id}",
            "</script>"
    })
    void updateRoomById(Integer id, String roomName, Integer capacity, Boolean isBusy, String location, Boolean multimedia, Boolean projector, Boolean requireApproval, Boolean isRestricted, String url);

    /**
     * Deletes the room specified by id.
     *
     * @param id the provided id
     */
    @Delete("delete from mamba.room where id = #{id}")
    void deleteRoomById(Integer id);

}
