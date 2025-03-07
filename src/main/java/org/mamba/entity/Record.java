package org.mamba.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Record {
    private Integer id;
    private Integer roomId;
    private Integer userId;
    private Room correspondingRoom;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime recordTime;
    private Boolean isLasting;
    private boolean hasCheckedIn;
    private boolean isCancelled;
}
