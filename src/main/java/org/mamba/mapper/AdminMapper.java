package org.mamba.mapper;

import org.apache.ibatis.annotations.*;
import org.mamba.entity.Admin;

import java.util.List;

@Mapper
public interface AdminMapper {
    /**
     * Obtains the admin list.
     */
    @Select("SELECT * FROM mamba.admin")
    List<Admin> getAdmins();

    /**
     * counts the total number of admins
     */
    @Select("SELECT COUNT(*) from mamba.admin;")
    Integer count();

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
}
