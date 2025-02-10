package com.example.service.impl;

import com.example.service.ApiKeyService;
import com.example.mapper.ApiKeyMapper;
import com.example.mapper.UserMapper;
import com.example.domain.User;
import com.example.dto.api.ApiKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyMapper apiKeyMapper;
    private final UserMapper userMapper;
    private static final Logger log = LoggerFactory.getLogger(ApiKeyServiceImpl.class);

    @Override
    public void createPendingApiKey(String keyName, Integer userId) {
        log.info("Creating pending API key with name: {} for user: {}", keyName, userId);
        try {
            // UUID를 사용하여 고유한 API 키 생성
            String apiKey = UUID.randomUUID().toString().replace("-", "");
            
            // 현재 시간으로부터 1주일 후 설정
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(168);
            
            apiKeyMapper.insertPendingApiKey(userId, apiKey, keyName, expiresAt);
        } catch (Exception e) {
            log.error("Error in createPendingApiKey", e);
            throw e;
        }
    }

    @Override
    public List<ApiKeyDto> getApiKeys(Integer userId) {
        return apiKeyMapper.findByUserId(userId);
    }
} 