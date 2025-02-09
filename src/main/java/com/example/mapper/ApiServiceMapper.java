package com.example.mapper;

import java.util.List;
import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.dto.api.ApiServiceDto;

@Mapper
public interface ApiServiceMapper {
    // 매퍼 메서드들은 여기에 추가될 예정
    boolean isValidApiKey(@Param("apiKey") String apiKey);
    Integer getApiKeyId(@Param("apiKey") String apiKey);
    void insertCallHistory(ApiServiceDto.CallHistory history);
    List<ApiServiceDto.CodeData> getAllResCodes();
    ApiServiceDto.CodeData getResCode(@Param("resCode") String resCode);
    List<ApiServiceDto.DangerousGoodsInfo> searchDangerousGoodsByDate(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    List<ApiServiceDto.DangerousGoodsInfo> searchDangerousGoodsByProduct(
        @Param("productName") String productName,
        @Param("companyName") String companyName
    );
} 