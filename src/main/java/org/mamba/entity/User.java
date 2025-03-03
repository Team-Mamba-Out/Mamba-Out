package org.mamba.entity;

import lombok.Data;

@Data
public class User {
    private Integer Uid;
    private String role;
    private String authenticationToken;
}
