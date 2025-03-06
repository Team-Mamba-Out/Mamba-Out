package org.mamba.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Integer id;
    private Integer uid;
    private String title;
    private String text;
    private LocalDateTime createTime;
    private boolean isRead;
    private String sender;
}
