
package domain.service;

import data.repository.UserRepository;
import domain.user.User;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public User registerPatient(String loginId, String password,
                                String name, String phone, String email) {
        User user = new User(null, loginId, password, name,
                "PATIENT", phone, email);
        return userRepository.saveNewUser(user);
    }

    public Optional<User> login(String loginId, String password) {
        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            return userOpt;
        }
        return Optional.empty();
    }
}
