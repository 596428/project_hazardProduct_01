package com.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.dto.user.UserDto;
import com.example.service.UserService;
import com.example.mapper.UserMapper;
import com.example.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(UserDto.Request request) {
        // 이메일 중복 체크
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encodedPassword);
        
        // 회원 정보 저장
        userMapper.insertUser(request);
    }

    @Override
    public void login(UserDto.Login request) {
        User user = userMapper.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        System.out.println("Found user: " + user);  // 사용자 정보 로그
        System.out.println("User role: " + user.getRoleCd());  // 권한 정보 로그
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 Spring Security Context에 인증 정보 설정
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleCd()));
        System.out.println("Setting authorities: " + authorities);  // 권한 설정 로그
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getEmail(),
            null,
            authorities
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("SecurityContext authentication set: " + SecurityContextHolder.getContext().getAuthentication());  // 인증 정보 설정 확인

        userMapper.updateLastLoginTime(user.getUserId());
    }
}