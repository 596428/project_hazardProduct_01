package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // API 요청을 위해 CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                // API 엔드포인트 허용
                .requestMatchers("/api/v1/**").permitAll()  // API 요청 허용
                .requestMatchers("/login", "/signup", "/css/**", "/js/**").permitAll()
                .requestMatchers("/test02", "/test04").hasAuthority("ROLE_01")  // test05 -> test04
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()  // POST 요청 명시적 허용
                .requestMatchers("/api/users/signup").permitAll()  // 회원가입 API는 인증 없이 접근 가능
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)  // 세션 생성 정책 변경
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/test01")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("Access Denied: " + accessDeniedException.getMessage());  // 디버깅 로그 추가
                    System.out.println("Current Authentication: " + SecurityContextHolder.getContext().getAuthentication());  // 디버깅 로그 추가
                    response.sendRedirect("/test01");  // 권한 없는 접근 시 메인페이지로 리다이렉트
                })
            )
            // Thymeleaf가 Security 컨텍스트에 접근할 수 있도록 설정
            .securityContext(context -> context
                .requireExplicitSave(false)
                .securityContextRepository(new HttpSessionSecurityContextRepository())
            )
            .addFilterAfter(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, 
                                              HttpServletResponse response, 
                                              FilterChain filterChain) throws ServletException, IOException {
                    if (SecurityContextHolder.getContext().getAuthentication() != null && 
                        SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                        request.getRequestURI().equals("/api/users/login")) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\":\"SUCCESS\",\"data\":\"로그인 성공\"}");
                        return;
                    }
                    filterChain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
} 