package org.mamba.service;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mamba.entity.Admin;
import org.mamba.entity.Record;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    /**
     * Obtains the admin list.
     */
    @Select("SELECT * FROM mamba.admin")
    List<Admin> getAdmins();

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
     * Reassign a room for a record.
     *
     * @param recordId the record id
     */
    void deleteAndReassignRoom(Integer recordId, LocalDateTime newStartTime, LocalDateTime newEndTime);
}
