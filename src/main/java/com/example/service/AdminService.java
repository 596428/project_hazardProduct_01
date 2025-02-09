package com.example.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Map<String, Object>> getUsersWithApiKeys();
    void updateUserRole(Integer userId, String roleCd);
    void updateApiKeyExpiry(String apiKey, String expiryDate);
} 