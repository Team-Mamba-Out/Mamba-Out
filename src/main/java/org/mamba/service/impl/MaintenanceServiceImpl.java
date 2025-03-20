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
        maintenanceMapper.updateMaintenanceStatus();
        maintenanceMapper.setRoomUnderMaintenance();
    }



    @Override
    public Map<String, Object> getMaintenance(Integer id, Integer roomId, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, Integer size, Integer page) {
        Integer offset = null;
        if (size != null && page != null) {
            offset = (page - 1) * size;
        }
        List<Maintenance> maintenanceList = maintenanceMapper.getMaintenance(id, roomId, scheduledStart, scheduledEnd, size, offset);
        int total = maintenanceMapper.countMaintenance(id, roomId, scheduledStart, scheduledEnd);
        Integer totalPage = null;
        if (size != null) {
            totalPage = total % size == 0 ? total / size : total / size + 1;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("totalPage", totalPage);
        result.put("total", total);
        result.put("pageNumber", page);
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
