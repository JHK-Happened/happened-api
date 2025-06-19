package com.happened.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    /**
     * JWT 인증이 필요한 테스트용 엔드포인트입니다.
     *
     * @param authentication JwtAuthenticationFilter가 SecurityContext에 저장한 인증 정보
     * @return 인증된 사용자의 이메일과 권한
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 성공!");
        // TokenProvider에서 principal을 email로 설정했으므로, getName()으로 이메일을 가져올 수 있습니다.
        response.put("email", authentication.getName());
        response.put("role", authentication.getAuthorities().stream().findFirst()
                .map(a -> a.getAuthority())
                .orElse("NO_ROLE"));

        return ResponseEntity.ok(response);
    }
}
