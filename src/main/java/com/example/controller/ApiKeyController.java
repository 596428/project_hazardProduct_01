package com.example.controller;

import com.example.service.ApiKeyService;
import com.example.dto.api.ApiKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/create")
    public ResponseEntity<?> createApiKey(@RequestBody ApiKeyDto.Request request) {
        try {
            apiKeyService.createPendingApiKey(request.getKeyName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiKeyDto.ListResponse> getApiKeys() {
        try {
            List<ApiKeyDto.Response> apiKeys = apiKeyService.getApiKeys();
            System.out.println("Controller - API Keys: " + apiKeys);
            
            ApiKeyDto.ListResponse response = new ApiKeyDto.ListResponse();
            response.setSuccess(true);
            response.setData(apiKeys);
            
            System.out.println("Controller - Final Response: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiKeyDto.ListResponse response = new ApiKeyDto.ListResponse();
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }
    }
} 