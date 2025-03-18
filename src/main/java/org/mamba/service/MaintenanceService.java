package org.mamba.service;

import org.mamba.entity.Maintenance;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public interface MaintenanceService {
    /**
     * Automatically update the maintenance status of the room
     */
    void updateMaintenanceStatus();

    /**
     * Get maintenance records based on the given conditions
     *
     * @param id           the maintenance id
     * @param roomId       the room id
     * @param scheduledStart the scheduled start time
     * @param scheduledEnd the scheduled end time
     * @param pageSize     the number of records per page
     * @param page         the current page number
     * @return the list of maintenance records
     */
    Map<String, Object> getMaintenance(Integer id, Integer roomId, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, Integer pageSize, Integer page);

    /**
     * Add a new maintenance record
     *
     * @param roomId the ID of the room
     * @param ScheduledStart the scheduled start time of the maintenance
     * @param ScheduledEnd the scheduled end time of the maintenance
     * @param description the description of the maintenance
     */
    void createMaintenance(int roomId, LocalDateTime ScheduledStart, LocalDateTime  ScheduledEnd, String description);

    /**
     * Delete a maintenance record by ID
     *
     * @param id the ID of the maintenance record to be deleted
     */
    void deleteMaintenance(Integer id);
}
