package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.dto.user.UserDto;
import java.util.Optional;
import com.example.domain.User;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    boolean existsByEmail(String email);
    void insertUser(UserDto.Request request);
    Optional<User> findByEmail(String email);
    void updateLastLoginTime(Integer userId);
    Integer findUserIdByUserName(@Param("userName") String userName);
} 