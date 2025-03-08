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
                            @Param("isCancelled") Boolean isCancelled,
                            @Param("pageSize") Integer pageSize,
                            @Param("offset") Integer offset);

    @Select("SELECT * FROM mamba.record WHERE id = #{id}")
    Record getRecordById(@Param("id") int id);

    /**
     * Insert a new record.
     */
    @Insert("INSERT INTO mamba.record (roomId, userId, startTime, endTime, recordTime, hasCheckedIn) " +
            "VALUES (#{roomId}, #{userId}, #{startTime}, #{endTime}, #{recordTime}, #{hasCheckedIn})")
    void createRecord(@Param("roomId") Integer roomId,
                      @Param("userId") Integer userId,
                      @Param("startTime") LocalDateTime startTime,
                      @Param("endTime") LocalDateTime endTime,
                      @Param("recordTime") LocalDateTime recordTime,
                      @Param("hasCheckedIn") Boolean hasCheckedIn);

    /**
     * Deletes the record specified by id.
     */
    @Delete("DELETE FROM mamba.record WHERE id = #{id}")
    void deleteRecordById(@Param("id") Integer id);

    /**
     *  allow update record list.
     */
    @Update("UPDATE Record " +
            "SET statusId = " +
            "  CASE " +
            "    WHEN startTime > NOW() THEN 1 " +
            "    WHEN NOW() BETWEEN startTime AND endTime THEN 2 " +
            "    ELSE 3 " +
            "  END")
    void updateRecordStatus();


    @Update("UPDATE mamba.record SET isCancelled = true WHERE id = #{id}")
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
    @Select("SELECT COUNT(*) FROM mamba.record WHERE statusId != 3")
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
                if (params.get("isCancelled") != null) {
                    WHERE("isCancelled = #{isCancelled}");
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
    }
}
