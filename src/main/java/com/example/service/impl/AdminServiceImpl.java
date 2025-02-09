package com.example.service.impl;

import org.springframework.stereotype.Service;
import com.example.service.AdminService;
import com.example.mapper.AdminMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    
    @Override
    public List<Map<String, Object>> getUsersWithApiKeys() {
        return adminMapper.getUsersWithApiKeys();
    }
    
    @Override
    public void updateUserRole(Integer userId, String roleCd) {
        adminMapper.updateUserRole(userId, roleCd);
    }
    
    @Override
    public void updateApiKeyExpiry(String apiKey, String expiryDate) {
        adminMapper.updateApiKeyExpiry(apiKey, expiryDate);
    }
} 