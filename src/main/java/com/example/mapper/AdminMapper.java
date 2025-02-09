package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    List<Map<String, Object>> getUsersWithApiKeys();
    
    // 사용자 권한 업데이트
    void updateUserRole(@Param("userId") Integer userId, @Param("roleCd") String roleCd);
    
    // API 키 만료일 업데이트
    void updateApiKeyExpiry(@Param("apiKey") String apiKey, @Param("expiryDate") String expiryDate);
} 