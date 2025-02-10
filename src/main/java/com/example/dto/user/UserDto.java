package com.example.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    
    private Integer userId;
    private String email;
    private String userName;
    
    @Data
    public static class Request {
        private String userName;
        private String email;
        private String password;
        private String roleCd;    // 01: 관리자, 02: 일반사용자
    }
    
    @Data
    public static class Login {
        private String email;
        private String password;
    }
    
    @Data
    public static class Response {
        private Integer userId;
        private String email;
        private String userName;
        private String roleCd;
        private String token;
    }
    
    @Data
    public static class ManageRequest {
        private String targetUserId;
        private String newRoleCd;
        private boolean isActive;
    }
}