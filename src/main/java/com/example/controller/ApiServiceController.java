package com.example.controller;

import com.example.service.ApiService;
import com.example.dto.api.ApiServiceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiServiceController {
    
    private static final Logger log = LoggerFactory.getLogger(ApiServiceController.class);
    private final ApiService apiService;
    
    @PostMapping("/code")
    public ApiServiceDto.CodeResponse getCode(@RequestBody ApiServiceDto.CodeRequest request) {
        log.info("Controller received request: {}", request);
        return apiService.getCode(request);
    }
    
    @PostMapping("/date-search")
    public ApiServiceDto.DateSearchResponse searchByDate(@RequestBody ApiServiceDto.DateSearchRequest request) {
        log.info("Controller received date search request: {}", request);
        return apiService.searchByDate(request);
    }
    
    @PostMapping("/product-search")
    public ApiServiceDto.ProductSearchResponse searchByProduct(@RequestBody ApiServiceDto.ProductSearchRequest request) {
        log.info("Controller received product search request: {}", request);
        return apiService.searchByProduct(request);
    }
    
    // API 엔드포인트들은 여기에 추가될 예정
} 