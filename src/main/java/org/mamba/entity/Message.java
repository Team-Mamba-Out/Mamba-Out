package org.mamba.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Integer id;
    private Integer Uid;
    private String title;
    private String text;
    private LocalDateTime createTime;

}
