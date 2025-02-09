package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.service.FileUploadService;
import com.example.mapper.FileUploadMapper;
import com.example.mapper.UserMapper;
import com.example.dto.upload.FileUploadDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadMapper fileUploadMapper;
    private final UserMapper userMapper;
    private final Set<String> ALLOWED_TYPES = Set.of(
        "varchar", "text", "integer", "bigint", "numeric", 
        "date", "timestamp", "boolean"
    );

    @Override
    public FileUploadDto.PreviewResponse previewExcelFile(MultipartFile file, int previewRows) throws Exception {
        FileUploadDto.PreviewResponse response = new FileUploadDto.PreviewResponse();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            // 헤더 처리
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }
            response.setHeaders(headers);
            
            // 데이터 처리
            List<List<String>> data = new ArrayList<>();
            int rowCount = sheet.getPhysicalNumberOfRows();
            
            for (int i = 1; i <= Math.min(previewRows, rowCount - 1); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    List<String> rowData = new ArrayList<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        rowData.add(getCellValueAsString(cell));
                    }
                    data.add(rowData);
                }
            }
            
            response.setData(data);
            response.setTotalRows(rowCount - 1);  // 헤더 제외
            
        } catch (Exception e) {
            throw new Exception("엑셀 파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return response;
    }

    @Override
    @Transactional
    public FileUploadDto.ImportResponse importExcelData(MultipartFile file, FileUploadDto.ImportRequest request) throws Exception {
        validateRequest(request);
        
        FileUploadDto.ImportResponse response = new FileUploadDto.ImportResponse();
        List<String> errorMessages = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 테이블 생성
            createTableIfNotExists(request.getTableName(), request.getColumns());
            
            // 데이터 삽입
            List<Map<String, Object>> dataList = new ArrayList<>();
            int startRow = request.getStartRow();
            int endRow = Math.min(request.getEndRow(), sheet.getLastRowNum());
            
            System.out.println("Processing rows from " + startRow + " to " + endRow);  // 로그 추가

            for (int i = startRow; i <= endRow; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, Object> rowData = new HashMap<>();
                    boolean hasData = false;  // 데이터 존재 여부 확인

                    for (FileUploadDto.ColumnDefinition col : request.getColumns()) {
                        Cell cell = row.getCell(col.getExcelIndex());
                        Object value = getTypedCellValue(cell, col.getType());
                        
                        // null이 아닌 경우에만 맵에 추가
                        if (value != null) {
                            hasData = true;
                        }
                        rowData.put(col.getName(), value);
                        
                        // 로그 추가
                        System.out.println("Column: " + col.getName() + 
                                        ", Excel Index: " + col.getExcelIndex() + 
                                        ", Value: " + value);
                    }

                    // 유효한 데이터가 있는 경우에만 추가
                    if (hasData) {
                        dataList.add(rowData);
                    }
                }
            }
            
            System.out.println("Total rows to insert: " + dataList.size());  // 로그 추가
            
            if (!dataList.isEmpty()) {
                fileUploadMapper.insertBulkData(
                    request.getTableName(),
                    request.getColumns(),  // ColumnDefinition 객체 전체를 전달
                    dataList
                );
                
                response.setSuccess(true);
                response.setProcessedRows(dataList.size());
            } else {
                throw new Exception("처리할 데이터가 없습니다.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during import: " + e.getMessage());
            // 원인 예외도 출력
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            e.printStackTrace();  // 전체 스택 트레이스 출력
            throw new Exception("데이터 이관 중 오류가 발생했습니다: " + 
                (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
        
        response.setErrorMessages(errorMessages);
        return response;
    }


    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toString();
                    }
                    // 과학적 표기법 방지
                    return String.format("%.0f", cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } catch (Exception e) {
            System.err.println("Error getting cell value as string: " + e.getMessage());
            return "";
        }
    }

    private Object getTypedCellValue(Cell cell, String type) {
        if (cell == null) return null;
        
        try {
            switch (type.toLowerCase()) {
                case "varchar":
                case "text":
                    // 숫자도 문자열로 변환
                    if (cell.getCellType() == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            return cell.getLocalDateTimeCellValue().toString();
                        }
                        // 과학적 표기법 방지를 위해 DecimalFormat 사용
                        return String.format("%.0f", cell.getNumericCellValue());
                    }
                    return cell.getStringCellValue();

                case "integer":
                case "bigint":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        // 소수점 제거하고 정수로 변환
                        return (long) cell.getNumericCellValue();
                    }
                    return Long.parseLong(getCellValueAsString(cell).split("\\.")[0]);

                case "numeric":
                    if (cell.getCellType() == CellType.NUMERIC) {
                        double value = cell.getNumericCellValue();
                        // 정수인 경우 정수로 반환
                        if (value == Math.floor(value)) {
                            return (long) value;
                        }
                        return value;
                    }
                    return Double.parseDouble(getCellValueAsString(cell));

                case "date":
                case "timestamp":
                    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue();
                    }
                    String dateStr = getCellValueAsString(cell);
                    return dateStr.isEmpty() ? null : LocalDateTime.parse(dateStr);

                case "boolean":
                    if (cell.getCellType() == CellType.BOOLEAN) {
                        return cell.getBooleanCellValue();
                    }
                    String boolStr = getCellValueAsString(cell).toLowerCase();
                    return boolStr.equals("true") || boolStr.equals("1");

                default:
                    return getCellValueAsString(cell);
            }
        } catch (Exception e) {
            System.err.println("Error converting cell value for type " + type + 
                             ": " + e.getMessage() + 
                             ", Cell content: " + getCellValueAsString(cell));
            return null;
        }
    }

    private void validateRequest(FileUploadDto.ImportRequest request) {
        if (!isValidIdentifier(request.getTableName())) {
            throw new IllegalArgumentException("유효하지 않은 테이블명입니다");
        }
        
        for (FileUploadDto.ColumnDefinition col : request.getColumns()) {
            if (!isValidIdentifier(col.getName())) {
                throw new IllegalArgumentException("유효하지 않은 컬럼명입니다: " + col.getName());
            }
            
            String baseType = col.getType().toLowerCase().split("\\(")[0];
            if (!ALLOWED_TYPES.contains(baseType)) {
                throw new IllegalArgumentException("허용되지 않은 데이터 타입입니다: " + col.getType());
            }
        }
    }

    private boolean isValidIdentifier(String identifier) {
        return identifier != null && identifier.matches("^[a-zA-Z][a-zA-Z0-9_]*$");
    }

    private void createTableIfNotExists(String tableName, List<FileUploadDto.ColumnDefinition> columns) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        
        for (int i = 0; i < columns.size(); i++) {
            FileUploadDto.ColumnDefinition col = columns.get(i);
            query.append(col.getName()).append(" ").append(col.getType());
            if (i < columns.size() - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        
        fileUploadMapper.executeQuery(query.toString());
    }
} 