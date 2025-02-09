package com.example.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId;
    private String userName;
    private String password;
    private String email;
    private String roleCd;
    private String apiKey;
    private LocalDateTime lastLoginDt;
    private String useYn;
    private LocalDateTime regDt;
} 