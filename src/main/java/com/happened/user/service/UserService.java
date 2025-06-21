package com.happened.user.service;

import com.happened.user.domain.User;
import com.happened.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 이메일로 사용자가 존재하는지 확인합니다.
     * GoogleAuthService에서 사용합니다.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 이메일로 사용자를 조회합니다.
     * GoogleAuthService에서 사용합니다.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 새로운 사용자를 데이터베이스에 등록합니다. (회원가입)
     * GoogleAuthService에서 사용자가 존재하지 않을 때 호출합니다.
     */
    @Transactional
    public User registerNewUser(String email, String name) {
        User newUser = User.builder()
                .email(email)
                .name(name)
                // 필요하다면 초기 Role 등을 설정할 수 있습니다.
                .build();
        return userRepository.save(newUser);
    }
}
