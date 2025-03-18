package org.mamba.entity;

import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Maintenance {
        private int id;
        private int roomId;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        String description;
        private int maintenanceStatusId;
}
