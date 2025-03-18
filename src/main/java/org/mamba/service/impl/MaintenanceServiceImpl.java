package org.mamba.service.impl;

import org.mamba.entity.Maintenance;
import org.mamba.mapper.MaintenanceMapper;
import org.mamba.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Scheduled(cron = "0 * * * * ?")
    @Override
    public void updateMaintenanceStatus() {
        System.out.println("Scheduled task started...");
        maintenanceMapper.updateMaintenanceStatus();
        maintenanceMapper.setRoomUnderMaintenance();
        System.out.println("Scheduled task finished...");
    }



    @Override
    public Map<String, Object> getMaintenance(Integer id, Integer roomId, Date scheduledStart, Date scheduledEnd, Integer pageSize, Integer page) {
        int offset = (page - 1) * pageSize;
        List<Maintenance> maintenanceList = maintenanceMapper.getMaintenance(id, roomId, scheduledStart, scheduledEnd, pageSize, offset);
        int total = maintenanceMapper.countMaintenance(id, roomId, scheduledStart, scheduledEnd);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("maintenanceList", maintenanceList);
        return result;
    }

    @Override
    @Transactional
    public void createMaintenance(int roomId, LocalDateTime ScheduledStart, LocalDateTime  ScheduledEnd, String description) {
        maintenanceMapper.insertMaintenance(roomId, ScheduledStart, ScheduledEnd, description);
    }

    @Override
    @Transactional
    public void deleteMaintenance(Integer id) {
        maintenanceMapper.deleteMaintenanceById(id);
    }

}
