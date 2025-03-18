package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Maintenance;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface MaintenanceMapper {
    @SelectProvider(type = MaintenanceSqlBuilder.class, method = "buildGetMaintenanceSql")
    List<Maintenance> getMaintenance(@Param("id") Integer id,
                                     @Param("roomId") Integer roomId,
                                     @Param("scheduledStart") Date scheduledStart,
                                     @Param("scheduledEnd") Date scheduledEnd,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("offset") Integer offset);

    @Update("UPDATE maintenance " +
            "SET maintenanceStatusId = 1 " +
            "WHERE scheduledEnd <= CURRENT_TIMESTAMP " +
            "AND maintenanceStatusId = 2 " +
            "AND scheduledEnd IS NOT NULL")
    int updateMaintenanceStatus();

    @Update("UPDATE maintenance " +
            "SET maintenanceStatusId = 2 " +
            "WHERE scheduledStart <= CURRENT_TIMESTAMP " +
            "AND maintenanceStatusId = 1 " +
            "AND scheduledStart IS NOT NULL " +
            "AND scheduledEnd > CURRENT_TIMESTAMP")
    int setRoomUnderMaintenance();



    @SelectProvider(type = MaintenanceSqlBuilder.class, method = "buildCountMaintenanceSql")
    int countMaintenance(@Param("id") Integer id,
                         @Param("roomId") Integer roomId,
                         @Param("scheduledStart") Date scheduledStart,
                         @Param("scheduledEnd") Date scheduledEnd);


    @Insert("INSERT INTO maintenance (roomId, scheduledStart, scheduledEnd, description) " +
            "VALUES (#{roomId}, #{scheduledStart}, #{scheduledEnd}, #{description})")
    void insertMaintenance(@Param("roomId") Integer roomId,
                           @Param("scheduledStart") LocalDateTime scheduledStart,
                           @Param("scheduledEnd") LocalDateTime  scheduledEnd,
                           @Param("description") String description);

    @Delete("DELETE FROM maintenance WHERE id = #{id}")
    void deleteMaintenanceById(@Param("id") Integer id);

    class MaintenanceSqlBuilder {
        public static String buildGetMaintenanceSql(Map<String, Object> params) {
            return new SQL() {{
                SELECT("*");
                FROM("maintenance");
                if (params.get("id") != null) {
                    WHERE("id = #{id}");
                }
                if (params.get("roomId") != null) {
                    WHERE("roomId = #{roomId}");
                }
                if (params.get("scheduledStart") != null) {
                    WHERE("scheduledStart >= #{scheduledStart}");
                }
                if (params.get("scheduledEnd") != null) {
                    WHERE("scheduledEnd <= #{scheduledEnd}");
                }
                ORDER_BY("scheduledStart DESC");
            }}.toString() + " LIMIT #{pageSize} OFFSET #{offset}";
        }

        public static String buildCountMaintenanceSql(Map<String, Object> params) {
            Integer id = (Integer) params.get("id");
            Integer roomId = (Integer) params.get("roomId");
            Date scheduledStart = (Date) params.get("scheduledStart");
            Date scheduledEnd = (Date) params.get("scheduledEnd");

            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM maintenance WHERE 1=1");

            if (id != null) {
                sql.append(" AND id = #{id}");
            }
            if (roomId != null) {
                sql.append(" AND room_id = #{roomId}");
            }
            if (scheduledStart != null) {
                sql.append(" AND scheduled_start >= #{scheduledStart}");
            }
            if (scheduledEnd != null) {
                sql.append(" AND scheduled_end <= #{scheduledEnd}");
            }

            return sql.toString();
        }

    }
}
