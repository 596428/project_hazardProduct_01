package com.example.util;

import java.util.HashMap;
import java.util.Map;

public class DatabaseTypeMapper {
    private static final Map<String, String> postgresqlToMariadbTypes = new HashMap<>();
    
    static {
        // 숫자 타입
        postgresqlToMariadbTypes.put("smallint", "SMALLINT");
        postgresqlToMariadbTypes.put("integer", "INT");
        postgresqlToMariadbTypes.put("bigint", "BIGINT");
        postgresqlToMariadbTypes.put("decimal", "DECIMAL");
        postgresqlToMariadbTypes.put("numeric", "DECIMAL");
        postgresqlToMariadbTypes.put("real", "FLOAT");
        postgresqlToMariadbTypes.put("double precision", "DOUBLE");
        
        // 문자열 타입
        postgresqlToMariadbTypes.put("character varying", "VARCHAR");
        postgresqlToMariadbTypes.put("varchar", "VARCHAR");
        postgresqlToMariadbTypes.put("character", "CHAR");
        postgresqlToMariadbTypes.put("char", "CHAR");
        postgresqlToMariadbTypes.put("text", "TEXT");
        
        // 날짜/시간 타입
        postgresqlToMariadbTypes.put("timestamp", "DATETIME");
        postgresqlToMariadbTypes.put("timestamp without time zone", "DATETIME");
        postgresqlToMariadbTypes.put("timestamp with time zone", "DATETIME");
        postgresqlToMariadbTypes.put("date", "DATE");
        postgresqlToMariadbTypes.put("time", "TIME");
        
        // 불리언 타입
        postgresqlToMariadbTypes.put("boolean", "TINYINT(1)");
        
        // 바이너리 타입
        postgresqlToMariadbTypes.put("bytea", "BLOB");
        
        // JSON 타입
        postgresqlToMariadbTypes.put("json", "JSON");
        postgresqlToMariadbTypes.put("jsonb", "JSON");
    }

    public static String convertPostgreSQLToMariaDB(String postgresType) {
        // 데이터 타입에서 크기 정보 추출 (예: varchar(255))
        String baseType = postgresType.replaceAll("\\(.*\\)", "").toLowerCase();
        String size = "";
        if (postgresType.contains("(")) {
            size = postgresType.substring(postgresType.indexOf("("));
        }
        
        String mariaDBType = postgresqlToMariadbTypes.get(baseType);
        if (mariaDBType == null) {
            // 매핑되지 않은 타입의 경우 기본값으로 처리
            return "TEXT";
        }
        
        // size 정보가 있는 경우 추가
        if (!size.isEmpty() && (mariaDBType.equals("VARCHAR") || mariaDBType.equals("CHAR"))) {
            return mariaDBType + size;
        }
        
        return mariaDBType;
    }

    public static String convertValue(String sourceType, String value) {
        if (value == null) {
            return "NULL";
        }

        // JSON 타입 처리 추가
        if (sourceType.equals("json") || sourceType.equals("jsonb")) {
            try {
                // JSON 문자열 이스케이프 처리
                return "'" + value.replace("'", "''")
                              .replace("\\", "\\\\") + "'";
            } catch (Exception e) {
                return "NULL";
            }
        }

        // PostgreSQL boolean을 MariaDB tinyint로 변환
        if (sourceType.equals("boolean")) {
            return value.toLowerCase().equals("true") ? "1" : "0";
        }

        // timestamp 처리
        if (sourceType.contains("timestamp")) {
            try {
                if (value.length() > 19) {
                    return "'" + value.substring(0, 19) + "'";
                }
                return "'" + value + "'";
            } catch (Exception e) {
                return "NULL";
            }
        }

        // 문자열 타입의 경우 이스케이프 처리
        if (sourceType.startsWith("character") || 
            sourceType.equals("text") || 
            sourceType.startsWith("varchar")) {
            return "'" + value.replace("'", "''") + "'";
        }

        // 날짜 타입
        if (sourceType.equals("date")) {
            return "'" + value + "'";
        }

        return value;
    }

    public static String convertDefaultValue(String defaultValue, String targetDbType) {
        if (defaultValue == null || defaultValue.isEmpty()) {
            return defaultValue;
        }

        // PostgreSQL의 타입 캐스팅(::) 제거
        if ("mariadb".equals(targetDbType)) {
            // 'something'::bpchar 형태를 'something'으로 변환
            if (defaultValue.contains("::")) {
                return defaultValue.substring(0, defaultValue.indexOf("::"));
            }
            // nextval 시퀀스는 이미 다른 곳에서 처리됨
        }
        
        return defaultValue;
    }
} 