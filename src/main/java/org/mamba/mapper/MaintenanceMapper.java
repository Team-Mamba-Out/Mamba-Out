package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Maintenance;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface MaintenanceMapper {
    @SelectProvider(type = RoomMapper.RoomSqlBuilder.class, method = "buildGetMaintenanceSql")
    List<Maintenance> getMaintenance(@Param("id") Integer id,
                                     @Param("roomId") Integer roomId,
                                     @Param("scheduledStart") Date scheduledStart,
                                     @Param("scheduledEnd") Date scheduledEnd,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("offset") Integer offset);

    @Update("UPDATE maintenance SET maintenanceStatusId = 1 WHERE scheduledEnd <= NOW() AND maintenanceStatusId = 2")
    int updateMaintenanceStatus();

    @Update("UPDATE maintenance SET maintenanceStatusId = 2 WHERE scheduledStart <= NOW() AND maintenanceStatusId = 1")
    int setRoomUnderMaintenance();

    @SelectProvider(type = RoomMapper.RoomSqlBuilder.class, method = "buildCountMaintenanceSql")
    int countMaintenance();

    @Insert("INSERT INTO maintenance (roomId,scheduledStart, scheduledEnd, description) " +
            "VALUES (#{roomId},#{scheduledStart}, #{scheduledEnd}, #{description})")
    void insertMaintenance(Integer roomId, Date ScheduledStart, Date ScheduledEnd, String description);

    @Delete("DELETE FROM maintenance WHERE id = #{id}")
    void deleteMaintenanceById(@Param("id") Integer id);

    public static class MaintenanceSqlBuilder {
        public String buildGetRoomMaintenanceSql(Map<String, Object> params) {
            return new SQL() {{
                SELECT("*");
                FROM("maintenance");
                if (params.get("roomId") != null) {
                    WHERE("roomId = #{roomId}");
                }
                ORDER_BY("scheduledStart DESC");
            }}.toString() + " LIMIT #{pageSize} OFFSET #{offset}";
        }

        public String buildCountMaintenanceSql(Map<String, Object> params) {
            return new SQL() {{
                SELECT("COUNT(*)");
                FROM("maintenance");
                if (params.get("roomId") != null) {
                    WHERE("roomId = #{roomId}");
                }
            }}.toString();
        }

        public String buildGetMaintenanceSql(Map<String, Object> params) {
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
    }
}
