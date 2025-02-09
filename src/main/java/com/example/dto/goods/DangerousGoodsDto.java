package com.example.dto.goods;

import lombok.Data;
import java.util.List;
import java.util.Map;
import com.example.domain.DangerousGoods;

public class DangerousGoodsDto {
    
    @Data
    public static class SearchCondition {
        private String keyword;
        private String inspInstCd;
        private String rptTypeCd;
        private String rtrvlRsnCd;
        private Integer page;
        private Integer size;
        private String startDate;
        private String endDate;
        private String sortField;
        private String sortDirection;
    }
    
    @Data
    public static class SearchResponse {
        private List<DangerousGoods> items;
        private int totalCount;
        private int currentPage;
        private int totalPages;
    }
    
    @Data
    public static class ModifyRequest {
        private String mstId;
        private String mstDataId;
        private Map<String, Object> updateFields;
    }
}