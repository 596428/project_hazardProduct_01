package com.example.service.impl;

import com.example.service.ApiKeyService;
import com.example.mapper.ApiKeyMapper;
import com.example.mapper.UserMapper;
import com.example.domain.User;
import com.example.dto.api.ApiKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyMapper apiKeyMapper;
    private final UserMapper userMapper;

    @Override
    public void createPendingApiKey(String keyName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Integer userId = userMapper.findByEmail(email)
            .map(User::getUserId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // UUID를 사용하여 고유한 API 키 생성
        String apiKey = UUID.randomUUID().toString().replace("-", "");
        
        // 현재 시간으로부터 1주일 후 설정
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(168);
        
        apiKeyMapper.insertPendingApiKey(userId, apiKey, keyName, expiresAt);
    }

    @Override
    public List<ApiKeyDto.Response> getApiKeys() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Service - User Email: " + email);
        
        Integer userId = userMapper.findByEmail(email)
            .map(User::getUserId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        System.out.println("Service - User ID: " + userId);
        
        List<ApiKeyDto.Response> result = apiKeyMapper.findApiKeysByUserId(userId);
        System.out.println("Service - Query Result: " + result);
        return result;
    }
} 