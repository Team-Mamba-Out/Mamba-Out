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
    private Integer statusId;
    private String status;
    private boolean hasCheckedIn;

}
