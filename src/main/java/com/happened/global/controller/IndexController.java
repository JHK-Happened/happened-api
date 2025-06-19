package com.happened.global.controller;

import com.happened.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        // CustomOAuth2UserService에서 로그인 성공 시 세션에 저장해둔 SessionUser 호출
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) { // 세션에 사용자 정보(user)가 있을 경우
            model.addAttribute("userName", user.getName()); // 모델에 사용자 이름을 저장
        }

        // 머스테치 스타터가 컨트롤러에서 문자열을 반환할 때
        // 앞의 경로(src/main/resources/templates)와 뒤의 파일 확장자(.mustache)를 자동으로 붙여서 변환
        return "index"; // -> "src/ma+in/resources/templates/index.mustache"
    }
}