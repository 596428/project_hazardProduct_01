package com.example.dto.migration;

import lombok.Data;

@Data
public class ForeignKeyDto {
    private String constraintName;
    private String columnName;
    private String referencedTable;
    private String referencedColumn;
} 