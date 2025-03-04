package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Lecturer;

import java.util.List;
import java.util.Map;

/**
 * Mapper interface for database operations on the Lecturer entity.
 */
@Mapper
public interface LecturerMapper {
    /**
     * Obtains the lecturer list based on the given conditions.
     */
    @SelectProvider(type = LecturerMapper.LecturerSqlBuilder.class, method = "buildGetLecturersSql")
    List<Lecturer> getLecturers(@Param("email") String email,
                              @Param("uid") Integer uid,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("pageSize") Integer pageSize,
                              @Param("offset") Integer offset);

    /**
     * Insert a new lecturer.
     */
    @Insert("INSERT INTO mamba.lecturer (email, uid, name, phone) " +
            "VALUES (#{email}, #{uid}, #{name}, #{phone})")
    void createLecturer(@Param("email") String email,
                       @Param("uid") Integer uid,
                       @Param("name") String name,
                       @Param("phone") String phone);

    /**
     * Update lecturer information by email.
     */
    @UpdateProvider(type = LecturerMapper.LecturerSqlBuilder.class, method = "buildUpdateLecturerSql")
    void updateLecturerByEmail(@Param("email") String email,
                           @Param("uid") Integer uid,
                           @Param("name") String name,
                           @Param("phone") String phone);

    /**
     * Deletes the lecturer specified by email.
     */
    @Delete("DELETE FROM mamba.lecturer WHERE email = #{email}")
    void deleteLecturerByEmail(@Param("email") String email);

    /**
     * Static class to build SQL queries for the lecturer table.
     */
    class LecturerSqlBuilder {
        /**
         * Dynamic SQL: Queries the lecturer list
         */
        public static String buildGetLecturersSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.lecturer");

                if (params.get("email") != null && !params.get("email").toString().isEmpty()) {
                    WHERE("email = #{email}");
                }
                if (params.get("uid") != null) {
                    WHERE("uid = #{uid}");
                }
                if (params.get("name") != null && !params.get("name").toString().isEmpty()) {
                    WHERE("name = #{name}");
                }
                if (params.get("phone") != null && !params.get("phone").toString().isEmpty()) {
                    WHERE("phone = #{phone}");
                }
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
         * Dynamic SQL: Updates lecturer information
         */
        public static String buildUpdateLecturerSql(Map<String, Object> params) {
            return new SQL() {{
                UPDATE("mamba.lecturer");
                if (params.get("email") != null && !params.get("email").toString().isEmpty()) {
                    SET("email = #{email}");
                }
                if (params.get("uid") != null) {
                    SET("uid = #{uid}");
                }
                if (params.get("name") != null && !params.get("name").toString().isEmpty()) {
                    SET("name = #{name}");
                }
                if (params.get("phone") != null && !params.get("phone").toString().isEmpty()) {
                    SET("phone = #{phone}");
                }
            }}.toString();
        }

    }
}
