package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Room;

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
                        @Param("pageSize") Integer pageSize,
                        @Param("offset") Integer offset);

    /**
     * Insert a new room.
     */
    @Insert("INSERT INTO mamba.room (roomName, capacity, isBusy, location, multimedia, projector, requireApproval, isRestricted, url) " +
            "VALUES (#{roomName}, #{capacity}, #{isBusy}, #{location}, #{multimedia}, #{projector}, #{requireApproval}, #{isRestricted}, #{url})")
    void createRoom(@Param("roomName") String roomName,
                    @Param("capacity") Integer capacity,
                    @Param("isBusy") Boolean isBusy,
                    @Param("location") String location,
                    @Param("multimedia") Boolean multimedia,
                    @Param("projector") Boolean projector,
                    @Param("requireApproval") Boolean requireApproval,
                    @Param("isRestricted") Boolean isRestricted,
                    @Param("url") String url);

    /**
     * Update room information by ID.
     */
    @UpdateProvider(type = RoomSqlBuilder.class, method = "buildUpdateRoomSql")
    void updateRoomById(@Param("id") Integer id,
                        @Param("roomName") String roomName,
                        @Param("capacity") Integer capacity,
                        @Param("isBusy") Boolean isBusy,
                        @Param("location") String location,
                        @Param("multimedia") Boolean multimedia,
                        @Param("projector") Boolean projector,
                        @Param("requireApproval") Boolean requireApproval,
                        @Param("isRestricted") Boolean isRestricted,
                        @Param("url") String url);

    /**
     * Deletes the room specified by ID.
     */
    @Delete("DELETE FROM mamba.room WHERE id = #{id}")
    void deleteRoomById(@Param("id") Integer id);

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
                WHERE("1=1");

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
                if (params.get("url") != null && !params.get("url").toString().isEmpty()) {
                    SET("url = #{url}");
                }
                WHERE("id = #{id}");
            }}.toString();
        }
    }
}
