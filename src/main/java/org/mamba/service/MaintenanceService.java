package org.mamba.service;

import org.mamba.entity.Maintenance;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MaintenanceService {
    /**
     * Automatically update the maintenance status of the room
     */
    void updateMaintenanceStatus();

    List<List<LocalDateTime>> getFreeTimesById(Integer id);

    List<List<LocalDateTime>> get7DaysFreeTimesById(Integer id);
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

    int countMaintenanceByRoomAndTime(Integer roomId, Integer rangeType);


    Double getTotalMaintenanceDuration(Integer roomId, Integer rangeType);

    List<List<LocalDateTime>> getFreeMaintainTime(Integer roomId);
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
