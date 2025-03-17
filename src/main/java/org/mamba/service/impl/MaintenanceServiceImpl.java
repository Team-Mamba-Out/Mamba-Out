package org.mamba.service.impl;

import org.mamba.entity.Maintenance;
import org.mamba.mapper.MaintenanceMapper;
import org.mamba.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    @Override
    public void updateMaintenanceStatus() {
        maintenanceMapper.updateMaintenanceStatus();
        maintenanceMapper.setRoomUnderMaintenance();
    }

    @Override
    public Map<String, Object> getMaintenance(Integer id, Integer roomId, Date scheduledStart, Date scheduledEnd, Integer pageSize, Integer page) {
        int offset = (page - 1) * pageSize;
        List<Maintenance> maintenanceList = maintenanceMapper.getMaintenance(id, roomId, scheduledStart, scheduledEnd, pageSize, offset);
        int total = maintenanceMapper.countMaintenance();
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("maintenanceList", maintenanceList);
        return result;
    }

    @Override
    @Transactional
    public void createMaintenance(int roomId, Date ScheduledStart, Date ScheduledEnd, String description) {
        maintenanceMapper.insertMaintenance(roomId, ScheduledStart, ScheduledEnd, description);
    }

    @Override
    @Transactional
    public void deleteMaintenance(Integer id) {
        maintenanceMapper.deleteMaintenanceById(id);
    }

}
