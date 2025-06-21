package com.happened.global.config;

import com.happened.auth.token.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form Login 비활성화

                // 세션을 사용하지 않으므로 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 요청 경로에 대한 인가 설정
        http.authorizeHttpRequests(authz -> authz
                // --- 여기에 새로운 구글 로그인 콜백 경로를 permitAll()로 추가합니다 ---
                .requestMatchers("/api/auth/google/callback").permitAll()
                // 다른 경로는 기존 설정 유지 (예시)
                .requestMatchers("/api/test/**").hasRole("USER")
                .anyRequest().authenticated()
        );

        // --- 클라이언트 주도 방식에서는 서버의 OAuth2 로그인 설정이 필요 없습니다 ---
        // .oauth2Login(oauth2 -> oauth2
        //         .successHandler(...)
        //         .userInfoEndpoint(userInfo -> userInfo.userService(...))
        // ) // 이 부분을 삭제하거나 주석 처리합니다.

        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}