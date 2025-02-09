package com.example.mapper;

import com.example.domain.DangerousGoods;
import com.example.dto.SearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DangerousGoodsMapper {
    List<DangerousGoods> searchDangerousGoods(SearchCondition condition);
    int countDangerousGoods(SearchCondition condition);
    List<DangerousGoods> getTop20Goods();
    String checkData();
    void updateField(Map<String, Object> change);
    List<DangerousGoods> getPaginatedGoods(Map<String, Object> params);
    int getTotalCount();
    void refreshView();
} 