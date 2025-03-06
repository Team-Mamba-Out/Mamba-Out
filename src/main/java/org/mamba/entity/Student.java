package org.mamba.entity;

import lombok.Data;

@Data
public class Student {
    private String email;
    private Integer uid;
    private String name;
    private String phone;
    private Integer breakTimer;
}
