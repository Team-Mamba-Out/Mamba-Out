package org.mamba.service;

public interface AdminService {

    /**
     * Reassign a room for a record.
     *
     * @param recordId the record id
     */
    void reassignRoomForRecord(Integer recordId);
}
