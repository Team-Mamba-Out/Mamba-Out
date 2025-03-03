package org.mamba.entity;

import lombok.Data;

@Data
public class Record {
    private Integer id;
    private Integer roomId;
    private Integer userId;
    private String startTime;
    private String endTime;
    private String recordTime;

}
