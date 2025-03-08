package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.User;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * Retrieves a list of users based on the given conditions.
     *
     * @return a list of users
     */
    @SelectProvider(type = UserSqlBuilder.class, method = "buildGetUsersSql")
    List<User> getUsers(@Param("uid") Integer uid,
                        @Param("role") String role,
                        @Param("pageSize") Integer pageSize,
                        @Param("offset") Integer offset);

    /**
     * counts the total number of users
     */
    @Select("SELECT COUNT(*) from mamba.user;")
    Integer count();

    /**
     * Updates user information based on the user ID.
     *
     * @param uid  the user ID
     * @param role the user role
     */
    @UpdateProvider(type = UserSqlBuilder.class, method = "buildUpdateUserSql")
    void updateUserByUid(@Param("uid") Integer uid,
                         @Param("role") String role);

    /**
     * Inserts a new user into the database.
     */
    @Insert("INSERT INTO mamba.user (role) VALUES (#{role})")
    void createUser(@Param("role") String role);

    /**
     * Deletes a user from the database by their UID.
     *
     * @param uid the UID of the user to delete
     */
    @Delete("DELETE FROM mamba.user WHERE uid = #{uid}")
    void deleteUserByUid(@Param("uid") Integer uid);

    /**
     * Gets a user by their uid.
     *
     * @param uid the user id
     * @return the user with the specified uid
     */
    @Select("SELECT * FROM mamba.user WHERE uid = #{uid}")
    User getUserByUid(@Param("uid") Integer uid);

    /**
     * Static class for building SQL queries.
     */
    class UserSqlBuilder {
        /**
         * Dynamic SQL: Queries the user list.
         *
         * @param params the parameters for the query
         * @return the SQL query string
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
         * Dynamic SQL: Updates user information.
         *
         * @param params the parameters for the update
         * @return the SQL update string
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