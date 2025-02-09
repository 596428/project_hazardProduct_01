package com.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.dto.common.ResponseDto;
import com.example.dto.user.UserDto;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import com.example.domain.User;
import com.example.mapper.UserMapper;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/api/users/signup")
    @ResponseBody
    public ResponseDto<String> signup(@RequestBody UserDto.Request request) {
        try {
            userService.signup(request);
            return ResponseDto.successMessage("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseDto.error(e.getMessage(), "SIGNUP_FAIL");
        }
    }

    @PostMapping("/api/users/login")
    @ResponseBody
    public ResponseDto<String> login(@RequestBody UserDto.Login request) {
        try {
            System.out.println("Login attempt with email: " + request.getEmail());
            userService.login(request);
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication after login: " + auth);
            System.out.println("Is authenticated: " + auth.isAuthenticated());
            System.out.println("Authorities: " + auth.getAuthorities());
            
            ResponseDto<String> response = ResponseDto.successMessage("로그인 성공");
            System.out.println("Login response structure: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return ResponseDto.error(e.getMessage(), "LOGIN_FAIL");
        }
    }

    @PostMapping("/api/users/logout")
    @ResponseBody
    public ResponseDto<String> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseDto.successMessage("로그아웃되었습니다.");
    }

    @GetMapping("/api/users/current")
    @ResponseBody
    public ResponseDto<UserDto.Response> getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                
            UserDto.Response response = new UserDto.Response();
            response.setEmail(user.getEmail());
            response.setUserName(user.getUserName());
            response.setRoleCd(user.getRoleCd());
            
            return ResponseDto.successMessage(response);
        } catch (Exception e) {
            return ResponseDto.error(e.getMessage(), "FETCH_USER_FAIL");
        }
    }
} 