package org.mamba.service;

import org.mamba.entity.Record;

public interface AdminService {

    /**
     * Reassign a room for a record.
     *
     * @param recordId the record id
     */
    Record reassignRoomForRecord(Integer recordId);
}
