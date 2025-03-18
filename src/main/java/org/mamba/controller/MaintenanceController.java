package org.mamba.controller;

import org.mamba.entity.Maintenance;
import org.mamba.entity.Result;
import org.mamba.entity.Maintenance;
import org.mamba.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

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
    @GetMapping("/getMaintenance")
    public Result getMaintenance(@RequestParam(required = false) Integer id,
                                 @RequestParam(required = false) Integer roomId,
                                 @RequestParam(required = false) LocalDateTime scheduledStart,
                                 @RequestParam(required = false) LocalDateTime scheduledEnd,
                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false, defaultValue = "1") Integer page) {
        Map<String, Object> maintenanceResult = maintenanceService.getMaintenance(id, roomId, scheduledStart, scheduledEnd, pageSize, page);
        return Result.success(maintenanceResult);
    }


    /**
     * Add a new maintenance record
     *
     * @param maintenance the maintenance record to be created
     * @return the result of the creation operation
     */
    @PostMapping("/create")
    public Result createMaintenance(@RequestBody Maintenance maintenance) {
        maintenanceService.createMaintenance(maintenance.getRoomId(), maintenance.getScheduledStart(), maintenance.getScheduledEnd(), maintenance.getDescription());
        return Result.success();
    }

    /**
     * Delete a maintenance record by ID
     *
     * @param id the ID of the maintenance record to be deleted
     * @return the result of the deletion operation
     */
    @DeleteMapping("/{id}")
    public Result deleteMaintenance(@PathVariable Integer id) {
        maintenanceService.deleteMaintenance(id);
        return Result.success();
    }
}