package com.example.dto.admin;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

// 
public class AdminDto {
    
    // 사용자 권한 관리
    @Data
    public static class UserManageRequest {
        private String targetUserId;
        private String newRoleCd;      // 01: 관리자, 02: 일반사용자
        private boolean isActive;
    }
    
    //위해상품정보수정
    @Data
    public static class GoodsModifyRequest {
        private String mstId;
        private String mstDataId;
        private Map<String, Object> updateFields;
    }
    
    // api키관리
    @Data
    public static class ApiKeyManageRequest {
        private String targetUserId;
        private boolean isActive;
        private LocalDateTime expirationDate;
    }
    
    //관리자용 통계
    @Data
    public static class StatisticsResponse {
        private int totalUsers;
        private int activeApiKeys;
        private int totalGoods;
        private List<LogEntry> recentLogs;
    }
    
    @Data
    public static class LogEntry {
        private LocalDateTime timestamp;
        private String userId;
        private String action;
        private String details;
    }

    @Data
    public static class RoleUpdateRequest {
        private Integer userId;
        private String roleCd;
    }
    
    @Data
    public static class ApiKeyExpiryUpdateRequest {
        private String apiKey;
        private String expiryDate;
    }
} 