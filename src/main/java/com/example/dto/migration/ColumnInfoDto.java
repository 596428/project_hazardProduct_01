package com.example.dto.migration;

import lombok.Data;

@Data
public class ColumnInfoDto {
    private String columnName;
    private String dataType;
    private String constraints;  // PK/FK
    private String nullable;
    private String defaultValue;
} 