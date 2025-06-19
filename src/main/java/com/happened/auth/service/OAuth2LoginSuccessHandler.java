package com.happened.auth.service;

import com.happened.auth.dto.SessionUser;
import com.happened.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.happened.auth.token.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final HttpSession httpSession;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        String jwt = tokenProvider.generateToken(authentication, sessionUser);
        String targetUrl = "happened://?token=" + jwt;

        log.info("발급된 JWT: {}", jwt);

        httpSession.removeAttribute("user");

        clearAuthenticationAttributes(request, response);

        response.sendRedirect(targetUrl);
    }

    // 인증 관련 쿠키를 정리하는 메소드
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
