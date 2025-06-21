package com.happened.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private Boolean isNewUser;
    // todo. 필요하다면 UserInfo DTO를 생성
}