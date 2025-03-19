package org.mamba.service;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mamba.entity.Admin;
import org.mamba.entity.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AdminService {
    /**
     * Approve a restricted room record.
     *
     * @param id the id of the record to approve
     */
    void approveRestrictedRoomRecord(Integer id);

    /**
     * Reject a restricted room record.
     *
     * @param id the id of the record to reject
     */
    void rejectRestrictedRoomRecord(Integer id);

    /**
     * Retrieves the list of all administrators.
     * @param uid
     */
    String getNameByUid(Integer uid);

    void cancelRecordAndReassign(Integer id, String reason);

    /**
     * Retrieves the list of all administrators.
     *
     * @return a list of {@link Admin} objects representing all administrators.
     */
    Map<String, Object> getAdmins(String email, Integer uid, String name, String phone, Integer size, Integer page);

    /**
     * Creates a new administrator account and sends a welcome notification.
     *
     * @param email the email address of the new administrator.
     * @param uid the unique user ID of the administrator.
     * @param name the name of the administrator.
     * @param phone the phone number of the administrator.
     */
    void createAdmin(String email, Integer uid, String name, String phone);

    /**
     * Updates the administrator's information by email and sends a notification.
     *
     * @param email the email address of the administrator to update.
     * @param uid the updated user ID of the administrator.
     * @param name the updated name of the administrator.
     * @param phone the updated phone number of the administrator.
     */
    void updateAdminByEmail(String email, Integer uid, String name, String phone);

    int getUserAccount();

    void occupyAndReassignRoom(Integer roomId, LocalDateTime newStartTime, LocalDateTime newEndTime, String reason);

    /**
     * Retrieves all records with their corresponding room names.
     *
     * @return a list of records with room names
     */
    List<Map<String, Object>> getAllRecordsWithRoomNames();



}
