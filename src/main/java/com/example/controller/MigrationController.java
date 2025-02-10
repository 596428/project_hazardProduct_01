package com.example.controller;

import com.example.service.MigrationService;
import com.example.dto.migration.MigrationDto;
import com.example.dto.migration.ColumnInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import com.example.security.CustomUserDetails;
import com.example.dto.user.UserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
public class MigrationController {
    
    private final MigrationService migrationService;

    @PostMapping("/execute")
    public MigrationDto.Response executeMigration(
            @RequestBody MigrationDto.Request request,
            @AuthenticationPrincipal UserDto userDto) {
        return migrationService.migrateTables(request, userDto.getUserId());
    }

    @GetMapping("/history")
    public List<MigrationDto.HistoryResponse> getMigrationHistory(
            @AuthenticationPrincipal UserDto userDto) {
        return migrationService.getMigrationHistory(userDto.getUserId());
    }

    @GetMapping("/tables")
    public List<String> getAllTables() {
        return migrationService.getAllTables();
    }

    @GetMapping("/tables/{tableName}/columns")
    public List<ColumnInfoDto> getTableColumns(@PathVariable String tableName) {
        return migrationService.getTableColumns(tableName);
    }

    @GetMapping("/tables/{tableName}/constraints")
    public List<String> getTableConstraints(@PathVariable String tableName) {
        return migrationService.getTableConstraints(tableName);
    }

    @GetMapping("/tables/{tableName}/preview")
    public List<Map<String, Object>> getTablePreview(@PathVariable String tableName) {
        return migrationService.getTablePreview(tableName);
    }

    @PostMapping("/test-connection")
    public ResponseEntity<?> testConnection(@RequestBody MigrationDto.Request request) {
        try {
            boolean isConnected = migrationService.testConnection(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", isConnected);
            response.put("message", isConnected ? "연결 성공" : "연결 실패");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/tables/relations")
    public Map<String, List<String>> getTableRelations() {
        return migrationService.getTableRelations();
    }
} 