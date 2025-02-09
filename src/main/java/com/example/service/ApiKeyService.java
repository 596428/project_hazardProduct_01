package com.example.service;

import java.util.List;
import java.util.Map;

import com.example.dto.api.ApiKeyDto;

public interface ApiKeyService {
    void createPendingApiKey(String keyName);
    List<ApiKeyDto.Response> getApiKeys();
} 