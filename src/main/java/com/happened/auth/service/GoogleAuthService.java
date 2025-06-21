package com.happened.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.happened.auth.config.GoogleAuthProperties;
import com.happened.auth.dto.AuthResponse;
import com.happened.auth.token.TokenProvider;
import com.happened.user.domain.User;
import com.happened.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "google.auth")
public class GoogleAuthService {

    private final GoogleAuthProperties googleAuthProperties;
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @Transactional
    public AuthResponse loginOrRegister(String idTokenString) throws Exception {
        GoogleIdToken.Payload payload = verifyGoogleIdToken(idTokenString);
        if (payload == null) {
            throw new IllegalArgumentException("유효하지 않은 ID 토큰입니다.");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        boolean isNewUser = !userService.existsByEmail(email);
        User user = userService.findByEmail(email)
                .orElseGet(() -> userService.registerNewUser(email, name));

        String accessToken = tokenProvider.createToken(user.getId());

        return new AuthResponse(accessToken, isNewUser);
    }

    private GoogleIdToken.Payload verifyGoogleIdToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // --- (3) 주입받은 객체에서 client-id 목록을 가져오도록 변경
                .setAudience(googleAuthProperties.getClientIds())
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        return idToken != null ? idToken.getPayload() : null;
    }
}