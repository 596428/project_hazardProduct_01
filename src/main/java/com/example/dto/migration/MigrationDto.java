package com.example.dto.migration;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MigrationDto {
    @Data
    public static class Request {
        private String targetDbType;  // 추가: "postgresql" 또는 "mariadb"
        private String targetHost;
        private int targetPort;
        private String targetDatabase;
        private String targetUsername;
        private String targetPassword;
        private List<String> selectedTables;
    }

    @Data
    public static class Response {
        private boolean success;
        private String message;
        private List<String> migratedTables;
        private String errorMessage;
    }

    @Data
    public static class HistoryResponse {
        private Long migrationId;
        private Integer userId;
        private LocalDateTime migrationDt;
        private String targetHost;
        private String targetDatabase;
        private String status;
        private String errorMessage;
        private List<String> tablesMigrated;
    }

    @Data
    public static class TableRelationDto {
        private String tableName;
        private List<String> relatedTables;  // FK로 연결된 테이블들
        private boolean isSelected;
    }
} 