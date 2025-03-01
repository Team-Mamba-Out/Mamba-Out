package org.mamba.entity;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private Integer role;
    private String password;
    private String authenticationToken;
    private String phone;

}
