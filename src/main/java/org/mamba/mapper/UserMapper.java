package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.User;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 获取用户列表，根据给定的条件进行筛选。
     */
    @SelectProvider(type = UserSqlBuilder.class, method = "buildGetUsersSql")
    List<User> getUsers(@Param("uid") Integer uid,
                        @Param("role") String role,
                        @Param("pageSize") Integer pageSize,
                        @Param("offset") Integer offset);



    /**
     * 根据用户ID更新用户信息。
     */
    @UpdateProvider(type = UserSqlBuilder.class, method = "buildUpdateUserSql")
    void updateUserByUid(@Param("uid") Integer uid,
                         @Param("role") String role);

    /**
     * 静态类，用于构建SQL查询。
     */
    class UserSqlBuilder {
        /**
         * 动态SQL：查询用户列表
         */
        public static String buildGetUsersSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.user");

                if (params.get("uid") != null) {
                    WHERE("uid = #{uid}");
                }
                if (params.get("role") != null && !params.get("role").toString().isEmpty()) {
                    WHERE("role = #{role}");
                }
            }};

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
         * 动态SQL：更新用户信息
         */
        public static String buildUpdateUserSql(Map<String, Object> params) {
            return new SQL() {{
                UPDATE("mamba.user");
                if (params.get("role") != null && !params.get("role").toString().isEmpty()) {
                    SET("role = #{role}");
                }
            }}.toString();
        }
    }
}