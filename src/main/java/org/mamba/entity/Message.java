package org.mamba.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Integer id;
    private Integer receiver;
    private Integer roomId;
    private Integer type;
    private String title;
    private String text;
    private LocalDateTime createTime;
    private boolean isRead;
    private String sender;
    private Boolean isOperated;
}
