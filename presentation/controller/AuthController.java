package presentation.controller;

import domain.service.AuthService;
import domain.user.User;
import java.util.Optional;

public class AuthController {

    private final AuthService authService = new AuthService();

    // 3개의 정보만 입력받아도, 나머지는 빈 값("")으로 채워서 가입시키는 메서드
    public User register(String loginId, String pw, String name, String role) {
        // 현재 GUI에는 전화번호(phone), 이메일(email) 입력칸이 없으므로 빈 문자열 전달
        return authService.registerUser(loginId, pw, name, role, "", "");
    }

    public Optional<User> login(String loginId, String pw) {
        return authService.login(loginId, pw);
    }
}