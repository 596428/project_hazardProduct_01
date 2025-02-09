package com.example.dto;

import lombok.Data;

@Data
public class SearchCondition {
    private String keyword;
    private String inspInstCd;
    private String rptTypeCd;
    private String rtrvlRsnCd;
    private Integer page;
    private Integer size;
} 