package com.example.service;

import com.example.dto.user.UserDto;

public interface UserService {
    void signup(UserDto.Request request);
    void login(UserDto.Login request);
} 