package org.mamba.service.impl;

import org.mamba.entity.Maintenance;
import org.mamba.entity.Record;
import org.mamba.mapper.MaintenanceMapper;
import org.mamba.mapper.RoomMapper;
import org.mamba.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Autowired
    private RoomMapper roomMapper;

    private final int DAILY_START_HOUR = 8;
    private final int DAILY_END_HOUR = 22;
    private final long PERIOD_MINUTE = 30;

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

    @Override
    public List<Map<String, Object>> getFreeTimesById(Integer id) {
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay(); // to 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay(); // 7 days later

        // Get all the records of the next 7 days
        List<Maintenance> maintenances = roomMapper.getFutureMaintenances(id, startOfDay, endOfDay);
        // Get busy times first
        List<List<LocalDateTime>> busyTimes = new ArrayList<>();

        // Put all the record time periods into the busy times list (each contains start/end time)
        for (Maintenance maintenance : maintenances) {
            busyTimes.add(Arrays.asList(maintenance.getScheduledStart(), maintenance.getScheduledEnd()));
        }

        Map<LocalDate, Set<LocalDateTime>> busyMap = new HashMap<>();
        for (List<LocalDateTime> busyTimeSlot : busyTimes) {
            if (busyTimeSlot.size() != 2) continue;
            LocalDateTime start = busyTimeSlot.get(0);
            LocalDate date = start.toLocalDate();
            busyMap.computeIfAbsent(date, k -> new HashSet<>()).add(start);
        }


        List<Map<String, Object>> freeResult = new ArrayList<>();

        // 7 consecutive days
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = now.toLocalDate().plusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.of(DAILY_START_HOUR, 0));
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.of(DAILY_END_HOUR, 0));

            for (LocalDateTime slotStart = dayStart; !slotStart.plusMinutes(PERIOD_MINUTE).isAfter(dayEnd); slotStart = slotStart.plusMinutes(PERIOD_MINUTE)) {
                if (!busyMap.getOrDefault(currentDate, Collections.emptySet()).contains(slotStart)) {
                    Map<String, Object> freeSlot = new HashMap<>();
                    freeSlot.put("startTime",slotStart);
                    freeSlot.put("endTime", slotStart.plusMinutes(PERIOD_MINUTE));
                    freeResult.add(freeSlot);
                }
            }
        }

        return freeResult;
    }

    @Override
    public int countMaintenanceByRoomAndTime(Integer roomId, Integer rangeType) {
        LocalDateTime startTime;

        if (rangeType.equals(1)) {
            startTime = LocalDateTime.now().minusMonths(1);
        } else if (rangeType.equals(2)) {
            startTime = LocalDateTime.now().minusMonths(2);
        } else if (rangeType.equals(3)) {
            startTime = LocalDateTime.now().minusMonths(3);
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }
        return maintenanceMapper.countMaintenanceByRoomAndTime(roomId, startTime);
    }

    @Override
    public Double getTotalMaintenanceDuration(Integer roomId, Integer rangeType) {
        LocalDateTime startTime;

        if (rangeType.equals(1)) {
            startTime = LocalDateTime.now().minusMonths(1);
        } else if (rangeType.equals(2)) {
            startTime = LocalDateTime.now().minusMonths(2);
        } else if (rangeType.equals(3)) {
            startTime = LocalDateTime.now().minusMonths(3);
        } else {
            throw new IllegalArgumentException("Invalid rangeType: " + rangeType);
        }
        return maintenanceMapper.sumMaintenanceDuration(roomId, startTime);
    }
}
