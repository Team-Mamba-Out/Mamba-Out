package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Student;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    /**
     * Obtains the student list based on the given conditions.
     */
    @SelectProvider(type = StudentMapper.StudentSqlBuilder.class, method = "buildGetStudentsSql")
    List<Student> getStudents(@Param("email") String email,
                              @Param("uid") Integer uid,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("breakTimer") Integer breakTimer,
                              @Param("pageSize") Integer pageSize,
                              @Param("offset") Integer offset);

    /**
     * counts the total number of students
     */
    @Select("SELECT COUNT(*) from mamba.student;")
    Integer count();

    /**
     * Insert a new student.
     */
    @Insert("INSERT INTO mamba.student (email, uid, name, phone, breakTimer) " +
            "VALUES (#{email}, #{uid}, #{name}, #{phone}, #{breakTimer})")
    void createStudent(@Param("email") String email,
                       @Param("uid") Integer uid,
                       @Param("name") String name,
                       @Param("phone") String phone,
                       @Param("breakTimer") Integer breakTimer);

    /**
     * Update student information by email.
     */
    @UpdateProvider(type = StudentMapper.StudentSqlBuilder.class, method = "buildUpdateStudentSql")
    void updateStudentByUid(@Param("email") String email,
                              @Param("uid") Integer uid,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("breakTimer") Integer breakTimer);

    /**
     * Deletes the student specified by email.
     */
    @Delete("DELETE FROM mamba.student WHERE email = #{email}")
    void deleteStudentByEmail(@Param("email") String email);

    /**
     * Static class to build SQL queries for the student table.
     */
    class StudentSqlBuilder {
        /**
         * Dynamic SQL: Queries the student list
         */
        public static String buildGetStudentsSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.student");

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
                if (params.get("breakTimer") != null) {
                    WHERE("breakTimer = #{breakTimer}");
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
         * Dynamic SQL: Updates student information
         */
        public static String buildUpdateStudentSql(Map<String, Object> params) {
            return new SQL() {{
                UPDATE("mamba.student");

                if (params.get("email") != null) {
                    SET("email = #{email}");
                }
                if (params.get("name") != null && !params.get("name").toString().isEmpty()) {
                    SET("name = #{name}");
                }
                if (params.get("phone") != null && !params.get("phone").toString().isEmpty()) {
                    SET("phone = #{phone}");
                }
                if (params.get("breakTimer") != null) {
                    SET("breakTimer = #{breakTimer}");
                }

                if (params.get("uid") != null) {
                    WHERE("uid = #{uid}");
                } else {
                    throw new IllegalArgumentException("Must contain: email");
                }
            }}.toString();
        }

    }
}