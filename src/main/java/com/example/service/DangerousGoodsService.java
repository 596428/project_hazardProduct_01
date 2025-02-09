package com.example.service;

import com.example.domain.DangerousGoods;
import com.example.dto.SearchCondition;
import java.util.List;
import java.util.Map;

public interface DangerousGoodsService {
    List<DangerousGoods> searchDangerousGoods(SearchCondition condition);
    int countDangerousGoods(SearchCondition condition);
    List<DangerousGoods> getTop20Goods();
    void updateGoods(List<Map<String, Object>> changes);
    Map<String, Object> getPaginatedGoods(int page);
} 