package com.example.controller;

import com.example.service.ApiKeyService;
import com.example.dto.api.ApiKeyDto;
import com.example.dto.user.UserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private static final Logger log = LoggerFactory.getLogger(ApiKeyController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createApiKey(@RequestBody ApiKeyDto.Request request, 
                                        @AuthenticationPrincipal UserDto userDto) {
        try {
            log.info("Received create API key request: {}", request);
            log.info("User info: {}", userDto);
            
            if (userDto == null) {
                return ResponseEntity.badRequest().body("인증 정보가 없습니다.");
            }
            
            apiKeyService.createPendingApiKey(request.getKeyName(), userDto.getUserId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating API key", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getApiKeys(@AuthenticationPrincipal UserDto userDto) {
        try {
            if (userDto == null) {
                return ResponseEntity.badRequest().body("인증 정보가 없습니다.");
            }
            log.info("User ID: " + userDto.getUserId());
            List<ApiKeyDto> apiKeys = apiKeyService.getApiKeys(userDto.getUserId());
            return ResponseEntity.ok(apiKeys);
        } catch (Exception e) {
            log.error("Error getting API keys", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 