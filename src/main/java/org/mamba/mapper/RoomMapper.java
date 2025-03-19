package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Record;
import org.mamba.entity.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface RoomMapper {

    /**
     * Obtains the room list based on the given conditions.
     */
    @SelectProvider(type = RoomSqlBuilder.class, method = "buildGetRoomsSql")
    List<Room> getRooms(@Param("id") Integer id,
                        @Param("roomName") String roomName,
                        @Param("capacity") Integer capacity,
                        @Param("multimedia") Boolean multimedia,
                        @Param("projector") Boolean projector,
                        @Param("requireApproval") Boolean requireApproval,
                        @Param("isRestricted") Boolean isRestricted,
                        @Param("roomType") Integer roomType,
                        @Param("pageSize") Integer pageSize,
                        @Param("offset") Integer offset);

    /**
     * Insert a new room.
     */
    @Insert("INSERT INTO mamba.room (roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, roomType, url, description, maxBookingDuration) " +
            "VALUES (#{roomName}, #{capacity}, #{isBusy}, #{location}, #{multimedia}, #{projector}, #{requireApproval}, #{isRestricted}, #{roomType}, #{url}, #{description}, #{maxBookingDuration})")
    void createRoom(@Param("roomName") String roomName,
                    @Param("capacity") Integer capacity,
                    @Param("isBusy") Boolean isBusy,
                    @Param("location") String location,
                    @Param("multimedia") Boolean multimedia,
                    @Param("projector") Boolean projector,
                    @Param("requireApproval") Boolean requireApproval,
                    @Param("isRestricted") Boolean isRestricted,
                    @Param("roomType") Integer roomType,
                    @Param("url") String url,
                    @Param("description") String description,
                    @Param("maxBookingDuration") Integer maxBookingDuration);

    @Update("UPDATE mamba.room SET isCanceled = 1 WHERE id = #{id}")
    void cancelRoomById(@Param("id") Integer id);


    /**
     * Obtains the room specified by ID.
     */
    @Select("SELECT * FROM mamba.room WHERE id = #{id}")
    Room getRoomById(@Param("id") int id);

    /**
     * Deletes the permission of the room specified by ID.
     *
     * @param room_id
     */
    @Delete("DELETE FROM mamba.room_user WHERE room_id = #{room_id}")
    void deletePermissionUsers(@Param("room_id") int room_id);

    /**
     * Obtains the room specified by ID.
     */
    @Insert("INSERT INTO mamba.room_user(room_id, uid) VALUES(#{room_id}, #{uid})")
    void createPermissionUser(@Param("room_id") int room_id,
                              @Param("uid") int uid);

    /**
     * @param room_id
     */
    @Select("SELECT uid FROM mamba.room_user WHERE room_id = #{room_id}")
    List<Integer> getPermissionUser(@Param("room_id") int room_id);


    /**
     * Update room information by ID.
     */
    @UpdateProvider(type = RoomSqlBuilder.class, method = "buildUpdateRoomSql")
    void updateRoom(@Param("id") Integer id,
                    @Param("roomName") String roomName,
                    @Param("capacity") Integer capacity,
                    @Param("isBusy") Boolean isBusy,
                    @Param("location") String location,
                    @Param("multimedia") Boolean multimedia,
                    @Param("projector") Boolean projector,
                    @Param("requireApproval") Boolean requireApproval,
                    @Param("isRestricted") Boolean isRestricted,
                    @Param("roomType") Integer roomType,
                    @Param("url") String url,
                    @Param("description") String description,
                    @Param("maxBookingDuration") Integer maxBookingDuration);

    /**
     * Deletes the room specified by ID.
     */
    @Delete("DELETE FROM mamba.room WHERE id = #{id}")
    void deleteRoomById(@Param("id") Integer id);

    /**
     * Gets all the records related to a room in the next 7 days.
     *
     * @param id         the id of the room to be checked
     * @param startOfDay the current day at 0am
     * @param endOfDay   7 days later at 0am
     * @return the list containing all the records in 7 days
     */
    @Select("SELECT * " +
            "FROM mamba.record r " +
            "WHERE r.roomId = #{id} " +
            "AND r.startTime >= #{startOfDay} " +
            "AND r.startTime <= #{endOfDay} " +
            "AND r.statusId != 4 " +
            "ORDER BY r.startTime")
    List<Record> getFutureRecords(@Param("id") Integer id, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Retrieves all records related to a specific room in the past 7 days.
     *
     * @param id         the ID of the room to be checked
     * @param startOfDay 7 days before at 0am
     * @param endOfDay   today at 0am
     * @return a list containing all records from the past 7 days
     */
    @Select("SELECT * " +
            "FROM mamba.record r " +
            "WHERE r.roomId = #{id} " +
            "AND r.startTime >= DATE_SUB(DATE(#{now}), INTERVAL 7 DAY) " +  // Past 7 days
            "AND r.startTime <= DATE(#{now}) " +  // Up to the current time
            "ORDER BY r.startTime")
    List<Record> getPastRecords(@Param("id") Integer id, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    /**
     * Retrieves all rooms.
     *
     * @return a list of all rooms
     */
    @Select("SELECT * FROM mamba.room")
    List<Room> getAllRooms();

    /**
     * counts the total number of rooms
     */
    @SelectProvider(type = RoomSqlBuilder.class, method = "buildCountRoomsSql")
    Integer count(@Param("id") Integer id,
                  @Param("roomName") String roomName,
                  @Param("capacity") Integer capacity,
                  @Param("multimedia") Boolean multimedia,
                  @Param("projector") Boolean projector,
                  @Param("requireApproval") Boolean requireApproval,
                  @Param("isRestricted") Boolean isRestricted,
                  @Param("roomType") Integer roomType);

    /**
     * Counts the total number of rooms.
     *
     * @return the total number of rooms
     */
    @Select("SELECT COUNT(*) FROM mamba.room")
    int countRooms();

    /**
     * Queries a room based on its name.
     *
     * @param roomName the name of the room
     * @return the room matching the name
     */
    @Select("SELECT * FROM mamba.room WHERE roomName = #{roomName}")
    Room getRoomByName(String roomName);

    /**
     * Static inner classes - Generate dynamic SQL
     */
    class RoomSqlBuilder {
        /**
         * Dynamic SQL: Queries the room list
         */
        public static String buildGetRoomsSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.room");

                if (params.get("id") != null) {
                    WHERE("id = #{id}");
                }
                if (params.get("roomName") != null && !params.get("roomName").toString().isEmpty()) {
                    WHERE("roomName LIKE CONCAT('%', #{roomName}, '%')");
                }
                if (params.get("capacity") != null) {
                    WHERE("capacity >= #{capacity}");
                }
                if (params.get("multimedia") != null) {
                    WHERE("multimedia = #{multimedia}");
                }
                if (params.get("projector") != null) {
                    WHERE("projector = #{projector}");
                }
                if (params.get("requireApproval") != null) {
                    WHERE("requireApproval = #{requireApproval}");
                }
                if (params.get("isRestricted") != null) {
                    WHERE("isRestricted = #{isRestricted}");
                }
                if (params.get("roomType") != null) {
                    WHERE("roomType = #{roomType}");
                }
                ORDER_BY("id ASC");
            }};

            // Deal with LIMIT and OFFSET
            String query = sql.toString();
            if (params.get("pageSize") != null) {
                if (params.get("offset") != null) {
                    query += " LIMIT #{pageSize} OFFSET #{offset}";
                } else {
                    query += " LIMIT #{pageSize}";
                }
            }
            return query;
        }

        public static String buildCountRoomsSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("COUNT(*)");
                FROM("mamba.room");

                if (params.get("id") != null) {
                    WHERE("id = #{id}");
                }
                if (params.get("roomName") != null && !params.get("roomName").toString().isEmpty()) {
                    WHERE("roomName LIKE CONCAT('%', #{roomName}, '%')");
                }
                if (params.get("capacity") != null) {
                    WHERE("capacity >= #{capacity}");
                }
                if (params.get("multimedia") != null) {
                    WHERE("multimedia = #{multimedia}");
                }
                if (params.get("projector") != null) {
                    WHERE("projector = #{projector}");
                }
                if (params.get("requireApproval") != null) {
                    WHERE("requireApproval = #{requireApproval}");
                }
                if (params.get("isRestricted") != null) {
                    WHERE("isRestricted = #{isRestricted}");
                }
                if (params.get("roomType") != null) {
                    WHERE("roomType = #{roomType}");
                }
            }};

            return sql.toString();
        }


        /**
         * Dynamic SQL: Updates room information
         */
        public static String buildUpdateRoomSql(Map<String, Object> params) {
            return new SQL() {{
                UPDATE("mamba.room");
                if (params.get("roomName") != null && !params.get("roomName").toString().isEmpty()) {
                    SET("roomName = #{roomName}");
                }
                if (params.get("capacity") != null) {
                    SET("capacity = #{capacity}");
                }
                if (params.get("isBusy") != null) {
                    SET("isBusy = #{isBusy}");
                }
                if (params.get("location") != null && !params.get("location").toString().isEmpty()) {
                    SET("location = #{location}");
                }
                if (params.get("multimedia") != null) {
                    SET("multimedia = #{multimedia}");
                }
                if (params.get("projector") != null) {
                    SET("projector = #{projector}");
                }
                if (params.get("requireApproval") != null) {
                    SET("requireApproval = #{requireApproval}");
                }
                if (params.get("isRestricted") != null) {
                    SET("isRestricted = #{isRestricted}");
                }
                if (params.get("roomType") != null) {
                    SET("roomType = #{roomType}");
                }
                if (params.get("url") != null && !params.get("url").toString().isEmpty()) {
                    SET("url = #{url}");
                }
                if (params.get("description") != null && !params.get("description").toString().isEmpty()) {
                    SET("description = #{description}");
                }
                if (params.get("maxBookingDuration") != null) {
                    SET("maxBookingDuration = #{maxBookingDuration}");
                }

                if (params.get("id") != null) {
                    WHERE("id = #{id}");
                } else {
                    throw new IllegalArgumentException("Must contain: id");
                }
            }}.toString();
        }
    }
}
