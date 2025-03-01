package org.mamba.entity;

import lombok.Data;

@Data
public class Room {
    private Integer id;
    private String roomName;
    private Integer capacity;
}
