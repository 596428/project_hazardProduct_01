package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.dto.upload.FileUploadDto;
import java.util.List;
import java.util.Map;

@Mapper
public interface FileUploadMapper {
    void executeQuery(@Param("query") String query);
    
    void insertBulkData(
        @Param("tableName") String tableName,
        @Param("columns") List<FileUploadDto.ColumnDefinition> columns,
        @Param("dataList") List<Map<String, Object>> dataList
    );
    
    void insertUploadHistory(
        @Param("fileName") String fileName,
        @Param("totalRows") int totalRows,
        @Param("successRows") int successRows,
        @Param("userId") Integer userId
    );
} 