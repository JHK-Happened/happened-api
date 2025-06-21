package com.happened.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happened.auth.dto.AuthResponse;
import com.happened.auth.dto.GoogleLoginRequest;
import com.happened.auth.service.GoogleAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // GoogleAuthService를 실제 객체가 아닌 가짜(Mock) 객체로 주입
    @MockBean
    private GoogleAuthService googleAuthService;

    @Test
    @DisplayName("구글 로그인 성공 시 JWT 토큰과 사용자 정보를 반환한다")
    void googleLoginSuccess() throws Exception {
        // given: 테스트 준비
        // 1. 클라이언트가 보낼 가짜 idToken을 준비
        String fakeIdToken = "fake-google-id-token-for-test";
        GoogleLoginRequest requestDto = new GoogleLoginRequest();
        // 실제 setter가 필요 GoogleLoginRequest에 public void setIdToken(String idToken) {} 추가
        // 혹은 리플렉션을 통해 값을 설정 가능 여기서는 직접 객체를 수정하는 것으로 가정
        // GoogleLoginRequest에 @Setter가 있다면 이 코드는 필요 X
        // (GoogleLoginRequest는 NoArgsConstructor만 있으므로, private 필드에 접근하기 위해 아래와 같이 임시 setter를 만들거나,
        // 테스트용 생성자를 추가하는 것이 좋음)
        // 여기서는 GoogleLoginRequest에 public setIdToken이 있다고 가정
        // requestDto.setIdToken(fakeIdToken); // -> GoogleLoginRequest에 setter가 없어서 컴파일 오류 발생 가능

        // 더 안정적인 방법: 테스트에서만 사용할 객체를 직접 생성
        String requestJson = "{\"idToken\":\"" + fakeIdToken + "\"}";


        // 2. googleAuthService.loginOrRegister 메서드가 호출되면,
        //    미리 정의된 AuthResponse를 반환하도록 설정
        String expectedAccessToken = "this-is-my-service-jwt-token";
        boolean isNewUser = true;
        AuthResponse expectedResponse = new AuthResponse(expectedAccessToken, isNewUser);

        given(googleAuthService.loginOrRegister(anyString()))
                .willReturn(expectedResponse);

        // when: 실제 테스트 실행
        // /api/auth/google/callback 엔드포인트로 POST 요청
        ResultActions resultActions = mockMvc.perform(post("/api/auth/google/callback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)); // objectMapper.writeValueAsString(requestDto)

        // then: 결과 검증
        resultActions
                .andExpect(status().isOk()) // 1. HTTP 상태코드가 200 OK인지 확인
                .andExpect(jsonPath("$.accessToken").value(expectedAccessToken)) // 2. 응답 JSON의 accessToken 필드 값 확인
                .andExpect(jsonPath("$.isNewUser").value(isNewUser)) // 3. 응답 JSON의 isNewUser 필드 값 확인
                .andDo(print()); // 요청/응답 전체 내용 로그 출력
    }

    @Test
    @DisplayName("유효하지 않은 ID 토큰으로 로그인 시 401 에러를 반환한다")
    void googleLoginFailWithInvalidToken() throws Exception {
        // given: 테스트 준비
        String invalidIdToken = "this-is-invalid-token";
        String requestJson = "{\"idToken\":\"" + invalidIdToken + "\"}";

        // googleAuthService가 IllegalArgumentException을 던지도록 설정
        given(googleAuthService.loginOrRegister(anyString()))
                .willThrow(new IllegalArgumentException("유효하지 않은 ID 토큰입니다."));

        // when: 실제 테스트 실행
        ResultActions resultActions = mockMvc.perform(post("/api/auth/google/callback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // then: 결과 검증
        resultActions
                .andExpect(status().isUnauthorized()) // HTTP 상태코드가 401 Unauthorized 인지 확인
                .andDo(print());
    }
}
