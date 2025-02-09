package com.example.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
 import org.springframework.stereotype.Service;

import com.example.domain.User;
import com.example.dto.user.UserDto;
import com.example.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google or kakao
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute(provider.equals("google") ? "name" : "nickname");
        
        // DB에서 이메일로 사용자 조회
        Optional<User> existingUser = userMapper.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            // 신규 사용자면 DB에 저장
            UserDto.Request request = new UserDto.Request();
            request.setEmail(email);
            request.setUserName(name);
            
            userService.signup(request);
        }
        
        return oauth2User;
    }
} 