package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper {

    /**
     * Obtains the record list based on the conditions given.
     */
    @SelectProvider(type = RecordSqlBuilder.class, method = "buildGetRecordsSql")
    List<Record> getRecords(@Param("id") Integer id,
                            @Param("roomId") Integer roomId,
                            @Param("userId") Integer userId,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime,
                            @Param("hasCheckedIn") Boolean hasCheckedIn,
                            @Param("statusId") Integer statusId,
                            @Param("pageSize") Integer pageSize,
                            @Param("offset") Integer offset,
                            @Param("isApproved") Boolean isApproved);

    /**
     * Obtains the record specified by ID given.
     */
    @Update("UPDATE mamba.record SET isApproved = true WHERE id = #{id}")
    void approveRestrictedRoomRecord(Integer id);

    /**
     * Finds records for a given room within a specified time range, including those that partially overlap.
     *
     * @param roomId the ID of the room
     * @param occupyStartTime the start time of the range
     * @param occupyEndTime the end time of the range
     * @return a list of records that overlap with the specified time range
     */
    @Select("SELECT * FROM mamba.record WHERE roomId = #{roomId} AND " +
            "(startTime < #{occupyEndTime} AND endTime > #{occupyStartTime})")
    List<Record> findRecordsByRoomAndTimeRange(@Param("roomId") Integer roomId,
                                               @Param("occupyStartTime") LocalDateTime occupyStartTime,
                                               @Param("occupyEndTime") LocalDateTime occupyEndTime);


    /**
     * counts the total number of records
     */
    @SelectProvider(type = RecordSqlBuilder.class, method = "buildCountRecordsSql")
    Integer count(@Param("id") Integer id,
                  @Param("roomId") Integer roomId,
                  @Param("userId") Integer userId,
                  @Param("startTime") LocalDateTime startTime,
                  @Param("endTime") LocalDateTime endTime,
                  @Param("hasCheckedIn") Boolean hasCheckedIn,
                  @Param("statusId") Integer statusId);

    /**
     * Insert a new record.
     */
    @Insert("INSERT INTO mamba.record (roomId, userId, startTime, endTime, recordTime, hasCheckedIn,isApproved) " +
            "VALUES (#{roomId}, #{userId}, #{startTime}, #{endTime}, #{recordTime}, #{hasCheckedIn}, #{isApproved})")
    void createRecord(@Param("roomId") Integer roomId,
                      @Param("userId") Integer userId,
                      @Param("startTime") LocalDateTime startTime,
                      @Param("endTime") LocalDateTime endTime,
                      @Param("recordTime") LocalDateTime recordTime,
                      @Param("hasCheckedIn") Boolean hasCheckedIn,
                      @Param("isApproved") Boolean isApproved);

    /**
     * Deletes the record specified by id.
     */
    @Delete("DELETE FROM mamba.record WHERE id = #{id}")
    void deleteRecordById(@Param("id") Integer id);

    /**
     *  allow update record list.
     */
    @Update("UPDATE Record " +
            "SET " +
            "  statusId = " +
            "    CASE " +
            "      WHEN statusId = 4 THEN statusId  " +
            "      WHEN NOW() >= startTime AND hasCheckedIn = false THEN 5  " +
            "      WHEN NOW() >= startTime AND hasCheckedIn = true THEN 2  " +
            "      WHEN statusId = 2 AND NOW() > endTime THEN 3  " +
            "      ELSE statusId  " +
            "    END,  " +
            "  allowCheckIn = " +
            "    CASE " +
            "      WHEN NOW() >= startTime - INTERVAL 10 MINUTE AND NOW() < startTime And hasCheckedIn = false THEN true  " +
            "      WHEN NOW() >= startTime THEN false  " +
            "      ELSE allowCheckIn " +
            "    END;")
    void updateRecordStatus();


    @Update("UPDATE mamba.record set allowCheckIn = false, statusId = 2, hasCheckedIn = true where id = #{id}")
    void checkIn(@Param("id") Integer id);

    @Update("UPDATE mamba.record SET statusId = 4 WHERE id = #{id}")
    void cancelRecordById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM mamba.record")
    int countRecords();

    /**
     * Counts the total number of orders for teachers.
     *
     * @return the total number of orders for teachers
     */
    @Select("SELECT COUNT(*) FROM mamba.record WHERE userId IN (SELECT uid FROM mamba.user WHERE role = 'Teacher')")
    int countTeacherOrders();

    /**
     * Counts the total number of orders for students.
     *
     * @return the total number of orders for students
     */
    @Select("SELECT COUNT(*) FROM mamba.record WHERE userId IN (SELECT uid FROM mamba.user WHERE role = 'Student')")
    int countStudentOrders();

    /**
     * Counts the total number of completed orders.
     *
     * @return the total number of completed orders
     */
    @Select("SELECT COUNT(*) FROM mamba.record WHERE statusId = 3")
    int countCompletedOrders();

    /**
     * Counts the total number of incomplete orders.
     *
     * @return the total number of incomplete orders
     */
    @Select("SELECT COUNT(*) FROM mamba.record WHERE statusId == 1 or statusId == 2")
    int countIncompleteOrders();

    /**
     * Static class to build SQL queries for the record table.
     */
    class RecordSqlBuilder {
        public static String buildGetRecordsSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.record");

                if (params.get("id") != null) {
                    WHERE("id = #{id}");
                }
                if (params.get("roomId") != null) {
                    WHERE("roomId = #{roomId}");
                }
                if (params.get("userId") != null) {
                    WHERE("userId = #{userId}");
                }
                if (params.get("startTime") != null) {
                    WHERE("startTime >= #{startTime}");
                }
                if (params.get("endTime") != null) {
                    WHERE("endTime <= #{endTime}");
                }
                if (params.get("hasCheckedIn") != null) {
                    WHERE("hasCheckedIn = #{hasCheckedIn}");
                }
                if (params.get("statusId") != null) {
                    WHERE("statusId = #{statusId}");
                }
                if (params.get("isApproved") != null) {
                    WHERE("isApproved = #{isApproved}");
                }
                ORDER_BY("recordTime DESC");
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

        public static String buildCountRecordsSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("COUNT(*)");
                FROM("mamba.record");

                if (params.get("id") != null) {
                    WHERE("id = #{id}");
                }
                if (params.get("roomId") != null) {
                    WHERE("roomId = #{roomId}");
                }
                if (params.get("userId") != null) {
                    WHERE("userId = #{userId}");
                }
                if (params.get("startTime") != null) {
                    WHERE("startTime >= #{startTime}");
                }
                if (params.get("endTime") != null) {
                    WHERE("endTime <= #{endTime}");
                }
                if (params.get("hasCheckedIn") != null) {
                    WHERE("hasCheckedIn = #{hasCheckedIn}");
                }
                if (params.get("statusId") != null) {
                    WHERE("statusId = #{statusId}");
                }
            }};

            return sql.toString();
        }
    }
}
