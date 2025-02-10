package com.example.service.impl;

import com.example.service.MigrationService;
import com.example.mapper.MigrationMapper;
import com.example.dto.migration.MigrationDto;
import com.example.dto.migration.ColumnInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.Statement;
import com.example.dto.migration.ForeignKeyDto;
import java.util.Set;
import java.util.HashSet;
import com.example.util.DatabaseTypeMapper;

@Service
@RequiredArgsConstructor
public class MigrationServiceImpl implements MigrationService {
    
    private final MigrationMapper migrationMapper;
    private static final Logger log = LoggerFactory.getLogger(MigrationServiceImpl.class);

    @Override
    @Transactional
    public MigrationDto.Response migrateTables(MigrationDto.Request request, Integer userId) {
        MigrationDto.Response response = new MigrationDto.Response();
        List<String> migratedTables = new ArrayList<>();
        
        try {
            // 테이블 의존성 그래프 생성 및 정렬
            List<String> orderedTables = getOrderedTables(request.getSelectedTables());
            
            String targetUrl = getConnectionUrl(request);
            
            try (Connection targetConn = DriverManager.getConnection(
                    targetUrl,
                    request.getTargetUsername(),
                    request.getTargetPassword()
            )) {
                try (Statement stmt = targetConn.createStatement()) {
                    // DB 타입에 따라 다른 명령어 실행
                    if ("postgresql".equals(request.getTargetDbType())) {
                        stmt.execute("SET session_replication_role = 'replica';");
                    } else if ("mariadb".equals(request.getTargetDbType())) {
                        stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
                    }
                    
                    // 정렬된 순서대로 테이블 생성 및 데이터 복사
                    for (String tableName : orderedTables) {
                        try {
                            log.info("Creating table: " + tableName);
                            String createTableSQL = generateCreateTableSQL(tableName, request.getTargetDbType());
                            log.debug("Create SQL: " + createTableSQL);
                            stmt.execute(createTableSQL);
                            
                            log.info("Copying data for table: " + tableName);
                            List<Map<String, Object>> sourceData = migrationMapper.getTableData(tableName);
                            if (!sourceData.isEmpty()) {
                                for (Map<String, Object> row : sourceData) {
                                    String insertSQL = generateInsertSQL(tableName, row, request.getTargetDbType());
                                    log.debug("Insert SQL: " + insertSQL);
                                    stmt.execute(insertSQL);
                                }
                            }
                            
                            migratedTables.add(tableName);
                        } catch (Exception e) {
                            log.error("Error migrating table: " + tableName, e);
                            throw e;
                        }
                    }
                    
                    // DB 타입에 따라 다른 명령어 실행
                    if ("postgresql".equals(request.getTargetDbType())) {
                        stmt.execute("SET session_replication_role = 'origin';");
                    } else if ("mariadb".equals(request.getTargetDbType())) {
                        stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                    }
                }
                
                migrationMapper.insertMigrationHistory(
                    userId,
                    request.getTargetHost(),
                    request.getTargetPort(),
                    request.getTargetDatabase(),
                    "SUCCESS",
                    null,
                    migratedTables
                );
                
                response.setSuccess(true);
                response.setMigratedTables(migratedTables);
            }
        } catch (Exception e) {
            log.error("Migration failed", e);
            
            migrationMapper.insertMigrationHistory(
                userId,
                request.getTargetHost(),
                request.getTargetPort(),
                request.getTargetDatabase(),
                "FAILED",
                e.getMessage(),
                migratedTables
            );
            
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }
        
        return response;
    }

    // 테이블 의존성에 따라 정렬
    private List<String> getOrderedTables(List<String> selectedTables) {
        // 의존성 그래프 생성
        Map<String, Set<String>> dependencies = new HashMap<>();
        for (String tableName : selectedTables) {
            dependencies.put(tableName, new HashSet<>());
            List<ForeignKeyDto> fks = migrationMapper.getTableForeignKeys(tableName);
            for (ForeignKeyDto fk : fks) {
                if (selectedTables.contains(fk.getReferencedTable())) {
                    dependencies.get(tableName).add(fk.getReferencedTable());
                }
            }
        }
        
        // 위상 정렬 수행
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> temp = new HashSet<>();
        
        for (String tableName : selectedTables) {
            if (!visited.contains(tableName)) {
                topologicalSort(tableName, dependencies, visited, temp, result);
            }
        }
        
        return result;
    }

    private void topologicalSort(String tableName, Map<String, Set<String>> dependencies,
                               Set<String> visited, Set<String> temp, List<String> result) {
        if (temp.contains(tableName)) {
            throw new RuntimeException("순환 참조가 발견되었습니다: " + tableName);
        }
        if (!visited.contains(tableName)) {
            temp.add(tableName);
            Set<String> deps = dependencies.get(tableName);
            if (deps != null) {
                for (String dep : deps) {
                    topologicalSort(dep, dependencies, visited, temp, result);
                }
            }
            temp.remove(tableName);
            visited.add(tableName);
            result.add(tableName);
        }
    }

    private String getConnectionUrl(MigrationDto.Request request) {
        return switch (request.getTargetDbType()) {
            case "postgresql" -> String.format("jdbc:postgresql://%s:%d/%s",
                request.getTargetHost(),
                request.getTargetPort(),
                request.getTargetDatabase());
            case "mariadb" -> String.format("jdbc:mariadb://%s:%d/%s",
                request.getTargetHost(),
                request.getTargetPort(),
                request.getTargetDatabase());
            default -> throw new IllegalArgumentException("Unsupported database type: " + request.getTargetDbType());
        };
    }

    // 테이블 생성 SQL 생성
    private String generateCreateTableSQL(String tableName, String targetDbType) {
        StringBuilder sql = new StringBuilder();
        
        // 컬럼 정보 가져오기
        List<ColumnInfoDto> columns = migrationMapper.getTableColumns(tableName);
        
        if ("mariadb".equals(targetDbType)) {
            // MariaDB의 경우 시퀀스 대신 AUTO_INCREMENT 사용
            for (ColumnInfoDto col : columns) {
                if (col.getDefaultValue() != null && 
                    col.getDefaultValue().contains("nextval")) {
                    col.setDefaultValue(null); // AUTO_INCREMENT로 대체될 것이므로 기본값 제거
                }
            }
        } else {
            // PostgreSQL의 경우 시퀀스 생성
            for (ColumnInfoDto col : columns) {
                if (col.getDefaultValue() != null && 
                    col.getDefaultValue().contains("nextval")) {
                    String seqName = col.getDefaultValue()
                        .replaceAll("nextval\\('([^']+)'.*\\)", "$1");
                    sql.append("CREATE SEQUENCE IF NOT EXISTS ")
                       .append(seqName)
                       .append(";\n\n");
                }
            }
        }
        
        // FK 정보 가져오기
        List<ForeignKeyDto> foreignKeys = migrationMapper.getTableForeignKeys(tableName);
        
        // 테이블 생성
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
        
        // 컬럼 정의
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfoDto col = columns.get(i);
            sql.append("    ").append(col.getColumnName()).append(" ");
            
            // 데이터 타입 변환
            if ("mariadb".equals(targetDbType)) {
                sql.append(DatabaseTypeMapper.convertPostgreSQLToMariaDB(col.getDataType()));
                
                // AUTO_INCREMENT 처리
                if (col.getDefaultValue() != null && 
                    col.getDefaultValue().contains("nextval")) {
                    sql.append(" AUTO_INCREMENT");
                }
            } else {
                sql.append(col.getDataType());
            }
            
            if (col.getConstraints() != null) {
                if (col.getConstraints().contains("PK")) {
                    sql.append(" PRIMARY KEY");
                }
            }
            
            if (col.getNullable().equals("[v]")) {
                sql.append(" NOT NULL");
            }
            
            if (col.getDefaultValue() != null && !col.getDefaultValue().isEmpty() &&
                !col.getDefaultValue().contains("nextval")) {
                String defaultValue = DatabaseTypeMapper.convertDefaultValue(
                    col.getDefaultValue(), 
                    targetDbType
                );
                sql.append(" DEFAULT ").append(defaultValue);
            }
            
            sql.append(i < columns.size() - 1 ? "," : "");
            sql.append("\n");
        }
        
        // FK 제약조건 추가
        if (!foreignKeys.isEmpty()) {
            sql.append(",\n"); // 컬럼 정의와 FK 제약조건 사이에 콤마 추가
            for (int i = 0; i < foreignKeys.size(); i++) {
                ForeignKeyDto fk = foreignKeys.get(i);
                sql.append("    CONSTRAINT ").append(fk.getConstraintName())
                   .append(" FOREIGN KEY (").append(fk.getColumnName()).append(")")
                   .append(" REFERENCES ").append(fk.getReferencedTable())
                   .append("(").append(fk.getReferencedColumn()).append(")");
                
                if (i < foreignKeys.size() - 1) {
                    sql.append(",");
                }
                sql.append("\n");
            }
        }
        
        sql.append(");");
        return sql.toString();
    }

    // 데이터 삽입 SQL 생성
    private String generateInsertSQL(String tableName, Map<String, Object> data, String targetDbType) {
        List<String> columns = new ArrayList<>(data.keySet());
        List<String> values = new ArrayList<>();
        
        for (String column : columns) {
            Object value = data.get(column);
            if (value == null) {
                values.add("NULL");
            } else if ("mariadb".equals(targetDbType)) {
                // MariaDB용 값 변환
                String columnType = migrationMapper.getColumnType(tableName, column);
                values.add(DatabaseTypeMapper.convertValue(
                    columnType,
                    value.toString()
                ));
            } else {
                // PostgreSQL의 경우 기존 로직 유지
                if (value instanceof String) {
                    values.add("'" + ((String) value).replace("'", "''") + "'");
                } else if (value instanceof java.sql.Timestamp) {
                    // timestamp 형식 처리
                    values.add("'" + value.toString().substring(0, 19) + "'");
                } else {
                    values.add(value.toString());
                }
            }
        }
        
        return String.format(
            "INSERT INTO %s (%s) VALUES (%s)",
            tableName,
            String.join(", ", columns),
            String.join(", ", values)
        );
    }

    @Override
    public List<MigrationDto.HistoryResponse> getMigrationHistory(Integer userId) {
        return migrationMapper.getMigrationHistory(userId);
    }

    @Override
    public List<String> getAllTables() {
        return migrationMapper.getAllTables();
    }

    @Override
    public List<ColumnInfoDto> getTableColumns(String tableName) {
        return migrationMapper.getTableColumns(tableName);
    }

    @Override
    public List<String> getTableConstraints(String tableName) {
        return migrationMapper.getTableConstraints(tableName);
    }

    @Override
    public boolean testConnection(MigrationDto.Request request) {
        try {
            String url = getConnectionUrl(request);
            
            try (Connection conn = DriverManager.getConnection(
                    url,
                    request.getTargetUsername(),
                    request.getTargetPassword()
            )) {
                return true;
            }
        } catch (SQLException e) {
            log.error("Database connection test failed", e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getTablePreview(String tableName) {
        return migrationMapper.getTablePreview(tableName);
    }

    @Override
    public Map<String, List<String>> getTableRelations() {
        List<Map<String, String>> relations = migrationMapper.getTableRelations();
        Map<String, List<String>> result = new HashMap<>();
        
        for (Map<String, String> relation : relations) {
            String tableName = relation.get("key");
            String relatedTablesStr = relation.get("value");
            if (relatedTablesStr != null && !relatedTablesStr.isEmpty()) {
                result.put(tableName, Arrays.asList(relatedTablesStr.split(",")));
            } else {
                result.put(tableName, new ArrayList<>());
            }
        }
        
        return result;
    }
} 