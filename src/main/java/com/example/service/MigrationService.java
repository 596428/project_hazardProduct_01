package com.example.service;

import com.example.dto.migration.MigrationDto;
import com.example.dto.migration.ColumnInfoDto;
import java.util.List;
import java.util.Map;

public interface MigrationService {
    MigrationDto.Response migrateTables(MigrationDto.Request request, Integer userId);
    List<MigrationDto.HistoryResponse> getMigrationHistory(Integer userId);
    List<String> getAllTables();
    List<ColumnInfoDto> getTableColumns(String tableName);
    List<String> getTableConstraints(String tableName);
    boolean testConnection(MigrationDto.Request request);
    List<Map<String, Object>> getTablePreview(String tableName);
    Map<String, List<String>> getTableRelations();
} 