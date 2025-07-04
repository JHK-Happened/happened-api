package com.happened.auth.dto;

import com.happened.user.domain.User;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    // 인증된 사용자 정보만 필요하므로 name, email, picture만 필드로 선언
    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}