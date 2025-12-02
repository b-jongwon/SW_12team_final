
package presentation.controller;

import domain.service.AuthService;
import domain.user.User;

import java.util.Optional;

public class AuthController {

    private final AuthService authService = new AuthService();

    public User registerPatient(String loginId, String pw,
                                String name, String phone, String email) {
        return authService.registerPatient(loginId, pw, name, phone, email);
    }

    public Optional<User> login(String loginId, String pw) {
        return authService.login(loginId, pw);
    }
}
