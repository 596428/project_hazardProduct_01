package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import com.example.service.AdminService;
import com.example.dto.admin.AdminDto;
import com.example.dto.common.ResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseDto<List<Map<String, Object>>> getUsersWithApiKeys() {
        try {
            List<Map<String, Object>> users = adminService.getUsersWithApiKeys();
            System.out.println("Admin users data: " + users);  // 데이터 로그
            return ResponseDto.successMessage(users);
        } catch (Exception e) {
            System.out.println("Error in getUsersWithApiKeys: " + e);  // 에러 로그
            e.printStackTrace();  // 스택 트레이스
            return ResponseDto.error(e.getMessage(), "FETCH_USERS_FAIL");
        }
    }
    
    @PostMapping("/users/role")
    public ResponseDto<String> updateUserRole(@RequestBody AdminDto.RoleUpdateRequest request) {
        try {
            adminService.updateUserRole(request.getUserId(), request.getRoleCd());
            return ResponseDto.successMessage("권한이 변경되었습니다.");
        } catch (Exception e) {
            return ResponseDto.error(e.getMessage(), "UPDATE_ROLE_FAIL");
        }
    }
    
    @PostMapping("/keys/expiry")
    public ResponseDto<String> updateApiKeyExpiry(@RequestBody AdminDto.ApiKeyExpiryUpdateRequest request) {
        try {
            System.out.println("Received request: " + request);  // 요청 데이터 로그
            adminService.updateApiKeyExpiry(request.getApiKey(), request.getExpiryDate());
            return ResponseDto.successMessage("만료일이 변경되었습니다.");
        } catch (Exception e) {
            System.out.println("Error updating expiry: " + e);  // 에러 로그
            e.printStackTrace();
            return ResponseDto.error(e.getMessage(), "UPDATE_EXPIRY_FAIL");
        }
    }
} 