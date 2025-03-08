package org.mamba.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer uid;
    private String role;
    private String microsoftId; // Microsoft's user ID (sub)
    private String email; // User's email
    private String name; // User's name
}
