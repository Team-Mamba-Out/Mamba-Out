package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.mamba.entity.Admin;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    /**
     * Obtains the admin list.
     */
    @SelectProvider(type = AdminMapper.AdminSqlBuilder.class, method = "buildGetAdminsSql")
    List<Admin> getAdmins(@Param("email") String email,
                          @Param("uid") Integer uid,
                          @Param("name") String name,
                          @Param("phone") String phone,
                          @Param("pageSize") Integer pageSize,
                          @Param("offset") Integer offset);

    /**
     * counts the total number of admins
     */
    @Select("SELECT COUNT(*) from mamba.admin;")
    Integer count();


    @Select("SELECT role FROM mamba.user WHERE uid = #{uid}")
    String getRoleByUid(@Param("uid") Integer uid);

    @Select("SELECT name FROM Student WHERE email = #{email} AND #{role} = '001' " +
            "UNION ALL " +
            "SELECT name FROM Lecturer WHERE email = #{email} AND #{role} = '002' " +
            "UNION ALL " +
            "SELECT name FROM Admin WHERE email = #{email} AND #{role} = '003'")
    String getNameByEmailAndRole(@Param("email") String email, @Param("role") String role);

    /**
     * Insert a new admin.
     */
    @Insert("INSERT INTO mamba.admin (email, uid, name, phone) " +
            "VALUES (#{email}, #{uid}, #{name}, #{phone})")
    void createAdmin(@Param("email") String email,
                     @Param("uid") Integer uid,
                     @Param("name") String name,
                     @Param("phone") String phone);

    /**
     * Update admin information by email.
     */
    @Update("UPDATE mamba.admin SET uid = #{uid}, name = #{name}, phone = #{phone} WHERE email = #{email}")
    void updateAdminByEmail(@Param("email") String email,
                            @Param("uid") Integer uid,
                            @Param("name") String name,
                            @Param("phone") String phone);
    /**
     * Get the account of the users;
     * @return the corresponding count.
     */
    @Select("SELECT COUNT(*) mamba.user")
    int userAccount();
    class AdminSqlBuilder {
        /**
         * Dynamic SQL: Queries the lecturer list
         */
        public static String buildGetAdminsSql(Map<String, Object> params) {
            SQL sql = new SQL() {{
                SELECT("*");
                FROM("mamba.admin");

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



    }
}
