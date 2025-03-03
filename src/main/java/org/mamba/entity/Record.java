package org.mamba.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Record {
    private Integer id;
    private Integer roomId;
    private Integer userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime recordTime;
}
