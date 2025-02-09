package com.example.service;

import com.example.dto.api.ApiServiceDto;

public interface ApiService {
    // 서비스 메서드들은 여기에 추가될 예정
    ApiServiceDto.CodeResponse getCode(ApiServiceDto.CodeRequest request);
    ApiServiceDto.DateSearchResponse searchByDate(ApiServiceDto.DateSearchRequest request);
    ApiServiceDto.ProductSearchResponse searchByProduct(ApiServiceDto.ProductSearchRequest request);
} 