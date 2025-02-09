package com.example.dto.upload;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FileUploadDto {
    
    @Data
    public static class PreviewRequest {
        private Integer previewRows;
    }
    
    @Data
    public static class PreviewResponse {
        private List<String> headers;
        private List<List<String>> data;
        private int totalRows;
    }
    
    @Data
    public static class ColumnDefinition {
        private String name;        // 컬럼명
        private String type;        // 데이터 타입
        private Integer excelIndex; // 엑셀 열 번호
    }
    
    @Data
    public static class ImportRequest {
        private String tableName;
        private List<ColumnDefinition> columns;
        private Integer startRow;
        private Integer endRow;
    }
    
    @Data
    public static class ImportResponse {
        private boolean success;
        private int processedRows;
        private List<String> errorMessages;
    }
} 