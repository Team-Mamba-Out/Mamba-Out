package org.mamba.service;

import org.mamba.entity.Record;

import java.time.LocalDateTime;

public interface AdminService {

    /**
     * Reassign a room for a record.
     *
     * @param recordId the record id
     */
    void deleteAndReassignRoom(Integer recordId, LocalDateTime newStartTime, LocalDateTime newEndTime);
}
