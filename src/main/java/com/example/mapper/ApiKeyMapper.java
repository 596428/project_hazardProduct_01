package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.dto.api.ApiKeyDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ApiKeyMapper {
    void insertPendingApiKey(
        @Param("userId") Integer userId, 
        @Param("apiKey") String apiKey, 
        @Param("keyName") String keyName,
        @Param("expiresAt") LocalDateTime expiresAt
    );
    
    List<ApiKeyDto.Response> findApiKeysByUserId(@Param("userId") Integer userId);
} 