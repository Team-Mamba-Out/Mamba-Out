package org.mamba.service.impl;

import org.mamba.entity.Maintenance;
import org.mamba.entity.Record;
import org.mamba.entity.Result;
import org.mamba.mapper.MaintenanceMapper;
import org.mamba.mapper.RoomMapper;
import org.mamba.service.AdminService;
import org.mamba.service.MaintenanceService;
import org.mamba.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Autowired
    @Lazy
    private MaintenanceService maintenanceService;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    @Lazy
    private RoomService roomService;

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
    public List<List<LocalDateTime>> getFreeMaintainTime(Integer roomId){
        List<List<LocalDateTime>> maintenances = maintenanceService.getFreeTimesById(roomId);
        List<List<LocalDateTime>> records = roomService.getFreeTimesById(roomId);

        List<List<LocalDateTime>> intersection = new ArrayList<>();

        for (List<LocalDateTime> maintenance : maintenances) {
            LocalDateTime maintenanceStart = maintenance.get(0);
            LocalDateTime maintenanceEnd =maintenance.get(1);

            for (List<LocalDateTime> record : records) {
                LocalDateTime recordStart = record.get(0);
                LocalDateTime recordEnd = record.get(1);

                // Check if the intervals overlap
                if (maintenanceStart.isBefore(recordEnd) && recordStart.isBefore(maintenanceEnd)) {
                    // Calculate the intersection
                    LocalDateTime start = maintenanceStart.isAfter(recordStart) ? maintenanceStart : recordStart;
                    LocalDateTime end = maintenanceEnd.isBefore(recordEnd) ? maintenanceEnd : recordEnd;

                    // Add the intersection to the result list
                    List<LocalDateTime> interval = new ArrayList<>();
                    interval.add(start);
                    interval.add(end);
                    intersection.add(interval);
                }
            }
        }

        List<List<LocalDateTime>> mergedFreeTimes = new ArrayList<>();
        List<LocalDateTime> currentInterval = intersection.get(0);

        for (int i = 1; i < intersection.size(); i++) {
            List<LocalDateTime> nextInterval = intersection.get(i);

            if (currentInterval.get(1).equals(nextInterval.get(0))) {
                currentInterval.set(1, nextInterval.get(1));
            } else {
                mergedFreeTimes.add(new ArrayList<>(currentInterval));
                currentInterval = nextInterval;
            }
        }

        mergedFreeTimes.add(currentInterval);

        return mergedFreeTimes;
    }

    @Override
    @Transactional
    public void createMaintenance(int roomId, LocalDateTime ScheduledStart, LocalDateTime  ScheduledEnd, String description) {
        List<List<LocalDateTime>> freeTimes = maintenanceService.getFreeMaintainTime(roomId);
//        Duration maintenanceDuration = Duration.between(ScheduledStart, ScheduledEnd);
//
//        List<List<LocalDateTime>> mergedFreeTimes = new ArrayList<>();
//        List<LocalDateTime> currentInterval = freeTimes.get(0);
//
//        for (int i = 1; i < freeTimes.size(); i++) {
//            List<LocalDateTime> nextInterval = freeTimes.get(i);
//
//            if (currentInterval.get(1).equals(nextInterval.get(0))) {
//                currentInterval.set(1, nextInterval.get(1));
//            } else {
//                mergedFreeTimes.add(new ArrayList<>(currentInterval));
//                currentInterval = nextInterval;
//            }
//        }
//
//        mergedFreeTimes.add(currentInterval);

        for (List<LocalDateTime> freeTime : freeTimes) {
            LocalDateTime freeStart = freeTime.get(0);
            LocalDateTime freeEnd = freeTime.get(1);

            if (!ScheduledStart.isBefore(freeStart) && !ScheduledEnd.isAfter(freeEnd))
            {
                maintenanceMapper.insertMaintenance(roomId, ScheduledStart, ScheduledEnd, description);
                return;
            }
        }

        throw new IllegalArgumentException("The maintenance duration exceeds the available free time.");

    }

    @Override
    @Transactional
    public void deleteMaintenance(Integer id) {
        maintenanceMapper.deleteMaintenanceById(id);
    }

    @Override
    public List<List<LocalDateTime>> getFreeTimesById(Integer id) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();  // 00:00
        LocalDateTime endOfDay = today.plusDays(7).atStartOfDay();  // 7 天后

        // 获取未来 7 天的所有预约记录
        List<Maintenance> records = roomMapper.getFutureMaintenances(id, startOfDay, endOfDay);

        // 使用 TreeMap 记录每天的忙碌时间段，方便按日期查找
        Map<LocalDate, List<List<LocalDateTime>>> busyMap = new TreeMap<>();

        for (Maintenance record : records) {
            LocalDate date = record.getScheduledStart().toLocalDate();
            busyMap.computeIfAbsent(date, k -> new ArrayList<>())
                    .add(Arrays.asList(record.getScheduledStart(), record.getScheduledEnd()));
        }

        List<List<LocalDateTime>> freeResult = new ArrayList<>();

        // 遍历未来 7 天
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = today.plusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.of(DAILY_START_HOUR, 0));
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.of(DAILY_END_HOUR, 0));

            List<List<LocalDateTime>> busySlots = busyMap.getOrDefault(currentDate, new ArrayList<>());

            // 按时间排序，确保 busySlots 是有序的
            busySlots.sort(Comparator.comparing(slot -> slot.get(0)));

            LocalDateTime slotStart = dayStart;

            for (List<LocalDateTime> busySlot : busySlots) {
                LocalDateTime busyStart = busySlot.get(0);
                LocalDateTime busyEnd = busySlot.get(1);

                // 如果当前 slotStart 早于 busyStart，则这个时间段是空闲的
                if (slotStart.isBefore(busyStart)) {
                    while (slotStart.plusMinutes(PERIOD_MINUTE).isBefore(busyStart) || slotStart.plusMinutes(PERIOD_MINUTE).equals(busyStart)) {
                        freeResult.add(Arrays.asList(slotStart, slotStart.plusMinutes(PERIOD_MINUTE)));
                        slotStart = slotStart.plusMinutes(PERIOD_MINUTE);
                    }
                }

                // 更新 slotStart 为当前 busy 结束时间，继续找下一个空闲时间
                if (slotStart.isBefore(busyEnd)) {
                    slotStart = busyEnd;
                }
            }

            // 检查 busySlots 之后是否还有空闲时间
            while (slotStart.plusMinutes(PERIOD_MINUTE).isBefore(dayEnd) || slotStart.plusMinutes(PERIOD_MINUTE).equals(dayEnd)) {
                freeResult.add(Arrays.asList(slotStart, slotStart.plusMinutes(PERIOD_MINUTE)));
                slotStart = slotStart.plusMinutes(PERIOD_MINUTE);
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
