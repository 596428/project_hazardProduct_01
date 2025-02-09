package com.example.service.impl;

import com.example.domain.DangerousGoods;
import com.example.dto.SearchCondition;
import com.example.mapper.DangerousGoodsMapper;
import com.example.service.DangerousGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class DangerousGoodsServiceImpl implements DangerousGoodsService {
    
    private final DangerousGoodsMapper dangerousGoodsMapper;

    @Override
    public List<DangerousGoods> searchDangerousGoods(SearchCondition condition) {
        return dangerousGoodsMapper.searchDangerousGoods(condition);
    }

    @Override
    public int countDangerousGoods(SearchCondition condition) {
        return dangerousGoodsMapper.countDangerousGoods(condition);
    }

    @Override
    public List<DangerousGoods> getTop20Goods() {
        return dangerousGoodsMapper.getTop20Goods();
    }

    @Transactional
    @Override
    public void updateGoods(List<Map<String, Object>> changes) {
        for (Map<String, Object> change : changes) {
            dangerousGoodsMapper.updateField(change);
        }
        dangerousGoodsMapper.refreshView();  // view refresh
    }

    @Override
    public Map<String, Object> getPaginatedGoods(int page) {
        int pageSize = 40;
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", pageSize);
        
        List<DangerousGoods> items = dangerousGoodsMapper.getPaginatedGoods(params);
        int totalCount = dangerousGoodsMapper.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        
        return result;
    }
} 