package org.mamba.entity;

import lombok.Data;

@Data
public class Room {
    private Integer id;
    private String roomName;
    private Integer capacity;
    private boolean isBusy;
    private String location;
    private boolean multimedia;
    private boolean projector;
    private boolean requireApproval;
    private boolean isRestricted;
    private String description;
    private Integer roomType;
    private String url;
    private Integer maxBookingDuration;
}
