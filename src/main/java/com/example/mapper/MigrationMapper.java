package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.dto.migration.MigrationDto;
import java.util.List;
import com.example.dto.migration.ColumnInfoDto;
import java.util.Map;
import com.example.dto.migration.ForeignKeyDto;

@Mapper
public interface MigrationMapper {
    void insertMigrationHistory(
        @Param("userId") Integer userId,
        @Param("targetHost") String targetHost,
        @Param("targetPort") Integer targetPort,
        @Param("targetDatabase") String targetDatabase,
        @Param("status") String status,
        @Param("errorMessage") String errorMessage,
        @Param("tablesMigrated") List<String> tablesMigrated
    );
    
    List<MigrationDto.HistoryResponse> getMigrationHistory(@Param("userId") Integer userId);
    List<String> getAllTables();
    List<ColumnInfoDto> getTableColumns(@Param("tableName") String tableName);
    List<String> getTableConstraints(@Param("tableName") String tableName);
    List<Map<String, Object>> getTablePreview(@Param("tableName") String tableName);
    List<Map<String, String>> getTableRelations();
    List<Map<String, Object>> getTableData(@Param("tableName") String tableName);
    List<ForeignKeyDto> getTableForeignKeys(@Param("tableName") String tableName);
    String getColumnType(@Param("tableName") String tableName, 
                        @Param("columnName") String columnName);
} 